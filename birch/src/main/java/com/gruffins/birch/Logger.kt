package com.gruffins.birch

import android.content.Context
import android.util.Log
import com.gruffins.birch.Utils.Companion.safe
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.System.currentTimeMillis
import java.lang.Thread.currentThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class Logger(
    context: Context,
    storage: Storage,
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, THREAD_NAME) }
) {
    companion object {
        const val THREAD_NAME = "Birch-Logger"
        const val DIRECTORY = "birch"
        const val MAX_FILE_SIZE_BYTES = 1024 * 512 * 1
        const val MAX_AGE_SECONDS = 60L * 60L * 60L * 3
    }

    enum class Level(val level: Int) {
        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        NONE(5);

        companion object {
            fun fromInt(level: Int) = values().first { it.level == level }
        }
    }

    var level: Level = storage.logLevel

    private val directory = File(context.filesDir, DIRECTORY)
    private var currentFile = File(directory, "current")
    private var fileWriter: FileWriter? = null
    private var bufferedWriter: BufferedWriter? = null

    init {
        directory.mkdirs()
    }

    fun log(level: Level, block: () -> String, original: () -> String) {
        if (level >= this.level || Birch.debug) {
            executorService.execute {
                safe {
                    ensureCurrentFileExists()

                    if (fileWriter == null) {
                        fileWriter = FileWriter(currentFile, true)
                        bufferedWriter = BufferedWriter(fileWriter)
                    }

                    bufferedWriter?.write(block() + ",\n")

                    if (Birch.debug) {
                        when (level) {
                            Level.TRACE -> Log.v("Birch", original())
                            Level.DEBUG -> Log.d("Birch", original())
                            Level.INFO -> Log.i("Birch", original())
                            Level.WARN -> Log.w("Birch", original())
                            Level.ERROR -> Log.e("Birch", original())
                            Level.NONE -> Unit
                        }
                    }

                    if (needsRollFile()) {
                        rollFile()
                    }
                }
            }
        }
    }

    fun nonCurrentFiles(): List<File>? {
        return directory.listFiles { _, name -> "current" != name }?.toList()
    }

    fun rollFile() {
        val block = {
            ensureCurrentFileExists()

            val timestamp = currentTimeMillis().toString()
            val rollTo = File(directory, timestamp)

            bufferedWriter?.close()
            fileWriter?.close()
            currentFile.renameTo(rollTo)

            currentFile = File(directory, "current")
            currentFile.createNewFile()

            fileWriter = FileWriter(currentFile, true)
            bufferedWriter = BufferedWriter(fileWriter)
        }

        if (isLoggerThread()) {
            block()
        } else {
            executorService.submit(block).get()
        }
    }

    fun trimFiles(now: Long = currentTimeMillis()) {
        val timestamp = now - MAX_AGE_SECONDS * 1000L
        nonCurrentFiles()
            ?.filter { it.name.toLong() < timestamp }
            ?.forEach { it.delete() }
    }

    private fun needsRollFile(): Boolean {
        return currentFile.length() > MAX_FILE_SIZE_BYTES
    }

    private fun ensureCurrentFileExists() {
        if (!currentFile.exists()) {
            currentFile.createNewFile()
        }
    }

    private fun isLoggerThread(): Boolean {
        return currentThread().name == THREAD_NAME
    }
}