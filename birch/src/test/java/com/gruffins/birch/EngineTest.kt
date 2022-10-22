package com.gruffins.birch

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@MockKExtension.ConfirmVerification
class EngineTest {

    @MockK(relaxed = true)
    private lateinit var source: Source

    @MockK(relaxed = true)
    private lateinit var logger: Logger

    @MockK(relaxed = true)
    private lateinit var storage: Storage

    @MockK(relaxed = true)
    private lateinit var network: Network

    @MockK(relaxed = true)
    lateinit var executor: ScheduledExecutorService

    private lateinit var eventBus: EventBus
    private lateinit var engine: Engine

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        eventBus = EventBus()
        engine = spyk(Engine(source, logger, storage, network, executor, eventBus))
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
    fun `engine forwards log calls to logger`() {
        engine.log(Logger.Level.TRACE) { "test" }
        verify { logger.log(Logger.Level.TRACE, any(), any()) }
    }

    @Test
    fun `flush calls logger to roll file`() {
        engine.flush()
        verify { logger.rollFile() }
    }

    @Test
    fun `flush calls network to upload logs`() {
        val file = mockk<File>(relaxed = true)

        every { file.length() } returns 1L
        every { logger.nonCurrentFiles() } returns listOf(file)

        engine.flush()

        verify { network.uploadLogs(file, any()) }
    }

    @Test
    fun `calling flush twice does not upload the logs twice`() {
        val file = mockk<File>(relaxed = true)

        every { file.length() } returns 1L
        every { logger.nonCurrentFiles() } returns listOf(file)

        repeat(2) { engine.flush() }

        verify(exactly = 1) { network.uploadLogs(file, any()) }
    }

    @Test
    fun `flush uploads the logs and deletes the file`() {
        val file = mockk<File>(relaxed = true)

        every { logger.nonCurrentFiles() } returns listOf(file)
        every { file.length() } returns 0L

        engine.flush()

        verify { file.delete() }
        verify(exactly = 0) { network.uploadLogs(file, any()) }
    }

    @Test
    fun `flush deletes files on success`() {
        val file = mockk<File>(relaxed = true)

        every { file.length() } returns 1L
        every { logger.nonCurrentFiles() } returns listOf(file)
        every { network.uploadLogs(file, any()) } answers { secondArg<(Boolean) -> Unit>().invoke(true) }

        engine.flush()

        verify { file.delete() }
    }

    @Test
    fun `updateSource calls network to sync source`() {
        engine.updateSource(source)
        verify { network.syncSource(source) }
    }

    @Test
    fun `sync configuration sets the log level on storage, level on logger and flush period on storage`() {
        val level = Logger.Level.TRACE
        val flushPeriod = 1L
        every { network.getConfiguration(source, any()) } answers {
            val json = JSONObject().also {
                it.put("log_level", level.level)
                it.put("flush_period_seconds", flushPeriod)
            }
            secondArg<(JSONObject) -> Unit>().invoke(json)
        }

        engine.syncConfiguration()

        verifyAll {
            storage.flushPeriod
            storage setProperty "logLevel" value level
            logger setProperty  "level" value level
            storage setProperty "flushPeriod" value flushPeriod
        }
    }
    @Test
    fun `trimFiles calls the logger to trim files`() {
        engine.trimFiles()
        verify { logger.trimFiles(any()) }
    }

    @Test
    fun `overriding the flush period takes priority over storage settings`() {
        storage.flushPeriod = 0
        engine.overrideFlushPeriod = 2

        verify { executor.scheduleAtFixedRate(any(), 0, 2, TimeUnit.SECONDS) }
    }

    @Test
    fun `unsetting override flush period reverts to storage settings`() {
        storage.flushPeriod = 0
        engine.overrideFlushPeriod = 2
        engine.overrideFlushPeriod = null

        verify { executor.scheduleAtFixedRate(any(), 0, 0, TimeUnit.SECONDS) }
    }
}