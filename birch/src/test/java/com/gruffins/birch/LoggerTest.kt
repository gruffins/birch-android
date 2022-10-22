package com.gruffins.birch

import android.content.Context
import com.gruffins.birch.utils.TestExecutorService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class LoggerTest {

    private lateinit var context: Context
    private lateinit var logger: Logger
    private lateinit var directory: File
    private lateinit var currentFile: File
    private lateinit var storage: Storage

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        storage = Storage(context)
        logger = Logger(context, storage, TestExecutorService())
        directory = File(context.filesDir, Logger.DIRECTORY)
        currentFile = File(directory, "current")
    }

    @After
    fun teardown() {
        directory.deleteRecursively()
        Birch.debug = false
    }

    @Test
    fun `debug mode logs any level`() {
        Birch.debug = true

        logger.level = Logger.Level.NONE
        logger.log(Logger.Level.TRACE, { "a" }, { "a" })
        assert(currentFile.exists())
    }

    @Test
    fun `logger skips logs lower than the current log level`() {
        logger.level = Logger.Level.NONE
        logger.log(Logger.Level.TRACE, { "a" }, { "a" })
        assert(!currentFile.exists())
    }

    @Test
    fun `logger does not skip logs higher than the current log level`() {
        logger.level = Logger.Level.TRACE
        logger.log(Logger.Level.TRACE, { "a" }, { "a" })
        assert(currentFile.exists())
    }

    @Test
    fun `rollFile moves the current file and creates a new current file`() {
        logger.rollFile()
        assert(directory.list()?.size == 2)
    }

    @Test
    fun `trimFiles clears logs older than the max age`() {
        logger.rollFile()
        val timestamp = System.currentTimeMillis() + Logger.MAX_AGE_SECONDS * 1000L + 1L
        logger.trimFiles(timestamp)
        assert(directory.list()?.size == 1)
    }
}