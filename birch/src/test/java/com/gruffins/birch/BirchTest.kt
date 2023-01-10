package com.gruffins.birch

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.SpyK
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BirchTest {

    private lateinit var engine: Engine

    @Before
    fun setup() {
        Birch.host = "localhost"
        Birch.init(RuntimeEnvironment.getApplication(), "api_key")
        engine = spyk(Birch.engine!!)
        Birch.engine = engine

    }

    @Test
    fun `host() gets and sets`() {
        Birch.host = "birch.gruffins.com"
        assert(Birch.host == "birch.gruffins.com")
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
    fun `flush() flushes`() {
        Birch.flush()
        verify { Birch.engine?.flush() }
    }

    @Test
    fun `t(String) calls the engine`() {
        Birch.t("message")
        verify { Birch.engine?.log(Level.TRACE, any()) }
    }

    @Test
    fun `t(StringF) calls the engine`() {
        Birch.t("message %s", "hello")
        verify { Birch.engine?.log(Level.TRACE, any()) }
    }

    @Test
    fun `t(Block) calls the engine`() {
        Birch.t { "message" }
        verify { Birch.engine?.log(Level.TRACE, any()) }
    }

    @Test
    fun `d(String) calls the engine`() {
        Birch.d("message")
        verify { Birch.engine?.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d(StringF) calls the engine`() {
        Birch.d("message %s", "hello")
        verify { Birch.engine?.log(Level.DEBUG, any()) }
    }

    @Test
    fun `d(Block) calls the engine`() {
        Birch.d { "message" }
        verify { Birch.engine?.log(Level.DEBUG, any()) }
    }

    @Test
    fun `i(String) calls the engine`() {
        Birch.i("message")
        verify { Birch.engine?.log(Level.INFO, any()) }
    }

    @Test
    fun `i(StringF) calls the engine`() {
        Birch.i("message %s", "hello")
        verify { Birch.engine?.log(Level.INFO, any()) }
    }

    @Test
    fun `i(Block) calls the engine`() {
        Birch.i { "message" }
        verify { Birch.engine?.log(Level.INFO, any()) }
    }

    @Test
    fun `w(String) calls the engine`() {
        Birch.w("message")
        verify { Birch.engine?.log(Level.WARN, any()) }
    }

    @Test
    fun `w(StringF) calls the engine`() {
        Birch.w("message %s", "hello")
        verify { Birch.engine?.log(Level.WARN, any()) }
    }

    @Test
    fun `w(Block) calls the engine`() {
        Birch.w { "message" }
        verify { Birch.engine?.log(Level.WARN, any()) }
    }

    @Test
    fun `e(String) calls the engine`() {
        Birch.e("message")
        verify { Birch.engine?.log(Level.ERROR, any()) }
    }

    @Test
    fun `e(StringF) calls the engine`() {
        Birch.e("message %s", "hello")
        verify { Birch.engine?.log(Level.ERROR, any()) }
    }

    @Test
    fun `e(Block) calls the engine`() {
        Birch.e { "message" }
        verify { Birch.engine?.log(Level.ERROR, any()) }
    }
}