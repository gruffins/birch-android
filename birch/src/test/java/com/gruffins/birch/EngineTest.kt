package com.gruffins.birch

import android.content.Context
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@MockKExtension.ConfirmVerification
class EngineTest {

    private lateinit var agent: Agent

    @MockK(relaxed = true)
    private lateinit var source: Source

    @MockK(relaxed = true)
    private lateinit var logger: Logger

    @MockK(relaxed = true)
    private lateinit var network: Network

    @MockK(relaxed = true)
    lateinit var executor: ScheduledExecutorService

    private lateinit var eventBus: EventBus
    private lateinit var engine: Engine
    private lateinit var context: Context
    private lateinit var storage: Storage

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        context = RuntimeEnvironment.getApplication()
        agent = Agent("birch").also {
            it.debug = true
        }
        eventBus = EventBus()
        storage = Storage(context, "birch", Level.ERROR)
        engine = spyk(
            Engine(
                agent,
                source,
                logger,
                storage,
                network,
                executor,
                eventBus,
                listOf(PasswordScrubber(), EmailScrubber())
            )
        )
        agent.engine = engine
    }

    @Test
    fun `queues all jobs`() {
        engine.start()

        verifyAll {
            executor.scheduleAtFixedRate(any(), 5,Engine.TRIM_PERIOD_SECONDS, TimeUnit.SECONDS)
            executor.scheduleAtFixedRate(any(), 15, Engine.FLUSH_PERIOD_SECONDS, TimeUnit.SECONDS)
            executor.scheduleAtFixedRate(any(), 10, Engine.SYNC_PERIOD_SECONDS, TimeUnit.SECONDS)
            executor.execute(any())
        }
    }

    @Test
    fun `log() forwards to the logger`() {
        engine.log(Level.TRACE) { "test" }
        verify { logger.log(Level.TRACE, any(), any()) }
    }

    @Test
    fun `log() returns true if not opted out`() {
        assert(engine.log(Level.TRACE) { "test" })
    }

    @Test
    fun `log() returns false if opted out`() {
        agent.optOut = true
        assert(!engine.log(Level.TRACE) { "test" })
    }

    @Test
    fun `log() applies scrubbers`() {
        val block = slot<() -> String>()
        val jsonBlock = slot<() -> String>()

        every { source.toJson() } returns JSONObject()
        every { logger.log(any(), capture(jsonBlock), capture(block)) } returns true

        engine.log(Level.TRACE) { "https://birch.ryanfung.com/?email=asdf+fdsa@domain.com&password=password123" }

        assert(block.captured.invoke() == "https://birch.ryanfung.com/?email=[FILTERED]&password=[FILTERED]")
        assert(jsonBlock.captured.invoke().contains("?email=[FILTERED]&password=[FILTERED]"))
    }

    @Test
    fun `flush() calls logger to roll file`() {
        engine.flushSynchronous()
        verify { logger.rollFile() }
    }

    @Test
    fun `flush() calls network to upload logs`() {
        val file = mockk<File>(relaxed = true)

        every { file.length() } returns 1L
        every { logger.nonCurrentFiles() } returns listOf(file)

        engine.flushSynchronous()

        verify { network.uploadLogs(file, any()) }
    }

    @Test
    fun `flush() uploads the logs and deletes the file`() {
        val file = mockk<File>(relaxed = true)

        every { logger.nonCurrentFiles() } returns listOf(file)
        every { file.length() } returns 0L

        engine.flushSynchronous()

        verify { file.delete() }
        verify(exactly = 0) { network.uploadLogs(file, any()) }
    }

    @Test
    fun `flush() async`() {
        engine.flush()
        verify { executor.execute(any()) }
    }

    @Test
    fun `flush() returns true if not opted out`() {
        assert(engine.flushSynchronous())
    }

    @Test
    fun `flush() returns false if opted out`() {
        agent.optOut = true
        assert(!engine.flushSynchronous())
    }

    @Test
    fun `flush() deletes files on success`() {
        val file = mockk<File>(relaxed = true)

        every { file.length() } returns 1L
        every { logger.nonCurrentFiles() } returns listOf(file)
        every { network.uploadLogs(file, any()) } answers { secondArg<(Boolean) -> Unit>().invoke(true) }

        engine.flushSynchronous()

        verify { file.delete() }
    }

    @Test
    fun `updateSource() calls network to sync source`() {
        engine.updateSourceSynchronous(source)
        verify { network.syncSource(source) }
    }

    @Test
    fun `updateSource() returns true if not opted out`() {
        assert(engine.updateSourceSynchronous(source))
    }

    @Test
    fun `updateSource() returns false if opted out`() {
        agent.optOut = true
        assert(!engine.updateSourceSynchronous(source))
    }

    @Test
    fun `updateSource() async`() {
        engine.updateSource(source)
        verify { executor.execute(any()) }
    }

    @Test
    fun `syncConfiguration() sets the log level on storage, level on logger and flush period on storage`() {
        val level = Level.TRACE
        val flushPeriod = 1L
        every { network.getConfiguration(source, any()) } answers {
            val json = JSONObject().also {
                it.put("log_level", level.level)
                it.put("flush_period_seconds", flushPeriod)
            }
            secondArg<(JSONObject) -> Unit>().invoke(json)
        }

        engine.syncConfigurationSynchronous()

        verifyAll {
            storage.flushPeriod
            storage setProperty "logLevel" value level
            logger setProperty  "level" value level
            storage setProperty "flushPeriod" value flushPeriod
            logger.log(Level.DEBUG, any(), any())
        }
    }

    @Test
    fun `syncConfiguration() async`() {
        engine.syncConfiguration()
        verify { executor.execute(any()) }
    }

    @Test
    fun `syncConfiguration() returns true if not opted out`() {
        assert(engine.syncConfigurationSynchronous())
    }

    @Test
    fun `syncConfiguration() returns false if opted out`() {
        agent.optOut = true
        assert(!engine.syncConfigurationSynchronous())
    }

    @Test
    fun `trimFiles removes old files`() {
        val directory = File(context.filesDir, agent.directory).also { it.mkdirs() }
        val file = File(directory, System.currentTimeMillis().toString()).also { it.createNewFile() }
        val timestamp = System.currentTimeMillis() + Engine.MAX_FILE_AGE_SECONDS * 1000L + 1L

        every { logger.nonCurrentFiles() } returns listOf(file)

        engine.trimFilesSynchronous(timestamp)

        assert(!file.exists())
    }

    @Test
    fun `trimFiles async`() {
        engine.trimFiles()
        verify { executor.execute(any()) }
    }
}