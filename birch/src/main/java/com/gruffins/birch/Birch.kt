package com.gruffins.birch

import android.annotation.SuppressLint
import android.content.Context
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class Birch {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var engine: Engine? = null

        /**
         * Sets the logger in debug mode. Logger is set to TRACE and uploading to 30 seconds.
         * Be sure to disable this in a production build.
         */
        @JvmStatic
        var debug: Boolean by Delegates.observable(false) { _, _, _ ->
            engine?.overrideFlushPeriod = if (debug) Engine.DEBUG_FLUSH_PERIOD_SECONDS else null
        }

        /**
         * Override the default host that should be used. Leave null to use the standard host.
         * This must be called BEFORE init().
         */
        @JvmStatic
        var host: String?
            get() = Network.HOST
            set(value) { Network.HOST = if (value.isNullOrBlank()) Network.DEFAULT_HOST else value }

        /**
         * The assigned UUID this source has been given. The UUID remains stable per install, it
         * does not persist across installs.
         */
        @JvmStatic
        val uuid: String? get() = engine?.source?.uuid

        /**
         * An identifier such as a user_id that can be used on the Birch dashboard to locate
         * the source.
         */
        @JvmStatic
        var identifier: String?
            get() = engine?.source?.identifier
            set(value) { engine?.source?.identifier = value }

        /**
         * Additional properties of the source that should be appended to each log.
         */
        @JvmStatic
        var customProperties: Map<String, String>
            get() = engine?.source?.customProperties ?: emptyMap()
            set(value) { engine?.source?.customProperties = value }

        /**
         * Initialize the Birch library with an application context and an API key generated
         * from the Birch dashboard. This must be the first call to the logger.
         *
         * @param context An application context.
         * @param apiKey The API key generated from the Birch dashboard.
         */
        @JvmStatic
        fun init(context: Context, apiKey: String) {
            if (engine == null) {
                val appContext = context.applicationContext
                val eventBus = EventBus()
                val storage = Storage(appContext)
                val source = Source(appContext, storage, eventBus)
                val logger = Logger(appContext, storage)
                val network = Network(apiKey)

                engine = Engine(
                    source,
                    logger,
                    storage,
                    network,
                    Executors.newScheduledThreadPool(1) { r -> Thread(r, "Birch-Engine") },
                    eventBus
                ).also {
                    it.start()
                }
            }
        }

        /**
         * Force the library to flush its logs. This will flush immediately rather than waiting
         * for the next upload period.
         */
        @JvmStatic
        fun flush() {
            engine?.flush()
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun t(message: String) {
            engine?.log(Logger.Level.TRACE) { -> message }
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun t(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.TRACE) { -> String.format(format, args) }
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun t(block: () -> String) {
            engine?.log(Logger.Level.TRACE, block)
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun d(message: String) {
            engine?.log(Logger.Level.DEBUG) { -> message }
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun d(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.DEBUG) { -> String.format(format, args) }
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun d(block: () -> String) {
            engine?.log(Logger.Level.DEBUG, block)
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun i(message: String) {
            engine?.log(Logger.Level.INFO) { -> message }
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun i(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.INFO) { -> String.format(format, args) }
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun i(block: () -> String) {
            engine?.log(Logger.Level.INFO, block)
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun w(message: String) {
            engine?.log(Logger.Level.WARN) { -> message }
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun w(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.WARN) { -> String.format(format, args) }
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun w(block: () -> String) {
            engine?.log(Logger.Level.WARN, block)
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun e(message: String) {
            engine?.log(Logger.Level.ERROR) { -> message }
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun e(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.ERROR) { -> String.format(format, args) }
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun e(block: () -> String) {
            engine?.log(Logger.Level.ERROR, block)
        }
    }
}