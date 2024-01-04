package com.gruffins.birch

import android.content.Context

object Birch {
    class InvalidPublicKeyException(message: String): RuntimeException(message)

    internal var agent = Agent("birch")

    /**
     * Sets the logger in debug mode. This should be FALSE in a production build.
     */
    @JvmStatic
    var debug: Boolean
        get() = agent.debug
        set(value) { agent.debug = value }

    /**
     * Sets the logger to opt out. This disables logs collection and source synchronization.
     */
    @JvmStatic
    var optOut: Boolean
        get() = agent.optOut
        set(value) { agent.optOut = value}

    /**
     * The assigned UUID this source has been given. The UUID remains stable per install, it
     * does not persist across installs.
     */
    @JvmStatic
    val uuid: String? get() = agent.uuid

    /**
     * An identifier such as a user_id that can be used on the Birch dashboard to locate
     * the source.
     */
    @JvmStatic
    var identifier: String?
        get() = agent.identifier
        set(value) { agent.identifier = value }

    /**
     * Additional properties of the source that should be appended to each log.
     */
    @JvmStatic
    var customProperties: Map<String, String>
        get() = agent.customProperties
        set(value) { agent.customProperties = value }

    /**
     * Set whether logging to console should be enabled. Defaults to TRUE. Consider changing to FALSE in production.
     */
    @JvmStatic
    var console: Boolean
        get() = agent.console
        set(value) { agent.console = value }

    /**
     * Set whether remote logging is enabled. Defaults to TRUE. This should be TRUE in a production
     * build so your logs are delivered to Birch.
     */
    @JvmStatic
    var remote: Boolean
        get() = agent.remote
        set(value) { agent.remote = value }

    /**
     * Override the level set by the server. Defaults to NULL. This should be NULL in a production
     * build so you can remotely adjust the log level.
     */
    @JvmStatic
    var level: Level?
        get() = agent.level
        set(value) { agent.level = value }

    /**
     * Whether to log synchronously or asynchronously. Defaults to FALSE. This should be FALSE in
     * a production build.
     */
    @JvmStatic
    var synchronous: Boolean
        get() = agent.synchronous
        set(value) { agent.synchronous = value }

    /**
     * Initialize the Birch library with an application context and an API key generated
     * from the Birch dashboard. This must be the first call to the logger.
     *
     * @param context An application context.
     * @param apiKey The API key generated from the Birch dashboard.
     * @param publicKey Base64 encoded PEM format RSA public key. Pull from Birch dashboard.
     * @param options Additional options to configure.
     */
    @JvmOverloads
    @JvmStatic
    fun init(
        context: Context,
        apiKey: String,
        publicKey: String? = null,
        options: Options = Options()
    ) {
        agent.init(context, apiKey, publicKey, options)
    }

    /**
     * Force agent to synchronize device configuration.
     */
    @JvmStatic
    fun syncConfiguration() {
        agent.syncConfiguration()
    }

    /**
     * Force the agent to flush its logs. This will flush immediately rather than waiting
     * for the next upload period.
     */
    @JvmStatic
    fun flush() {
        agent.flush()
    }

    /**
     * Logs a message at the TRACE level.
     *
     * @param message The message to be logged.
     */
    @JvmStatic
    fun t(message: String) {
        agent.t(message)
    }

    /**
     * Logs a message at the TRACE level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    @JvmStatic
    fun t(format: String, vararg args: Any?) {
        agent.t(format, args)
    }

     /**
     * Logs a message at the TRACE level.
     *
     * @param block A block returning the message to be logged.
     */
     @JvmStatic
    fun t(block: () -> String) {
        agent.t(block)
    }

    /**
     * Logs the throwable at the TRACE level.
     *
     * @param throwable The throwable to log.
     */
    @JvmStatic
    fun t(throwable: Throwable) {
        agent.t(throwable)
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message The message to be logged.
     */
    @JvmStatic
    fun d(message: String) {
        agent.d(message)
    }

     /**
     * Logs a message at the DEBUG level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
     @JvmStatic
    fun d(format: String, vararg args: Any?) {
        agent.d(format, args)
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param block A block returning the message to be logged.
     */
    @JvmStatic
    fun d(block: () -> String) {
        agent.d(block)
    }

    /**
     * Logs the throwable at the DEBUG level.
     *
     * @param throwable The throwable to log.
     */
    @JvmStatic
    fun d(throwable: Throwable) {
        agent.d(throwable)
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message The message to be logged.
     */
    @JvmStatic
    fun i(message: String) {
        agent.i(message)
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    @JvmStatic
    fun i(format: String, vararg args: Any?) {
        agent.i(format, args)
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param block A block returning the message to be logged.
     */
    @JvmStatic
    fun i(block: () -> String) {
        agent.i(block)
    }

    /**
     * Logs the throwable at the INFO level.
     *
     * @param throwable The throwable to log.
     */
    @JvmStatic
    fun i(throwable: Throwable) {
        agent.i(throwable)
    }

     /**
     * Logs a message at the WARN level.
     *
     * @param message The message to be logged.
     */
     @JvmStatic
    fun w(message: String) {
        agent.w(message)
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    @JvmStatic
    fun w(format: String, vararg args: Any?) {
        agent.w(format, args)
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param block A block returning the message to be logged.
     */
    @JvmStatic
    fun w(block: () -> String) {
        agent.w(block)
    }

    /**
     * Logs the throwable at the WARN level.
     *
     * @param throwable The throwable to log.
     */
    @JvmStatic
    fun w(throwable: Throwable) {
        agent.w(throwable)
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message The message to be logged.
     */
    @JvmStatic
    fun e(message: String) {
        agent.e(message)
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param format A format string used for String.format().
     * @param args The arguments passed into String.format().
     */
    @JvmStatic
    fun e(format: String, vararg args: Any?) {
        agent.e(format, args)
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param block A block returning the message to be logged.
     */
    @JvmStatic
    fun e(block: () -> String) {
        agent.e(block)
    }

    /**
     * Logs the throwable at the ERROR level.
     *
     * @param throwable The throwable to log.
     */
    @JvmStatic
    fun e(throwable: Throwable) {
        agent.e(throwable)
    }
}