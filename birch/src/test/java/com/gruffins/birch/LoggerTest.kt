package com.gruffins.birch

import android.content.Context
import com.gruffins.birch.utils.TestExecutorService
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var logger: Logger
    private lateinit var currentFile: File
    private lateinit var storage: Storage

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        storage = Storage(context)
        logger = Logger(context, storage, null, TestExecutorService())
        currentFile = File(logger.directory, "current")

        ShadowStatFs.registerStats(logger.directory, 1, 1, 1)
    }

    @After
    fun teardown() {
        logger.directory.deleteRecursively()
        Birch.debug = false
        Birch.remote = true
        Birch.console = false
        Birch.level = null
        Birch.synchronous = false
        ShadowStatFs.reset()
    }

    @Test
    fun `Birch#level() overrides server configuration`() {
        Birch.level = Level.TRACE
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
        Birch.console = true
        logger.level = Level.TRACE

        val block = mockk<() -> String>()
        every { block.invoke() } returns "test"

        logger.log(Level.TRACE, { "test" }, block)
        logger.log(Level.DEBUG, { "test" }, block)
        logger.log(Level.INFO, { "test" }, block)
        logger.log(Level.WARN, { "test" }, block)
        logger.log(Level.ERROR, { "test" }, block)
        logger.log(Level.NONE, { "test" }, block)

        verify(exactly = 5) { block.invoke() }
    }

    @Test
    fun `log() with encryption encrypts the logs in the file`() {
        val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

        logger = Logger(context, storage, Encryption(keyPair.public), TestExecutorService())
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
        Birch.remote = false
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
        Birch.synchronous = true
        logger.log(Level.TRACE, { "a" }, { "a" })
        assert(currentFile.readText().isNotBlank())
    }
}