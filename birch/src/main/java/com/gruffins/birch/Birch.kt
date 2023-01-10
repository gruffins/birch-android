package com.gruffins.birch

import android.annotation.SuppressLint
import android.content.Context
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class Birch private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        internal var engine: Engine? = null
        internal var flushPeriod: Long? = null

        /**
         * Sets the logger in debug mode. Logger is set to TRACE and uploading to 30 seconds.
         * Be sure to disable this in a production build.
         */
        @JvmStatic
        var debug: Boolean by Delegates.observable(false) { _, _, newValue ->
            flushPeriod = if (newValue) 30 else null
            engine?.syncConfiguration()
        }

        /**
         * Sets the logger to opt out. This disables logs collection and source synchronization.
         */
        @JvmStatic
        var optOut: Boolean = false

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
         * Set whether logging to console should be enabled. Defaults to false.
         */
        @JvmStatic
        var console: Boolean = false

        /**
         * Initialize the Birch library with an application context and an API key generated
         * from the Birch dashboard. This must be the first call to the logger.
         *
         * @param context An application context.
         * @param apiKey The API key generated from the Birch dashboard.
         */
        @JvmStatic
        fun init(
            context: Context,
            apiKey: String
        ) {
            init(context, apiKey, null, listOf(PasswordScrubber(), EmailScrubber()))
        }

        /**
         * Initialize the Birch library with an application context and an API key generated
         * from the Birch dashboard. This must be the first call to the logger.
         *
         * @param context An application context.
         * @param apiKey The API key generated from the Birch dashboard.
         * @param publicKey Base64 encoded PEM format RSA public key. Pull from Birch dashboard.
         */
        @JvmStatic
        fun init(
            context: Context,
            apiKey: String,
            publicKey: String?
        ) {
            init(context, apiKey, publicKey, listOf(PasswordScrubber(), EmailScrubber()))
        }

        /**
         * Initialize the Birch library with an application context and an API key generated
         * from the Birch dashboard. This must be the first call to the logger.
         *
         * @param context An application context.
         * @param apiKey The API key generated from the Birch dashboard.
         * @param publicKey Base64 encoded PEM format RSA public key. Pull from Birch dashboard.
         * @param scrubbers The list of scrubbers to be used.
         */
        @JvmStatic
        fun init(
            context: Context,
            apiKey: String,
            publicKey: String?,
            scrubbers: List<Scrubber>,
        ) {
            if (engine == null) {
                val encryption: Encryption? = if (publicKey != null) {
                    Encryption.create(publicKey)
                } else {
                    null
                }

                val appContext = context.applicationContext
                val eventBus = EventBus()
                val storage = Storage(appContext)
                val source = Source(appContext, storage, eventBus)
                val logger = Logger(appContext, storage, encryption)
                val network = Network(apiKey)

                engine = Engine(
                    source,
                    logger,
                    storage,
                    network,
                    Executors.newScheduledThreadPool(1) { r -> Thread(r, "Birch-Engine") },
                    eventBus,
                    scrubbers
                ).also {
                    it.start()
                }
            } else {
                w { "[Birch] Ignored duplicate init() call" }
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
            engine?.log(Logger.Level.TRACE) { message }
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun t(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.TRACE) { String.format(format, args) }
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
            engine?.log(Logger.Level.DEBUG) { message }
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun d(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.DEBUG) { String.format(format, args) }
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
            engine?.log(Logger.Level.INFO) { message }
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun i(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.INFO) { String.format(format, args) }
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
            engine?.log(Logger.Level.WARN) { message }
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun w(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.WARN) { String.format(format, args) }
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
            engine?.log(Logger.Level.ERROR) { message }
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun e(format: String, vararg args: Any?) {
            engine?.log(Logger.Level.ERROR) { String.format(format, args) }
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

    class InvalidPublicKeyException(message: String): RuntimeException(message)
}