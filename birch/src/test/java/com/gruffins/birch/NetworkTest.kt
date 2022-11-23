package com.gruffins.birch

import com.gruffins.birch.utils.TestHTTP
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class NetworkTest {

    private lateinit var configuration: Network.Configuration
    private lateinit var source: Source
    private lateinit var storage: Storage
    private lateinit var eventBus: EventBus

    @Before
    fun setup() {
        configuration = Network.Configuration("127.0.0.1")
        storage = Storage(RuntimeEnvironment.getApplication())
        eventBus = EventBus()
        source = Source(RuntimeEnvironment.getApplication(), storage, eventBus)
    }

    @Test
    fun `uploadLogs fails on 401 response code`() {
        val network = createNetwork(HTTP.Response(401, "{}"))

        var success = false
        val file = File("test").also { it.createNewFile() }
        network.uploadLogs(file) { success = it }
        assert(!success)
    }

    @Test
    fun `uploadLogs fails on a non 200 response code`() {
        val network = createNetwork(HTTP.Response(500, "{}"))

        var success = false
        val file = File("test").also { it.createNewFile() }
        network.uploadLogs(file) { success = it }
        assert(!success)
    }

    @Test
    fun `uploadLogs succeeds on 200 response code`() {
        val network = createNetwork(HTTP.Response(200, "{}"))

        var success = false
        val file = File("test").also { it.createNewFile() }
        network.uploadLogs(file) { success = it }
        assert(success)
    }

    @Test
    fun `syncSource fails on 401 response code`() {
        val network = createNetwork(HTTP.Response(401, "{}"))

        var called = false
        network.syncSource(Source(RuntimeEnvironment.getApplication(), Storage(RuntimeEnvironment.getApplication()), EventBus())) {
            called = true
        }
        assert(!called)
    }

    @Test
    fun `syncSource fails on non 200 response code`() {
        val network = createNetwork(HTTP.Response(500, "{}"))

        var called = false
        network.syncSource(Source(RuntimeEnvironment.getApplication(), Storage(RuntimeEnvironment.getApplication()), EventBus())) {
            called = true
        }
        assert(!called)
    }

    @Test
    fun `syncSource succeeds on 200 response code`() {
        val network = createNetwork(HTTP.Response(200, "{}"))

        var called = false
        network.syncSource(Source(RuntimeEnvironment.getApplication(), Storage(RuntimeEnvironment.getApplication()), EventBus())) {
            called = true
        }
        assert(called)
    }

    @Test
    fun `getConfiguration fails on 401 response code`() {
        val network = createNetwork(HTTP.Response(401, "{}"))

        var called = false
        network.getConfiguration(source) {
            called = true
        }
        assert(!called)
    }

    @Test
    fun `getConfiguration fails on non 200 response code`() {
        val network = createNetwork(HTTP.Response(500, "{}"))

        var called = false
        network.getConfiguration(source) {
            called = true
        }
        assert(!called)
    }

    @Test
    fun `getConfiguration fails with bad json response`() {
        val network = createNetwork(HTTP.Response(200, "invalid"))

        var called = false
        network.getConfiguration(source) {
            called = true
        }
        assert(!called)
    }

    @Test
    fun `getConfiguration succeeds with 200 and valid json`() {
        val network = createNetwork(
            HTTP.Response(
                200,
                JSONObject().also { sc ->
                    sc.put("source_configuration", JSONObject().also {
                        it.put("log_level", Logger.Level.WARN.level)
                        it.put("flush_period_seconds", 1L)
                    })
                }.toString()
            )
        )

        var level: Logger.Level? = null
        var period: Long? = null

        network.getConfiguration(source) {
            level = Logger.Level.fromInt(it.getInt("log_level"))
            period = it.getLong("flush_period_seconds")

        }
        assert(level == Logger.Level.WARN)
        assert(period == 1L)
    }

    private fun createNetwork(response: HTTP.Response): Network {
        return Network(
            "apiKey",
            configuration,
            TestHTTP(response)
        )
    }
}