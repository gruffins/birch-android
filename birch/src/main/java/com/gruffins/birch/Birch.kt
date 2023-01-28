package com.gruffins.birch

import android.content.Context

object Birch {
    class InvalidPublicKeyException(message: String): RuntimeException(message)

    internal var agent = Agent("birch")

    var debug: Boolean
        get() = agent.debug
        set(value) { agent.debug = value }

    var optOut: Boolean
        get() = agent.optOut
        set(value) { agent.optOut = value}

    val uuid: String? get() = agent.uuid

    var identifier: String?
        get() = agent.identifier
        set(value) { agent.identifier = value }

    var customProperties: Map<String, String>
        get() = agent.customProperties
        set(value) { agent.customProperties = value }

    var console: Boolean
        get() = agent.console
        set(value) { agent.console = value }

    var remote: Boolean
        get() = agent.remote
        set(value) { agent.remote = value }

    var level: Level?
        get() = agent.level
        set(value) { agent.level = value }

    var synchronous: Boolean
        get() = agent.synchronous
        set(value) { agent.synchronous = value }

    @JvmOverloads
    fun init(
        context: Context,
        apiKey: String,
        publicKey: String? = null,
        options: Options = Options()
    ) {
        agent.init(context, apiKey, publicKey, options)
    }

    fun syncConfiguration() {
        agent.syncConfiguration()
    }

    fun flush() {
        agent.flush()
    }

    fun t(message: String) {
        agent.t(message)
    }


    fun t(format: String, vararg args: Any?) {
        agent.t(format, args)
    }


    fun t(block: () -> String) {
        agent.t(block)
    }

    fun d(message: String) {
        agent.d(message)
    }

    fun d(format: String, vararg args: Any?) {
        agent.d(format, args)
    }

    fun d(block: () -> String) {
        agent.d(block)
    }

    fun i(message: String) {
        agent.i(message)
    }

    fun i(format: String, vararg args: Any?) {
        agent.i(format, args)
    }

    fun i(block: () -> String) {
        agent.i(block)
    }

    fun w(message: String) {
        agent.w(message)
    }

    fun w(format: String, vararg args: Any?) {
        agent.w(format, args)
    }

    fun w(block: () -> String) {
        agent.w(block)
    }

    fun e(message: String) {
        agent.e(message)
    }

    fun e(format: String, vararg args: Any?) {
        agent.e(format, args)
    }

    fun e(block: () -> String) {
        agent.e(block)
    }
}