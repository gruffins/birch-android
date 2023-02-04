package com.gruffins.birch

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.*

@RunWith(RobolectricTestRunner::class)
class StorageTest {

    private lateinit var storage: Storage

    @Before
    fun setup() {
        storage = Storage(RuntimeEnvironment.getApplication(), "birch", Level.ERROR)
    }

    @Test
    fun `uuid() has valid getter and setter`() {
        val uuid = UUID.randomUUID().toString()
        storage.uuid = uuid
        assert(storage.uuid == uuid)
    }

    @Test
    fun `logLevel() has valid getter and setter`() {
        val level = Level.DEBUG
        storage.logLevel = level
        assert(storage.logLevel == level)
    }

    @Test
    fun `identifier() has valid getter and setter`() {
        val identifier = "identifier"
        storage.identifier = identifier
        assert(storage.identifier == identifier)
    }

    @Test
    fun `customProperties() has valid getter and setter`() {
        val map = mapOf("key" to "value")
        storage.customProperties = map
        assert(storage.customProperties!!["key"] == "value")
    }

    @Test
    fun `customProperties() clears if set to null`() {
        storage.customProperties = null
        assert(storage.customProperties == null)
    }

    @Test
    fun `flushPeriod() has valid getter and setter`() {
        storage.flushPeriod = 1L
        assert(storage.flushPeriod == 1L)
    }
}