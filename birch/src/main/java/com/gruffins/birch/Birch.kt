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
         * Sets the logger in debug mode. This will log Birch operations and set the flush period to 30 seconds.
         * This should be FALSE in a production build otherwise you will not be able to modify the settings
         * remotely.
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
         * Set whether logging to console should be enabled. Defaults to FALSE. This should be FALSE
         * in a production build since you cannot read logcat remotely anyways.
         */
        @JvmStatic
        var console: Boolean = false

        /**
         * Set whether remote logging is enabled. Defaults to TRUE. This should be TRUE in a production
         * build so your logs are delivered to Birch.
         */
        @JvmStatic
        var remote: Boolean = true

        /**
         * Override the level set by the server. Defaults to NULL. This should be NULL in a production
         * build so you can remotely adjust the log level.
         */
        @JvmStatic
        var level: Level? = null

        /**
         * Whether to log synchronously or asynchronously. Defaults to FALSE. This should be FALSE in
         * a production build.
         */
        @JvmStatic
        var synchronous: Boolean = false

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
            engine?.log(Level.TRACE) { message }
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun t(format: String, vararg args: Any?) {
            engine?.log(Level.TRACE) { String.format(format, args) }
        }

        /**
         * Logs a message at the TRACE level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun t(block: () -> String) {
            engine?.log(Level.TRACE, block)
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun d(message: String) {
            engine?.log(Level.DEBUG) { message }
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun d(format: String, vararg args: Any?) {
            engine?.log(Level.DEBUG) { String.format(format, args) }
        }

        /**
         * Logs a message at the DEBUG level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun d(block: () -> String) {
            engine?.log(Level.DEBUG, block)
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun i(message: String) {
            engine?.log(Level.INFO) { message }
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun i(format: String, vararg args: Any?) {
            engine?.log(Level.INFO) { String.format(format, args) }
        }

        /**
         * Logs a message at the INFO level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun i(block: () -> String) {
            engine?.log(Level.INFO, block)
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun w(message: String) {
            engine?.log(Level.WARN) { message }
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun w(format: String, vararg args: Any?) {
            engine?.log(Level.WARN) { String.format(format, args) }
        }

        /**
         * Logs a message at the WARN level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun w(block: () -> String) {
            engine?.log(Level.WARN, block)
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param message The message to be logged.
         */
        @JvmStatic
        fun e(message: String) {
            engine?.log(Level.ERROR) { message }
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param format A format string used for String.format().
         * @param args The arguments passed into String.format().
         */
        @JvmStatic
        fun e(format: String, vararg args: Any?) {
            engine?.log(Level.ERROR) { String.format(format, args) }
        }

        /**
         * Logs a message at the ERROR level.
         *
         * @param block A block returning the message to be logged.
         */
        @JvmStatic
        fun e(block: () -> String) {
            engine?.log(Level.ERROR, block)
        }
    }

    class InvalidPublicKeyException(message: String): RuntimeException(message)
}