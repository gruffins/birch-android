package com.gruffins.birch.timber

import android.util.Log
import com.gruffins.birch.Birch
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter

class BirchTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val msg = formatMessage(tag, message, t)
        when (priority) {
            Log.VERBOSE -> Birch.t { msg }
            Log.DEBUG -> Birch.d { msg }
            Log.INFO -> Birch.i { msg }
            Log.WARN -> Birch.w { msg }
            Log.ERROR -> Birch.e { msg }
        }
    }

    private fun formatMessage(tag: String?, message: String, t: Throwable?): String {
        return StringBuilder()
            .also { builder ->
                tag?.let { builder.append("[${tag}] ") }
                builder.append(message)
                t?.let { builder.append("\n").append(getStackTraceString(it)) }
            }
            .toString()
    }

    private fun getStackTraceString(t: Throwable): String {
      val sw = StringWriter(256)
      val pw = PrintWriter(sw, false)
      t.printStackTrace(pw)
      pw.flush()
      return sw.toString()
    }
}