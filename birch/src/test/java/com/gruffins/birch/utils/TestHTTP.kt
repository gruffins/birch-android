package com.gruffins.birch.utils

import com.gruffins.birch.HTTP
import java.io.File
import java.net.URL

internal class TestHTTP(private val response: Response): HTTP() {
    override fun get(
        url: URL,
        headers: Map<String, String>,
        onResponse: (Response) -> Unit
    ) {
        onResponse(response)
    }

    override fun post(
        url: URL,
        body: ByteArray?,
        headers: Map<String, String>,
        onResponse: (Response) -> Unit
    ) {
        onResponse(response)
    }

    override fun postFile(
        url: URL,
        file: File,
        headers: Map<String, String>,
        onResponse: (Response) -> Unit
    ) {
        onResponse(response)
    }
}