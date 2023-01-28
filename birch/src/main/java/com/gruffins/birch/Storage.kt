package com.gruffins.birch

import android.content.Context
import android.content.SharedPreferences
import com.gruffins.birch.Utils.mapToString
import com.gruffins.birch.Utils.stringToMap

internal class Storage(
    context: Context,
    directory: String,
    private val defaultLevel: Level
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("com.gruffins.$directory", 0)

    var uuid: String?
        get() = sharedPreferences.getString("uuid", null)
        set(value) {
            sharedPreferences.edit().putString("uuid", value).apply()
        }

    var identifier: String?
        get() = sharedPreferences.getString("identifier", null)
        set(value) {
            sharedPreferences.edit().putString("identifier", value).apply()
        }

    var customProperties: Map<String, String>?
        get() = stringToMap(sharedPreferences.getString("custom_properties", null))
        set(value) {
            value?.let {
                sharedPreferences.edit().putString("custom_properties", mapToString(it)).apply()
            } ?: run {
                sharedPreferences.edit().remove("custom_properties").apply()
            }
        }

    var logLevel: Level
        get() = Level.fromInt(sharedPreferences.getInt("log_level", defaultLevel.level))
        set(value) {
            sharedPreferences.edit().putInt("log_level", value.level).apply()
        }

    var flushPeriod: Long
        get() = sharedPreferences.getLong("flush_period", Engine.FLUSH_PERIOD_SECONDS)
        set(value) {
            sharedPreferences.edit().putLong("flush_period", value).apply()
        }
}