package com.gruffins.birch

import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AgentTest {

    private lateinit var engine: Engine
    private lateinit var agent: Agent

    private var block = slot<() -> String>()

    @Before
    fun setup() {
        agent = Agent("birch").also {
            it.init(RuntimeEnvironment.getApplication(), "api_key")
        }
        engine = spyk(agent.engine!!)
        agent.engine = engine

        every { engine.log(any(), capture(block)) } returns true
    }

    @After
    fun teardown() {
        if (block.isCaptured) {
            block.captured.invoke()
        }
    }

    @Test
    fun `uuid() returns engine value`() {
        assert(agent.uuid == engine.source.uuid)
    }

    @Test
    fun `optOut() gets and sets`() {
        agent.optOut = true
        assert(agent.optOut)
    }

    @Test
    fun `identifer() gets and sets`() {
        val value = "identifier"
        agent.identifier = value
        assert(agent.identifier == value)
    }

    @Test
    fun `customProperties() gets and sets`() {
        val value = mapOf("key" to "value")
        agent.customProperties = value
        assert(agent.customProperties["key"] == "value")
    }

    @Test
    fun `init() with invalid public key raises exception`() {
        try {
            Agent("birch")
                .init(RuntimeEnvironment.getApplication(), "api_key", "key")
        } catch (ex: Birch.InvalidPublicKeyException) {
            return
        }
        fail("Should have raised exception")
    }

    @Test
    fun `init() twice doesnt crash`() {
        try {
            agent.init(RuntimeEnvironment.getApplication(), "api_key")
            agent.init(RuntimeEnvironment.getApplication(), "api_key")
        } catch (ex: Exception) {
            fail("Should not throw exception")
        }
    }

    @Test
    fun `syncConfiguration() calls engine`() {
        agent.syncConfiguration()
        verify { engine.syncConfiguration() }
    }
    @Test
    fun `flush() calls engine`() {
        agent.flush()
        verify { engine.flush() }
    }

    @Test
    fun `t() with string`() {
        agent.t("message")
        verify { engine.log(Level.TRACE, any()) }
    }

    @Test
    fun `t() with format`() {
        agent.t("message %s", "value")
        verify { engine.log(Level.TRACE, any()) }
    }

    @Test
    fun `t() with block`() {
        agent.t { "message" }
        verify { engine.log(Level.TRACE, any()) }
    }

    @Test
    fun `t() with throwable`() {
        agent.t(Throwable("Error"))
        verify { engine.log(Level.TRACE, any()) }
    }

    @Test
    fun `t() with message and throwable`() {
        agent.t("message", Throwable("Error"))
        verify { engine.log(Level.TRACE, any()) }
    }

    @Test
    fun `d() with string`() {
        agent.d("message")
        verify { engine.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d() with format`() {
        agent.d("message %s", "value")
        verify { engine.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d() with block`() {
        agent.d { "message" }
        verify { engine.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d() with a throwable`() {
        agent.d(Throwable("Error"))
        verify { engine.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d() with message and throwable`() {
        agent.d("message", Throwable("Error"))
        verify { engine.log(Level.DEBUG, any()) }
    }

    @Test
    fun `i() with string`() {
        agent.i("message")
        verify { engine.log(Level.INFO, any()) }
    }

    @Test
    fun `i() with format`() {
        agent.i("message %s", "value")
        verify { engine.log(Level.INFO, any()) }
    }

    @Test
    fun `i() with block`() {
        agent.i { "message" }
        verify { engine.log(Level.INFO, any()) }
    }

    @Test
    fun `i() with throwable`() {
        agent.i(Throwable("Error"))
        verify { engine.log(Level.INFO, any()) }
    }

    @Test
    fun `i() with message and throwable`() {
        agent.i("message", Throwable("Error"))
        verify { engine.log(Level.INFO, any()) }
    }

    @Test
    fun `w() with string`() {
        agent.w("message")
        verify { engine.log(Level.WARN, any()) }
    }

    @Test
    fun `w() with format`() {
        agent.w("message %s", "value")
        verify { engine.log(Level.WARN, any()) }
    }

    @Test
    fun `w() with block`() {
        agent.w { "message" }
        verify { engine.log(Level.WARN, any()) }
    }

    @Test
    fun `w() with throwable`() {
        agent.w(Throwable("Error"))
        verify { engine.log(Level.WARN, any()) }
    }

    @Test
    fun `w() with message and throwable`() {
        agent.w("message", Throwable("Error"))
        verify { engine.log(Level.WARN, any()) }
    }

    @Test
    fun `e() with string`() {
        agent.e("message")
        verify { engine.log(Level.ERROR, any()) }
    }

    @Test
    fun `e() with format`() {
        agent.e("message %s", "value")
        verify { engine.log(Level.ERROR, any()) }
    }

    @Test
    fun `e() with block`() {
        agent.e { "message" }
        verify { engine.log(Level.ERROR, any()) }
    }

    @Test
    fun `e() with throwable`() {
        agent.e(Throwable("Error"))
        verify { engine.log(Level.ERROR, any()) }
    }

    @Test
    fun `e() with message and throwable`() {
        agent.e("message", Throwable("Error"))
        verify { engine.log(Level.ERROR, any()) }
    }

    @Test
    fun `currentLevel() returns the loggers level`() {
        assert(agent.currentLevel == engine.currentLevel)
    }
}
