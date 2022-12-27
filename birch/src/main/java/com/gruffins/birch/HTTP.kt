package com.gruffins.birch

import java.io.DataOutputStream
import java.io.File
import java.net.URL
import java.util.UUID
import javax.net.ssl.HttpsURLConnection

internal open class HTTP {
    companion object {
        const val LINE = "\r\n"
    }

    open fun get(
        url: URL,
        headers: Map<String, String> = emptyMap(),
        onResponse: (Response) -> Unit
    ) {
        val connection = openConnection("GET", url, headers)
        try {
            onResponse(parseResponse(connection))
        } catch (ex: Exception) {
            onResponse(Response(-1, ""))
        } finally {
            connection.disconnect()
        }
    }

    open fun postFile(
        url: URL,
        file: File,
        headers: Map<String, String> = emptyMap(),
        onResponse: (Response) -> Unit
    ) {
        val boundary = UUID.randomUUID().toString()
        val combinedHeaders = mutableMapOf("Content-Type" to "multipart/form-data; boundary=$boundary") + headers
        val connection = openConnection("POST", url, combinedHeaders)
        val os = DataOutputStream(connection.outputStream)
        os.writeBytes("--$boundary$LINE")
        os.writeBytes("Content-Disposition: form-data; name=\"logs\"; filename=\"${file.name}.gz\"$LINE$LINE")
        os.write(Utils.compress(file))
        os.writeBytes("--$boundary--$LINE")
        os.flush()

        try {
            onResponse(parseResponse(connection))
        } catch (ex: Exception) {
            onResponse(Response(-1, ""))
        } finally {
            connection.disconnect()
        }
    }

    open fun post(
        url: URL,
        body: ByteArray?,
        headers: Map<String, String> = emptyMap(),
        onResponse: (Response) -> Unit
    ) {
        val connection = openConnection("POST", url, headers).also {
            body?.let { byteArr -> it.outputStream.write(byteArr) }
        }
        try {
            onResponse(parseResponse(connection))
        } catch (ex: Exception) {
            onResponse(Response(-1, ""))
        } finally {
            connection.disconnect()
        }
    }

    internal fun openConnection(method: String, url: URL, headers: Map<String, String>): HttpsURLConnection {
        return (url.openConnection() as HttpsURLConnection).also {
            it.requestMethod = method
            it.connectTimeout = 15_000
            it.readTimeout = 15_000
            it.useCaches = false
            it.setRequestProperty("Accept", "application/json")
            it.doInput = true
            it.doOutput = (method == "POST" || method == "PUT")

            headers.forEach { entry ->
                it.setRequestProperty(entry.key, entry.value)
            }
        }
    }

    internal fun parseResponse(connection: HttpsURLConnection): Response {
        val responseCode = connection.responseCode
        val responseBody = if (responseCode in 100..399) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream.bufferedReader().readText()
        }
        return Response(responseCode, responseBody)
    }

    class Response(private val statusCode: Int, val body: String) {
        val unauthorized: Boolean
            get() = statusCode == 401

        val success: Boolean
            get() = statusCode in 100..399

        val failure: Boolean
            get() = !success
    }
}