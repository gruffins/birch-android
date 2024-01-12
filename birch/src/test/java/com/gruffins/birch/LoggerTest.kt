package com.gruffins.birch

import android.content.Context
import com.gruffins.birch.utils.TestExecutorService
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowStatFs
import java.io.File
import java.security.KeyPairGenerator

@RunWith(RobolectricTestRunner::class)
class LoggerTest {

    private lateinit var context: Context
    private lateinit var agent: Agent
    private lateinit var logger: Logger
    private lateinit var currentFile: File
    private lateinit var storage: Storage

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        agent = Agent("birch")
        storage = Storage(context, agent.directory, Level.ERROR)
        logger = Logger(context, storage, agent, null, TestExecutorService())
        currentFile = File(logger.directory, "current")

        ShadowStatFs.registerStats(logger.directory, 1, 1, 1)
    }

    @After
    fun teardown() {
        logger.directory.deleteRecursively()
        ShadowStatFs.reset()
    }

    @Test
    fun `agent#level() overrides server configuration`() {
        agent.level = Level.TRACE
        logger.level = Level.NONE
        logger.log(Level.TRACE, { "a" }, { "a" })
        assert(currentFile.exists())
    }

    @Test
    fun `logger skips logs lower than the current log level`() {
        logger.level = Level.NONE
        logger.log(Level.TRACE, { "a" }, { "a" })
        assert(!currentFile.exists())
    }

    @Test
    fun `logger does not skip logs higher than the current log level`() {
        logger.level = Level.TRACE
        logger.log(Level.TRACE, { "a" }, { "a" })
        assert(currentFile.exists())
    }

    @Test
    fun `rollFile() moves the current file and creates a new current file`() {
        logger.rollFile()
        assert(logger.directory.list()?.size == 2)
    }

    @Test
    fun `logger skips logging if disk is full`() {
        ShadowStatFs.registerStats(logger.directory, 1, 0, 0)
        assert(!logger.log(Level.TRACE, { "a" }, { "a" }))
    }

    @Test
    fun `log() with console works at all levels`() {
        agent.console = true
        logger.level = Level.TRACE

        var count = 0
        val callback = {
            count += 1
            ""
        }

        logger.log(Level.TRACE, { "test" }, callback)
        logger.log(Level.DEBUG, { "test" }, callback)
        logger.log(Level.INFO, { "test" }, callback)
        logger.log(Level.WARN, { "test" }, callback)
        logger.log(Level.ERROR, { "test" }, callback)
        logger.log(Level.NONE, { "test" }, callback)

        assert(count == 5)
    }

    @Test
    fun `log() with encryption encrypts the logs in the file`() {
        val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

        logger = Logger(context, storage, agent, Encryption(keyPair.public), TestExecutorService())
        logger.level = Level.TRACE
        logger.log(Level.TRACE, { "a" }, { "a" })

        val content = currentFile.readText()
        assert(content.contains("em"))
        assert(content.contains("ek"))
    }

    @Test
    fun `log() without encryption does not encrypt logs in the file`() {
        logger.level = Level.TRACE
        logger.log(Level.TRACE, { "a" }, { "a" })

        val content = currentFile.readText()
        assert(!content.contains("em"))
        assert(!content.contains("ek"))
    }

    @Test
    fun `log() without remote enabled does not write to file`() {
        agent.remote = false
        logger.level = Level.TRACE
        logger.log(Level.TRACE, { "a" }, { "a" })

        assert(currentFile.readText().isBlank())
    }

    @Test
    fun `log() with remote enabled writes to file`() {
        logger.level = Level.TRACE
        logger.log(Level.TRACE, { "a" }, { "a" })

        assert(currentFile.readText().isNotBlank())
    }

    @Test
    fun `log() works synchronously`() {
        logger.level = Level.TRACE
        agent.synchronous = true
        logger.log(Level.TRACE, { "a" }, { "a" })
        assert(currentFile.readText().isNotBlank())
    }
    @Test
    fun `currentLevel() factors in agent override`() {
        agent.level = Level.TRACE
        storage.logLevel = Level.ERROR
        assert(logger.currentLevel == Level.TRACE)
    }

    @Test
    fun `currentLevel() returns the storage level`() {
        agent.level = null
        storage.logLevel = Level.ERROR
        assert(logger.currentLevel == Level.ERROR)
    }

}