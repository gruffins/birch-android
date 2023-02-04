package com.gruffins.birch

import android.os.Build
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SourceTest {

    private lateinit var storage: Storage
    private lateinit var source: Source
    private lateinit var eventBus: EventBus

    @Before
    fun setup() {
        storage = Storage(RuntimeEnvironment.getApplication(), "birch", Level.ERROR)
        eventBus = EventBus()
        source = Source(RuntimeEnvironment.getApplication(), storage, eventBus)
    }

    @Test
    fun `uuid returns current uuid`() {
        assert(source.uuid == storage.uuid)
    }

    @Test
    fun `appVersion returns current app version`() {
        assert(source.appVersion == "")
    }

    @Test
    fun `appBuildNumber returns current build number`() {
        assert(source.appBuildNumber == "0")
    }

    @Test
    fun `brand returns current brand`() {
        assert(source.brand == Build.BRAND)
    }

    @Test
    fun `manufacturer returns current manufacturer`() {
        assert(source.manufacturer == Build.MANUFACTURER)
    }

    @Test
    fun `model returns current model`() {
        assert(source.model == Build.MODEL)
    }

    @Test
    fun `os returns Android`() {
        assert(source.os == "Android")
    }

    @Test
    fun `osVersion returns current OS version`() {
        assert(source.osVersion == Build.VERSION.SDK_INT.toString())
    }

    @Test
    fun `setting the identifier writes to storage`() {
        source.identifier = "test"
        assert(storage.identifier == source.identifier)
    }

    @Test
    fun `setting custom properties writes to storage`() {
        source.customProperties = mapOf("key" to "value")
        assert(storage.customProperties!!["key"] == "value")
    }

    @Test
    fun `toJson() serializes correctly`() {
        source.identifier = "identifier"
        source.customProperties = mapOf("key" to "value")
        val json = source.toJson()
        assert(json.getString("uuid") == source.uuid)
        assert(json.getString("app_version") == source.appVersion)
        assert(json.getString("app_build_number") == source.appBuildNumber)
        assert(json.getString("brand") == source.brand)
        assert(json.getString("manufacturer") == source.manufacturer)
        assert(json.getString("model") == source.model)
        assert(json.getString("os") == source.os)
        assert(json.getString("os_version") == source.osVersion)
        assert(json.getString("identifier") == source.identifier)
        assert(json.getString("package_name") == source.packageName)
        assert(json.getString("custom_property__key") == "value")
    }

    @Test
    fun `toJson() is cached`() {
        val json = source.toJson()
        assert(json === source.toJson())
    }
}