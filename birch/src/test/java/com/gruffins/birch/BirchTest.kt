package com.gruffins.birch

import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BirchTest {

    private lateinit var agent: Agent

    @Before
    fun setup() {
        Birch.init(
            RuntimeEnvironment.getApplication(),
            "api_key",
            null,
            Options().also {
                it.host = "localhost"
            }
        )
        agent = spyk(Birch.agent)
        Birch.agent = agent

    }

    @Test
    fun `uuid() returns a uuid`() {
        assert(Birch.uuid != null)
    }

    @Test
    fun `identifier() gets and sets`() {
        Birch.identifier = "abcd"
        assert(Birch.identifier == "abcd")
    }

    @Test
    fun `customProperties() gets and sets`() {
        Birch.customProperties = mapOf("key" to "value")
        assert(Birch.customProperties["key"] == "value")
    }

    @Test
    fun `syncConfiguration() syncs`() {
        Birch.syncConfiguration()
        verify { agent.syncConfiguration() }
    }

    @Test
    fun `flush() flushes`() {
        Birch.flush()
        verify { agent.flush() }
    }

    @Test
    fun `t(String) calls the agent`() {
        Birch.t("message")
        verify { agent.t("message") }
    }

    @Test
    fun `t(StringF) calls the engine`() {
        Birch.t("message %s", "hello")
        verify { agent.t("message %s", any()) }
    }

    @Test
    fun `t(Block) calls the engine`() {
        val block = { "message" }
        Birch.t(block)
        verify { agent.t(block) }
    }

    @Test
    fun `d(String) calls the engine`() {
        Birch.d("message")
        verify { agent.d("message") }
    }

    @Test
    fun `d(StringF) calls the engine`() {
        Birch.d("message %s", "hello")
        verify { agent.d("message %s", any()) }
    }

    @Test
    fun `d(Block) calls the engine`() {
        val block = { "message" }
        Birch.d(block)
        verify { agent.d(block) }
    }

    @Test
    fun `i(String) calls the engine`() {
        Birch.i("message")
        verify { agent.i("message") }
    }

    @Test
    fun `i(StringF) calls the engine`() {
        Birch.i("message %s", "hello")
        verify { agent.i("message %s", any()) }
    }

    @Test
    fun `i(Block) calls the engine`() {
        val block = { "message" }
        Birch.i(block)
        verify { agent.i(block) }
    }

    @Test
    fun `w(String) calls the engine`() {
        Birch.w("message")
        verify { agent.w("message") }
    }

    @Test
    fun `w(StringF) calls the engine`() {
        Birch.w("message %s", "hello")
        verify { agent.w("message %s", any()) }
    }

    @Test
    fun `w(Block) calls the engine`() {
        val block = { "message" }
        Birch.w(block)
        verify { agent.w(block) }
    }

    @Test
    fun `e(String) calls the engine`() {
        Birch.e("message")
        verify { agent.e("message") }
    }

    @Test
    fun `e(StringF) calls the engine`() {
        Birch.e("message %s", "hello")
        verify { agent.e("message %s", any()) }
    }

    @Test
    fun `e(Block) calls the engine`() {
        val block = { "message" }
        Birch.e(block)
        verify { agent.e(block) }
    }
}