package com.gruffins.birch

import com.gruffins.birch.Utils.Companion.currentTimestamp
import com.gruffins.birch.Utils.Companion.safe
import org.json.JSONObject
import java.lang.System.currentTimeMillis
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
    private val eventBus: EventBus,
    private val scrubbers: List<Scrubber>
): EventBus.Listener {
    companion object {
        const val SYNC_PERIOD_SECONDS = 60L * 15L
        const val FLUSH_PERIOD_SECONDS = 60L * 30L
        const val TRIM_PERIOD_SECONDS = 60L * 60L * 24L
        const val MAX_FILE_AGE_SECONDS = 60L * 60L * 24L * 3L
    }

    private enum class FutureType {
        SYNC, FLUSH, TRIM
    }

    private val futures: MutableMap<FutureType, ScheduledFuture<*>> = mutableMapOf()
    private var isStarted = false
    private var flushPeriod: Long by Delegates.observable(storage.flushPeriod) { _, _, newValue ->
        val period = Birch.flushPeriod ?: newValue

        futures[FutureType.FLUSH]?.cancel(false)
        futures[FutureType.FLUSH] = executorService.scheduleAtFixedRate(this::flush, 0, period, TimeUnit.SECONDS)
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

            futures[FutureType.TRIM] = executorService.scheduleAtFixedRate(this::trimFiles, 5, TRIM_PERIOD_SECONDS, TimeUnit.SECONDS)
            futures[FutureType.SYNC] = executorService.scheduleAtFixedRate(this::syncConfiguration, 10, SYNC_PERIOD_SECONDS, TimeUnit.SECONDS)
            futures[FutureType.FLUSH] = executorService.scheduleAtFixedRate(this::flush, 15, FLUSH_PERIOD_SECONDS, TimeUnit.SECONDS)

            updateSource(source)
        }
    }

    fun log(level: Logger.Level, message: () -> String): Boolean {
        if (Birch.optOut) {
            return false
        }

        val timestamp = currentTimestamp
        val scrubbed = { scrubbers.fold(message()) { acc, scrubber -> scrubber.scrub(acc) } }

        logger.log(
            level,
            {
                JSONObject().also { json ->
                    json.put("timestamp", timestamp)
                    json.put("level", level.level)
                    json.put("source", source.toJson())
                    json.put("message", scrubbed())
                }.toString()
            },
            scrubbed
        )
        return true
    }

    fun flushSynchronous(): Boolean {
        if (Birch.optOut) {
            return false
        }

        safe {
            logger.rollFile()
            logger.nonCurrentFiles()?.sorted()?.forEach {
                if (it.length() == 0L) {
                    it.delete()
                } else {
                    network.uploadLogs(it) { success ->
                        if (success) {
                            if (Birch.debug) {
                                Birch.d { "[Birch] Removing file ${it.name}."}
                            }
                            it.delete()
                        }
                    }
                }
            }
        }
        return true
    }

    fun flush() {
        executorService.execute { flushSynchronous() }
    }

    fun updateSourceSynchronous(source: Source): Boolean {
        if (Birch.optOut) {
            return false
        }

        network.syncSource(source)
        return true
    }

    fun updateSource(source: Source) {
        executorService.execute { updateSourceSynchronous(source) }
    }

    fun syncConfigurationSynchronous(): Boolean {
        if (Birch.optOut) {
            return false
        }

        network.getConfiguration(source) {
            val logLevel = Logger.Level.fromInt(it.optInt("log_level", Logger.Level.ERROR.level))
            val period = it.optLong("flush_period_seconds", FLUSH_PERIOD_SECONDS)

            storage.logLevel = logLevel
            logger.level = logLevel
            storage.flushPeriod = period

            flushPeriod = period
        }
        return true
    }

    fun syncConfiguration() {
        executorService.execute { syncConfigurationSynchronous() }
    }

    fun trimFilesSynchronous(now: Long = currentTimeMillis()) {
        val timestamp = now - MAX_FILE_AGE_SECONDS * 1000L
        logger.nonCurrentFiles()
            ?.filter { it.name.toLong() < timestamp }
            ?.forEach { it.delete() }
    }

    fun trimFiles(now: Long = currentTimeMillis()) {
        executorService.execute { trimFilesSynchronous(now) }
    }
}