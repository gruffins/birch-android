package com.gruffins.birch

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    @After
    fun teardown() {
        Birch.debug = false
    }

    @Test
    fun `currentTimestamp() returns a timestamp`() {
        assert(Utils.currentTimestamp != "")
    }

    @Test
    fun `hasOSVersion() returns true if greater or equal to given version`() {
        assert(Utils.hasOSVersion(0))
    }

    @Test
    fun `mapToString() returns a string version of the map`() {
        val map = mapOf("key" to "value")
        val result = Utils.mapToString(map)
        assert(result == "{\"key\":\"value\"}")
    }

    @Test
    fun `stringToMap() returns null if input is null`() {
        val result = Utils.stringToMap(null)
        assert(result == null)
    }

    @Test
    fun `stringToMap() returns correctly serialized map`() {
        val str = "{\"key\":\"value\"}"
        val result = Utils.stringToMap(str)
        assert(result!!["key"] == "value")
    }

    @Test
    fun `stringToMap() with invalid json returns null`() {
        val str = ""
        val result = Utils.stringToMap(str)
        assert(result == null)
    }

    @Test
    fun `parsePublicKey() returns a public key with valid public key`() {
        val output = Utils.parsePublicKey("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvNd9jxohFki83udxjn1f\nFgg11FGQryDJBEYyR42+fiTJFBgBr5SYnaQTTkvK3lcPaGiZ/EcQ1OEm1ljeMSlM\nZlAoqR63pV9/Y4iKp/FwtQTrmQF9DdJ2CDPanbDcSLBIMhMEwa/qNPWkLn+5IXqF\n0HO/x8wN+CuLVmdqYb+K9g1mNch0STECj9YJuW3ca3Sk/huVf6QK1TRSS7QyCrXz\nFtp6I8XIqu1DCbPqvuwFOi37wSJe/VSyO7CRyQe921lqbDFm3WhZAL0HIMrZKe+l\n2/vpZRm491Qvygy3xN+se7ISwrKqbJtlVqMXjurRPvXMEB372GsfRBivnhu0vfNU\n0wIDAQAB\n-----END PUBLIC KEY-----\n")
        assert(output != null)
    }

    @Test
    fun `safe() catches exceptions`() {
        Birch.debug = true

        val block = {
            throw RuntimeException("test")
        }

        Utils.safe(block)
    }

    @Test
    fun `compress() returns a byte array less than original`() {
        val file = File("test.txt").also {
            it.createNewFile()
            it.writeBytes("test1234test1234".toByteArray())
        }

        assert(Utils.compress(file).isNotEmpty())
        file.delete()
    }
}