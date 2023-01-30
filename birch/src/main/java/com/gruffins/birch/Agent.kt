package com.gruffins.birch

import android.content.Context
import java.util.concurrent.Executors

class Agent(
    val directory: String
) {
    internal var engine: Engine? = null

    /**
     * Sets the logger in debug mode.
     * This should be FALSE in a production build otherwise you will not be able to modify the settings
     * remotely.
     */
    var debug: Boolean = false

    /**
     * Sets the logger to opt out. This disables logs collection and source synchronization.
     */
    var optOut: Boolean = false

    /**
     * The assigned UUID this source has been given. The UUID remains stable per install, it
     * does not persist across installs.
     */
    val uuid: String? get() = engine?.source?.uuid

    /**
     * An identifier such as a user_id that can be used on the Birch dashboard to locate
     * the source.
     */
    var identifier: String?
        get() = engine?.source?.identifier
        set(value) { engine?.source?.identifier = value }

    /**
     * Additional properties of the source that should be appended to each log.
     */
    var customProperties: Map<String, String>
        get() = engine?.source?.customProperties ?: emptyMap()
        set(value) { engine?.source?.customProperties = value }

    /**
     * Set whether logging to console should be enabled. Defaults to FALSE. This should be FALSE
     * in a production build since you cannot read logcat remotely anyways.
     */
    var console: Boolean = false

    /**
     * Set whether remote logging is enabled. Defaults to TRUE. This should be TRUE in a production
     * build so your logs are delivered to Birch.
     */
    var remote: Boolean = true

    /**
     * Override the level set by the server. Defaults to NULL. This should be NULL in a production
     * build so you can remotely adjust the log level.
     */
    var level: Level? = null

    /**
     * Whether to log synchronously or asynchronously. Defaults to FALSE. This should be FALSE in
     * a production build.
     */
    var synchronous: Boolean = false

    /**
     * Initialize the Birch library with an application context and an API key generated
     * from the Birch dashboard. This must be the first call to the logger.
     *
     * @param context An application context.
     * @param apiKey The API key generated from the Birch dashboard.
     * @param publicKey Base64 encoded PEM format RSA public key. Pull from Birch dashboard.
     * @param options Additional options to configure
     */
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

    /**
     * Force agent to synchronous device configuration.
     */
    fun syncConfiguration() {
        engine?.syncConfiguration()
    }

    /**
     * Force the agent to flush its logs. This will flush immediately rather than waiting
     * for the next upload period.
     */
    fun flush() {
        engine?.flush()
    }

    /**
     * Logs a message at the TRACE level.
     *
     * @param message The message to be logged.
     */
    fun t(message: String) {
        engine?.log(Level.TRACE) { message }
    }

    /**
     * Logs a message at the TRACE level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    fun t(format: String, vararg args: Any?) {
        engine?.log(Level.TRACE) { String.format(format, args) }
    }

     /**
     * Logs a message at the TRACE level.
     *
     * @param block A block returning the message to be logged.
     */
    fun t(block: () -> String) {
        engine?.log(Level.TRACE, block)
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message The message to be logged.
     */
    fun d(message: String) {
        engine?.log(Level.DEBUG) { message }
    }

     /**
     * Logs a message at the DEBUG level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    fun d(format: String, vararg args: Any?) {
        engine?.log(Level.DEBUG) { String.format(format, args) }
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param block A block returning the message to be logged.
     */
    fun d(block: () -> String) {
        engine?.log(Level.DEBUG, block)
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message The message to be logged.
     */
    fun i(message: String) {
        engine?.log(Level.INFO) { message }
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    fun i(format: String, vararg args: Any?) {
        engine?.log(Level.INFO) { String.format(format, args) }
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param block A block returning the message to be logged.
     */
    fun i(block: () -> String) {
        engine?.log(Level.INFO, block)
    }

     /**
     * Logs a message at the WARN level.
     *
     * @param message The message to be logged.
     */
    fun w(message: String) {
        engine?.log(Level.WARN) { message }
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    fun w(format: String, vararg args: Any?) {
        engine?.log(Level.WARN) { String.format(format, args) }
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param block A block returning the message to be logged.
     */
    fun w(block: () -> String) {
        engine?.log(Level.WARN, block)
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message The message to be logged.
     */
    fun e(message: String) {
        engine?.log(Level.ERROR) { message }
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    fun e(format: String, vararg args: Any?) {
        engine?.log(Level.ERROR) { String.format(format, args) }
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param block A block returning the message to be logged.
     */
    fun e(block: () -> String) {
        engine?.log(Level.ERROR, block)
    }
}