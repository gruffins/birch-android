package com.gruffins.birch

import com.gruffins.birch.Utils.Companion.currentTimestamp
import com.gruffins.birch.Utils.Companion.safe
import org.json.JSONObject
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

internal class Engine(
    val source: Source,
    private val logger: Logger,
    private val storage: Storage,
    private val network: Network,
    private val executorService: ScheduledExecutorService,
    eventBus: EventBus
): EventBus.Listener {
    companion object {
        const val SYNC_PERIOD_SECONDS = 60L * 15L
        const val FLUSH_PERIOD_SECONDS = 60L * 30L
        const val DEBUG_FLUSH_PERIOD_SECONDS = 30L
        const val TRIM_PERIOD_SECONDS = 60L * 60L * 60L * 24L
    }

    private var isStarted = false
    private var uploadQueue = mutableListOf<String>()
    private var flushPeriod: Long by Delegates.observable(storage.flushPeriod) { _, _, newValue ->
        val period = overrideFlushPeriod ?: newValue

        flushFuture?.cancel(false)
        flushFuture = executorService.scheduleAtFixedRate(this::flush, 0, period, TimeUnit.SECONDS)
    }
    private var flushFuture: ScheduledFuture<*>? = null

    var overrideFlushPeriod: Long? by Delegates.observable(null) { _, _, newValue ->
        flushPeriod = newValue ?: storage.flushPeriod
    }

    init {
        eventBus.subscribe(this)
    }

    override fun onEvent(event: EventBus.Event){
        when (event) {
            is EventBus.Event.SourceUpdated -> updateSource(event.source)
        }
    }

    fun start() {
        if (!isStarted) {
            isStarted = true

            executorService.scheduleAtFixedRate(this::trimFiles, 5, TRIM_PERIOD_SECONDS, TimeUnit.SECONDS)
            executorService.scheduleAtFixedRate(this::syncConfiguration, 10, SYNC_PERIOD_SECONDS, TimeUnit.SECONDS)
            flushFuture = executorService.scheduleAtFixedRate(this::flush, 15, FLUSH_PERIOD_SECONDS, TimeUnit.SECONDS)
            executorService.execute { updateSource(source) }
        }
    }

    fun log(level: Logger.Level, message: () -> String) {
        val timestamp = currentTimestamp

        logger.log(
            level,
            {
                JSONObject().also { json ->
                    json.put("timestamp", timestamp)
                    json.put("level", level.level)
                    json.put("source", source.toJson())
                    json.put("message", message())
                }.toString()
            }, message
        )
    }

    fun flush() {
        safe {
            logger.rollFile()
            logger.nonCurrentFiles()?.sorted()?.forEach {
                if (it.length() == 0L) {
                    it.delete()
                } else if (!uploadQueue.contains(it.name)) {
                    uploadQueue.add(it.name)
                    network.uploadLogs(it) { success ->
                        if (success) {
                            if (Birch.debug) {
                                Birch.d { "[Birch] Removing file ${it.name}."}
                            }
                            it.delete()
                        }
                        uploadQueue.remove(it.name)
                    }
                }
            }
        }
    }

    fun updateSource(source: Source) {
        network.syncSource(source)
    }

    fun syncConfiguration() {
        network.getConfiguration(source) {
            val logLevel = Logger.Level.fromInt(it.optInt("log_level", Logger.Level.ERROR.level))
            val period = it.optLong("flush_period_seconds", FLUSH_PERIOD_SECONDS)

            storage.logLevel = logLevel
            logger.level = logLevel
            storage.flushPeriod = period

            flushPeriod = period
        }
    }

    fun trimFiles() {
        logger.trimFiles()
    }
}