package com.gruffins.birch.app

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.gruffins.birch.Birch

class MainActivity: Activity() {

    lateinit var toggleDebugButton: Button
    lateinit var traceButton: Button
    lateinit var debugButton: Button
    lateinit var infoButton: Button
    lateinit var warnButton: Button
    lateinit var errorButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleDebugButton = findViewById(R.id.toggle_debug)
        traceButton = findViewById(R.id.trace)
        debugButton = findViewById(R.id.debug)
        infoButton = findViewById(R.id.info)
        warnButton = findViewById(R.id.warn)
        errorButton = findViewById(R.id.error)

        toggleDebugButton.setOnClickListener(this::toggleDebug)
        traceButton.setOnClickListener(this::trace)
        debugButton.setOnClickListener(this::debug)
        infoButton.setOnClickListener(this::info)
        warnButton.setOnClickListener(this::warn)
        errorButton.setOnClickListener(this::error)
    }

    private fun toggleDebug(_view: View) {
        Birch.debug = !Birch.debug
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

}