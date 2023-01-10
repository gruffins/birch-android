package com.gruffins.birch

import org.junit.Test

class LevelTest {

    @Test
    fun `fromInt() parses 0`() {
        assert(Level.fromInt(0) == Level.TRACE)
    }

    @Test
    fun `fromInt() parses 1`() {
        assert(Level.fromInt(1) == Level.DEBUG)
    }

    @Test
    fun `fromInt() parses 2`() {
        assert(Level.fromInt(2) == Level.INFO)
    }

    @Test
    fun `fromInt() parses 3`() {
        assert(Level.fromInt(3) == Level.WARN)
    }

    @Test
    fun `fromInt() parses 4`() {
        assert(Level.fromInt(4) == Level.ERROR)
    }

    @Test
    fun `fromInt() parses 5`() {
        assert(Level.fromInt(5) == Level.NONE)
    }
}