package com.gruffins.birch

import android.content.Context
import android.os.StatFs
import android.util.Log
import com.gruffins.birch.Utils.safe
import com.gruffins.birch.Utils.splitStringBySize
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.lang.System.currentTimeMillis
import java.lang.Thread.currentThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The logger is responsible for writing to the current file and rolling the file when necessary.
 * It is not responsible for tracking previously rolled files.
 */
internal class Logger(
    context: Context,
    private val storage: Storage,
    private val agent: Agent,
    private val encryption: Encryption?,
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, THREAD_NAME) }
) {
    companion object {
        const val THREAD_NAME = "Birch-Logger"
        const val MAX_FILE_SIZE_BYTES = 1024 * 512 * 1
        const val MAX_ENTRY_SIZE = 4096
    }

    val level get() = storage.logLevel
    val directory = File(context.filesDir, agent.directory)
    val currentLevel get() = agent.level ?: this.level

    private var currentFile = File(directory, "current")

    init {
        directory.mkdirs()
    }

    fun log(level: Level, metadata: () -> JSONObject, original: () -> String): Boolean {
        if (diskAvailable() && level >= currentLevel)   {
            executorService.submit {
                FileWriter(currentFile, true).use { fileWriter ->
                    ensureCurrentFileExists()

                    splitStringBySize(original(), MAX_ENTRY_SIZE).forEach { chunk ->
                        val json = metadata().also {
                            it.put("message", chunk)
                        }

                        val message = encryption?.let { enc ->
                            JSONObject().also {
                                it.put("ek", enc.encryptedKey)
                                it.put("em", enc.encrypt(json.toString()))
                            }
                        } ?: run {
                            json.toString()
                        }

                        if (agent.remote) {
                            fileWriter.write("$message,\n")
                        }

                        if (agent.console) {
                            when (level) {
                                Level.TRACE -> Log.v("Birch", chunk)
                                Level.DEBUG -> Log.d("Birch", chunk)
                                Level.INFO -> Log.i("Birch", chunk)
                                Level.WARN -> Log.w("Birch", chunk)
                                Level.ERROR -> Log.e("Birch", chunk)
                                Level.NONE -> Unit
                            }
                        }
                    }

                    if (needsRollFile()) {
                        rollFile()
                    }
                }
            }.also {
                if (agent.synchronous) {
                    safe { it.get(5, TimeUnit.SECONDS) }
                }
            }
            return true
        } else if (agent.debug && agent.console) {
            Log.d("Birch", "Dropped log. level=$level currentLevel=$currentLevel")
        }
        return false
    }

    fun nonCurrentFiles(): List<File>? {
        return directory.listFiles { _, name -> "current" != name }?.toList()
    }

    fun rollFile() {
        val block = {
            ensureCurrentFileExists()

            val timestamp = currentTimeMillis().toString()
            val rollTo = File(directory, timestamp)

            currentFile.renameTo(rollTo)
            currentFile = File(directory, "current")
            currentFile.createNewFile()
        }

        if (isLoggerThread()) {
            block()
        } else {
            executorService.submit(block).get()
        }
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

    private fun diskAvailable(): Boolean {
        return if (Utils.hasOSVersion(18)) {
            StatFs(directory.absolutePath).availableBytes > 0
        } else {
            StatFs(directory.absolutePath).availableBlocks > 0
        }
    }
}