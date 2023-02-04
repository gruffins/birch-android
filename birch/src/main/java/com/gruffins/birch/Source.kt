package com.gruffins.birch

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import com.gruffins.birch.Utils.hasOSVersion
import org.json.JSONObject
import java.util.*
import kotlin.properties.Delegates

internal class Source(
    context: Context,
    storage: Storage,
    eventBus: EventBus
) {

    val uuid: String
    val packageName: String
    val appVersion: String
    val appBuildNumber: String
    val brand: String
    val manufacturer: String
    val model: String
    val os: String
    val osVersion: String

    var identifier: String? by Delegates.observable(storage.identifier) { _, _, newValue ->
        storage.identifier = newValue
        cache = null
        eventBus.publish(EventBus.Event.SourceUpdated(this@Source))
    }

    var customProperties: Map<String, String>? by Delegates.observable(storage.customProperties) { _, _, newValue ->
        storage.customProperties = newValue
        cache = null
        eventBus.publish(EventBus.Event.SourceUpdated(this@Source))
    }

    private var cache: JSONObject? = null

    init {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)

        uuid = storage.uuid ?: UUID.randomUUID().toString()
        packageName = context.packageName
        appVersion = info.versionName ?: ""
        appBuildNumber = if (hasOSVersion(VERSION_CODES.P)) info.longVersionCode.toString() else info.versionCode.toString()
        brand = Build.BRAND
        manufacturer = Build.MANUFACTURER
        model = Build.MODEL
        os = "Android"
        osVersion = Build.VERSION.SDK_INT.toString()

        storage.uuid = uuid
    }

    fun toJson(): JSONObject {
       return cache ?: JSONObject().also { json ->
           json.put("uuid", uuid)
           json.put("package_name", packageName)
           json.put("app_version", appVersion)
           json.put("app_build_number", appBuildNumber)
           json.put("brand", brand)
           json.put("manufacturer", manufacturer)
           json.put("model", model)
           json.put("os", os)
           json.put("os_version", osVersion)
           json.put("identifier", identifier)

           customProperties?.forEach { entry ->
               json.put("custom_property__${entry.key}", entry.value)
           }
           cache = json
       }
    }
}
