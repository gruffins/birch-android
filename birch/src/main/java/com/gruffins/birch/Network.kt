package com.gruffins.birch

import android.net.Uri
import com.gruffins.birch.Utils.safe
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.*

internal class Network(
    private val agent: Agent,
    private val host: String,
    private val apiKey: String,
    private val http: HTTP = HTTP()
) {
    companion object {
        const val UPLOAD_PATH = "/api/v1/logs"
        const val SOURCE_PATH = "/api/v1/sources"
        const val CONFIGURATION_PATH = "/api/v1/sources/%s/configuration"
    }

    fun uploadLogs(file: File, callback: (Boolean) -> Unit) {
        safe {
            if (agent.debug) {
                agent.d { "[Birch] Pushing logs ${file.name}." }
            }
            http.postFile(
                createURL(UPLOAD_PATH),
                file,
                mapOf("X-API-Key" to apiKey)
            ) {
                if (it.unauthorized) {
                    agent.e { "[Birch] Invalid API key." }
                    callback(false)
                } else {
                    if (agent.debug) {
                        agent.d { "[Birch] Upload logs responded. success=${it.success}"}
                    }
                    callback(it.success)
                }
            }
        }
    }

    fun syncSource(source: Source, callback: (() -> Unit)? = null) {
            safe {
                if (agent.debug) {
                    agent.d { "[Birch] Pushing source." }
                }

                val payload = JSONObject().also {
                    it.put("source", source.toJson())
                }

                http.post(
                    createURL(SOURCE_PATH),
                    payload.toString().toByteArray(),
                    mapOf(
                        "X-API-Key" to apiKey,
                        "Content-Type" to "application/json"
                    ),
                ) {
                    if (it.unauthorized) {
                        agent.e { "[Birch] Invalid API key" }
                    } else if (it.success) {
                        if (agent.debug) {
                            agent.d { "[Birch] Sync source responded. success=${it.success}" }
                        }
                        callback?.invoke()
                    }
                }
            }
    }

    fun getConfiguration(source: Source, callback: (json: JSONObject) -> Unit) {
        safe {
            if (agent.debug) {
                agent.d { "[Birch] Fetching source configuration." }
            }

            http.get(
                createURL(
                    String.format(
                        Locale.US,
                        CONFIGURATION_PATH,
                        source.uuid
                    )
                ),
                mapOf(
                    "X-API-Key" to apiKey,
                    "Content-Type" to "application/json"
                )
            ) {
                if (it.unauthorized) {
                    agent.e { "[Birch] Invalid API key" }
                } else if (it.success) {
                    if (agent.debug) {
                        agent.d { "[Birch] Get configuration responded. success=${it.success}" }
                    }
                    val json = JSONObject(it.body)
                    callback(json.getJSONObject("source_configuration"))
                }
            }
        }
    }

    private fun createURL(path: String): URL {
        return URL(
            Uri.Builder().scheme("https").authority(host).path(path).toString()
        )
    }
}