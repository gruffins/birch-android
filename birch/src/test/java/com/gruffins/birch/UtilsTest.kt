package com.gruffins.birch

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    @After
    fun teardown() {
        Birch.debug = false
    }

    @Test
    fun `currentTimestamp() returns a timestamp`() {
        assert(Utils.currentTimestamp != "")
    }

    @Test
    fun `hasOSVersion() returns true if greater or equal to given version`() {
        assert(Utils.hasOSVersion(0))
    }

    @Test
    fun `mapToString() returns a string version of the map`() {
        val map = mapOf("key" to "value")
        val result = Utils.mapToString(map)
        assert(result == "{\"key\":\"value\"}")
    }

    @Test
    fun `stringToMap() returns null if input is null`() {
        val result = Utils.stringToMap(null)
        assert(result == null)
    }

    @Test
    fun `stringToMap() returns correctly serialized map`() {
        val str = "{\"key\":\"value\"}"
        val result = Utils.stringToMap(str)
        assert(result!!["key"] == "value")
    }

    @Test
    fun `stringToMap() with invalid json returns null`() {
        val str = ""
        val result = Utils.stringToMap(str)
        assert(result == null)
    }

    @Test
    fun `safe() catches exceptions`() {
        Birch.debug = true

        val block = {
            throw RuntimeException("test")
        }

        Utils.safe(block)
    }
}