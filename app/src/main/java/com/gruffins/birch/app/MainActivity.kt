package com.gruffins.birch.app

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.gruffins.birch.Birch
import com.gruffins.birch.Level
import kotlin.concurrent.thread

class MainActivity: Activity() {

    lateinit var toggleDebugButton: Button
    lateinit var toggleLevelButton: Button
    lateinit var toggleConsoleButton: Button
    lateinit var toggleRemoteButton: Button
    lateinit var toggleSynchronousButton: Button
    lateinit var syncConfigurationButton: Button
    lateinit var traceButton: Button
    lateinit var debugButton: Button
    lateinit var infoButton: Button
    lateinit var warnButton: Button
    lateinit var errorButton: Button
    lateinit var stressTestButton: Button

    var isStressTesting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleDebugButton = findViewById(R.id.toggle_debug)
        toggleLevelButton = findViewById(R.id.toggle_level)
        toggleConsoleButton = findViewById(R.id.toggle_console)
        toggleRemoteButton = findViewById(R.id.toggle_remote)
        toggleSynchronousButton = findViewById(R.id.toggle_synchronous)
        syncConfigurationButton = findViewById(R.id.sync_configuration)
        traceButton = findViewById(R.id.trace)
        debugButton = findViewById(R.id.debug)
        infoButton = findViewById(R.id.info)
        warnButton = findViewById(R.id.warn)
        errorButton = findViewById(R.id.error)
        stressTestButton = findViewById(R.id.stress_test)

        toggleDebugButton.setOnClickListener(this::toggleDebug)
        toggleLevelButton.setOnClickListener(this::toggleLevel)
        toggleConsoleButton.setOnClickListener(this::toggleConsole)
        toggleRemoteButton.setOnClickListener(this::toggleRemote)
        toggleSynchronousButton.setOnClickListener(this::toggleSynchronous)
        syncConfigurationButton.setOnClickListener(this::syncConfiguration)
        traceButton.setOnClickListener(this::trace)
        debugButton.setOnClickListener(this::debug)
        infoButton.setOnClickListener(this::info)
        warnButton.setOnClickListener(this::warn)
        errorButton.setOnClickListener(this::error)
        stressTestButton.setOnClickListener(this::stressTest)

        setState()
    }

    private fun syncConfiguration(_view: View) {
        Birch.syncConfiguration()
    }

    private fun toggleDebug(_view: View) {
        Birch.debug = !Birch.debug
        setState()
    }

    private fun toggleLevel(_view: View) {
        Birch.level = when (Birch.level) {
            Level.TRACE -> Level.DEBUG
            Level.DEBUG -> Level.INFO
            Level.INFO -> Level.WARN
            Level.WARN -> Level.ERROR
            Level.ERROR -> null
            else -> Level.TRACE
        }
        setState()
    }

    private fun toggleConsole(_view: View) {
        Birch.console = !Birch.console
        setState()
    }

    private fun toggleRemote(_view: View) {
        Birch.remote = !Birch.remote
        setState()
    }

    private fun toggleSynchronous(_view: View) {
        Birch.synchronous = !Birch.synchronous
        setState()
    }

    private fun trace(_view: View) {
        Birch.t { "trace log" }
    }

    private fun debug(_view: View) {
        Birch.d { "debug message" }
    }

    private fun info(_view: View) {
        Birch.i { "info text" }
    }

    private fun warn(_view: View) {
        Birch.w { "warn msg" }
    }

    private fun error(_view: View) {
        Birch.e { "error alert" }
    }

    private fun stressTest(_view: View) {
        if (isStressTesting) {
            return
        }
        isStressTesting = true
        val threads = mutableListOf<Thread>()

        repeat(4) {
            val tid = "thread-$it"
            threads.add(
                thread {
                    repeat(5_000) {
                        Birch.d { "$tid - $it" }
                        Thread.sleep((10..50).random().toLong())
                    }
                }
            )
        }

        threads.forEach { it.join() }
        isStressTesting = false
    }

    private fun translate(bool: Boolean): String {
        return if (bool) "ON" else "OFF"
    }

    @SuppressLint("SetTextI18n")
    private fun setState() {
        toggleDebugButton.text = "Debug ${translate(Birch.debug)}"
        toggleLevelButton.text = "Level ${Birch.level}"
        toggleConsoleButton.text = "Console ${translate(Birch.console)}"
        toggleRemoteButton.text = "Remote ${translate(Birch.remote)}"
        toggleSynchronousButton.text = "Synchronous ${translate(Birch.synchronous)}"
    }
}