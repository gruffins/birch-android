package com.gruffins.birch

import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.OutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@RunWith(RobolectricTestRunner::class)
class HTTPTest {

    @MockK(relaxed = true)
    private lateinit var connection: HttpsURLConnection
    private lateinit var http: HTTP

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        http = spyk(HTTP())
        every { http.openConnection(any(), any(), any()) } returns connection
    }

    @Test
    fun `get() with success`() {
        every { http.parseResponse(connection) } returns HTTP.Response(200, "{}")

        http.get(URL("https://localhost/"), emptyMap()) { response ->
            assert(response.success)
        }
    }

    @Test
    fun `get() with failure`() {
        every { http.parseResponse(connection) } returns HTTP.Response(400, "{}")

        http.get(URL("https://localhost/"), emptyMap()) { response ->
            assert(response.failure)
        }
    }

    @Test
    fun `get() with unauthorized`() {
        every { http.parseResponse(connection) } returns HTTP.Response(401, "{}")

        http.get(URL("https://localhost/"), emptyMap()) { response ->
            assert(response.unauthorized)
        }
    }

    @Test
    fun `get() with exception`() {
        every { http.parseResponse(connection) } throws RuntimeException()

        http.get(URL("https://localhost/"), emptyMap()) { response ->
            assert(response.failure)
        }
    }

    @Test
    fun `postFile() with response`() {
        val file = File("test").also { it.createNewFile() }
        val os = mockk<OutputStream>(relaxed = true)

        every { connection.outputStream } returns os
        every { http.parseResponse(connection) } returns HTTP.Response(201, "{}")

        http.postFile(URL("https://localhost/"), file, emptyMap()) { response ->
            assert(response.success)
        }
    }

    @Test
    fun `postFile() with exception`() {
        val file = File("test").also { it.createNewFile() }
        val os = mockk<OutputStream>(relaxed = true)

        every { connection.outputStream } returns os
        every { http.parseResponse(connection) } throws RuntimeException()

        http.postFile(URL("https://localhost/"), file, emptyMap()) { response ->
            assert(response.failure)
        }
    }

    @Test
    fun `post() with success`() {
        val body = "test".toByteArray()
        val os = mockk<OutputStream>(relaxed = true)

        every { connection.outputStream } returns os
        every { http.parseResponse(connection) } returns HTTP.Response(201, "{}")

        http.post(URL("https://localhost/"), body, emptyMap()) { response ->
            assert(response.success)
        }
    }

    @Test
    fun `post() with failure`() {
        val body = "test".toByteArray()
        val os = mockk<OutputStream>(relaxed = true)

        every { connection.outputStream } returns os
        every { http.parseResponse(connection) } returns HTTP.Response(401, "{}")

        http.post(URL("https://localhost/"), body, emptyMap()) { response ->
            assert(response.failure)
        }
    }

    @Test
    fun `post() with exception`() {
        val body = "test".toByteArray()
        val os = mockk<OutputStream>(relaxed = true)

        every { connection.outputStream } returns os
        every { http.parseResponse(connection) } throws RuntimeException()

        http.post(URL("https://localhost/"), body, emptyMap()) { response ->
            assert(response.failure)
        }
    }
}