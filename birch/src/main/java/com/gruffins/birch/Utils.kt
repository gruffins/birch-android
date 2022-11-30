package com.gruffins.birch

import android.os.Build
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

internal class Utils private constructor() {
    companion object {
        private const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        private val FORMATTER = SimpleDateFormat(ISO_8601_FORMAT, Locale.US).also { it.timeZone = TimeZone.getTimeZone("UTC") }
        private val lock = Object()

        val currentTimestamp: String
            get() = synchronized(lock) { FORMATTER.format(Date()) }

        fun hasOSVersion(version: Int): Boolean {
            return Build.VERSION.SDK_INT > version
        }

        fun mapToString(map: Map<String, String>): String {
            return JSONObject().also {
                map.forEach { entry ->
                    it.put(entry.key, entry.value)
                }
            }.toString()
        }

        fun stringToMap(str: String?): Map<String, String>? {
            if (str == null) {
                return null
            } else {
                return try {
                    val map = mutableMapOf<String, String>()
                    val json = JSONObject(str)

                    for (key in json.keys()) {
                        map[key] = json.getString(key)
                    }

                    map
                } catch (ex: JSONException) {
                    null
                }
            }
        }

        fun safe(lambda: () -> Unit) {
            try {
                lambda()
            } catch (ex: Exception) {
                if (Birch.debug) {
                    Birch.e { "[Birch] $ex" }
                }
            }
        }
    }
}