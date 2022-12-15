package com.gruffins.birch

import android.util.Base64
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@RunWith(RobolectricTestRunner::class)
class EncryptionTest {

    private lateinit var encryption: Encryption
    private lateinit var keyPair: KeyPair

    @Before
    fun setup() {
        keyPair = KeyPairGenerator.getInstance("RSA").genKeyPair()
        encryption = Encryption(keyPair.public)
    }

    @Test
    fun `it can decrypt a message`() {
        val encrypted = encryption.encrypt("secret")

        val rsaCipher = Cipher.getInstance("RSA").also {
            it.init(Cipher.DECRYPT_MODE, keyPair.private)
        }

        val encodedSecretKey = rsaCipher.doFinal(
            Base64.decode(encryption.encryptedKey, Base64.NO_WRAP)
        )

        val decryptedAESKey = SecretKeySpec(encodedSecretKey, 0, encodedSecretKey.size, "AES")

        val bytes = Base64.decode(encrypted, Base64.NO_WRAP)
        val iv = bytes.take(16).toByteArray()
        val message = bytes.drop(16).toByteArray()

        val aesCipher = Cipher.getInstance(Encryption.TRANSFORMATION).also {
            it.init(
                Cipher.DECRYPT_MODE,
                decryptedAESKey,
                IvParameterSpec(iv)
            )
        }

        val output = String(aesCipher.doFinal(message))

        assert(output == "secret")
    }

    @Test
    fun `create() throws exception invalid public key`() {
        try {
            Encryption.create("test")
            fail("Should have thrown InvalidPublicKeyException")
        } catch (_: Exception) {}
    }

    @Test
    fun `create() returns encryption for valid rsa public key`() {
        val publicKey = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE2aEVvZlV0VmY3dHhZMVZDNUhNSwpSVVpYRk1FNWN1V3lCTlJKZU1RRmlPK1NnWGlodGNESmx3VzhGeGJaQUlUTWF1azhzay9VTndTZlRXcWcxOXVqCkNrdklkaVVqaWdjSmQyQWZJd0pIWlRJUWRkUjh3dnhzSnNTYTJyVnl4ZUxNZ0VWNExXZGx5Q0l4VUJBWURlSy8KUWZScGJlT21xdmVBMGNDNlVGc2R4R1F0NEJiWVp2YjMycVlEU1c1OExMMXRiQThpN002dE5wZXpaY1JtOVhIWAo1c1dDNDc2RmRGRjhQVWU5a0RRSFpEc3cxK0dWM0RGeW9JWmE1WklSWmFuYkhxY2plRTlLWXgxclNHa1VkT3dhCncwb1piODZQUzlJT2E3cjNHNGxpaDZMdkRLRlAwamxvVHVqTFdUMzhBRzg3TzcrYTFXdHZjS2ZOUUt2OHU0S24KZXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg=="
        assert(Encryption.create(publicKey) != null)
    }
}