package com.gruffins.birch

import android.net.Uri
import com.gruffins.birch.Utils.Companion.safe
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class Network(
    private val apiKey: String,
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "Birch-Network") },
    private val configuration: Configuration = Configuration(),
    private val http: HTTP = HTTP()
) {
    companion object {
        const val DEFAULT_HOST = "birch.ryanfung.com"
        var HOST = DEFAULT_HOST
    }

    fun uploadLogs(file: File, callback: (Boolean) -> Unit) {
        executorService.execute {
            if (Birch.debug) {
                Birch.d { "[Birch] Pushing logs ${file.name}." }
            }

            safe {
                http.postFile(
                    createURL(configuration.uploadPath),
                    file,
                    mapOf("X-API-Key" to apiKey)
                ) {
                    if (it.unauthorized) {
                        Birch.e { "[Birch] Invalid API key." }
                        callback(false)
                    } else {
                        if (Birch.debug) {
                            Birch.d { "[Birch] Upload logs responded. success=${it.success}"}
                        }
                        callback(it.success)
                    }
                }
            }
        }
    }

    fun syncSource(source: Source, callback: (() -> Unit)? = null) {
        executorService.execute {
            safe {
                if (Birch.debug) {
                    Birch.d { "[Birch] Pushing source." }
                }

                val payload = JSONObject().also {
                    it.put("source", source.toJson())
                }

                http.post(
                    createURL(configuration.sourcePath),
                    payload.toString().toByteArray(),
                    mapOf(
                        "X-API-Key" to apiKey,
                        "Content-Type" to "application/json"
                    ),
                ) {
                    if (it.unauthorized) {
                        Birch.e { "[Birch] Invalid API key" }
                    } else if (it.success) {
                        if (Birch.debug) {
                            Birch.d { "[Birch] Sync source responded. success=${it.success}" }
                        }
                        callback?.invoke()
                    }
                }
            }
        }
    }

    fun getConfiguration(source: Source, callback: (json: JSONObject) -> Unit) {
        executorService.execute {
            safe {
                if (Birch.debug) {
                    Birch.d { "[Birch] Fetching source configuration." }
                }

                http.get(
                    createURL(
                        String.format(
                            Locale.US,
                            configuration.configurationPath,
                            source.uuid
                        )
                    ),
                    mapOf(
                        "X-API-Key" to apiKey,
                        "Content-Type" to "application/json"
                    )
                ) {
                    if (it.unauthorized) {
                        Birch.e { "[Birch] Invalid API key" }
                    } else if (it.success) {
                        if (Birch.debug) {
                            Birch.d { "[Birch] Get configuration responded. success=${it.success}" }
                        }
                        val json = JSONObject(it.body)
                        callback(json.getJSONObject("source_configuration"))
                    }
                }
            }
        }
    }

    private fun createURL(path: String): URL {
        return URL(
            Uri.Builder().scheme("https").authority(configuration.host).path(path).toString()
        )
    }

    class Configuration(
        val host: String = HOST,
        val uploadPath: String = "/api/v1/logs",
        val sourcePath: String = "/api/v1/sources",
        val configurationPath: String = "/api/v1/sources/%s/configuration"
    )
}