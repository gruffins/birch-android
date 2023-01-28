package com.gruffins.birch

import android.content.Context
import java.util.concurrent.Executors

class Agent(
    val directory: String
) {
    internal var engine: Engine? = null

    var debug: Boolean = false

    var optOut: Boolean = false

    val uuid: String? get() = engine?.source?.uuid

    var identifier: String?
        get() = engine?.source?.identifier
        set(value) { engine?.source?.identifier = value }

    var customProperties: Map<String, String>
        get() = engine?.source?.customProperties ?: emptyMap()
        set(value) { engine?.source?.customProperties = value }

    var console: Boolean = false

    var remote: Boolean = true

    var level: Level? = null

    var synchronous: Boolean = false

    @JvmOverloads
    fun init(
        context: Context,
        apiKey: String,
        publicKey: String? = null,
        options: Options = Options()
    ) {
        if (engine == null) {
            val encryption: Encryption? = if (publicKey != null) {
                Encryption.create(publicKey)
            } else {
                null
            }

            val appContext = context.applicationContext
            val eventBus = EventBus()
            val storage = Storage(appContext, directory, options.defaultLevel)
            val source = Source(appContext, storage, eventBus)
            val logger = Logger(appContext, storage, this, encryption)
            val network = Network(this, options.host, apiKey)

            engine = Engine(
                this,
                source,
                logger,
                storage,
                network,
                Executors.newScheduledThreadPool(1) { r -> Thread(r, "Birch-Engine") },
                eventBus,
                options.scrubbers
            ).also {
                it.start()
            }
        } else {
            w { "[Birch] Ignored duplicate init() call" }
        }
    }

    fun syncConfiguration() {
        engine?.syncConfiguration()
    }

    fun flush() {
        engine?.flush()
    }

    fun t(message: String) {
        engine?.log(Level.TRACE) { message }
    }

    fun t(format: String, vararg args: Any?) {
        engine?.log(Level.TRACE) { String.format(format, args) }
    }

    fun t(block: () -> String) {
        engine?.log(Level.TRACE, block)
    }

    fun d(message: String) {
        engine?.log(Level.DEBUG) { message }
    }

    fun d(format: String, vararg args: Any?) {
        engine?.log(Level.DEBUG) { String.format(format, args) }
    }

    fun d(block: () -> String) {
        engine?.log(Level.DEBUG, block)
    }

    fun i(message: String) {
        engine?.log(Level.INFO) { message }
    }

    fun i(format: String, vararg args: Any?) {
        engine?.log(Level.INFO) { String.format(format, args) }
    }

    fun i(block: () -> String) {
        engine?.log(Level.INFO, block)
    }

    fun w(message: String) {
        engine?.log(Level.WARN) { message }
    }

    fun w(format: String, vararg args: Any?) {
        engine?.log(Level.WARN) { String.format(format, args) }
    }

    fun w(block: () -> String) {
        engine?.log(Level.WARN, block)
    }

    fun e(message: String) {
        engine?.log(Level.ERROR) { message }
    }

    fun e(format: String, vararg args: Any?) {
        engine?.log(Level.ERROR) { String.format(format, args) }
    }

    fun e(block: () -> String) {
        engine?.log(Level.ERROR, block)
    }
}