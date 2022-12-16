package com.gruffins.birch

import android.util.Base64
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class Encryption(
    private val publicKey: PublicKey
) {
    companion object {
        const val TRANSFORMATION = "AES/CBC/PKCS7Padding"

        fun create(publicKey: String): Encryption {
            try {
                return Encryption(
                    Utils.parsePublicKey(
                        String(Base64.decode(publicKey.toByteArray(), Base64.NO_WRAP))
                    )
                )
            } catch (ex: Exception) {
                throw Birch.InvalidPublicKeyException("Invalid public key")
            }
        }
    }

    private val secureRandom: SecureRandom = SecureRandom()
    private val symmetricKey: SecretKey = KeyGenerator.getInstance("AES")
        .also { it.init(256) }
        .generateKey()

    private val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)

    val encryptedKey: String = Base64.encodeToString(
        Cipher.getInstance("RSA/ECB/PKCS1Padding").also {
            it.init(Cipher.ENCRYPT_MODE, publicKey)
        }.doFinal(symmetricKey.encoded),
        Base64.NO_WRAP
    )

    fun encrypt(input: String): String {
        val iv = generateIV()

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(symmetricKey.encoded, "AES"),
            IvParameterSpec(iv)
        )

        return Base64.encodeToString(
            iv + cipher.doFinal(input.toByteArray()),
            Base64.NO_WRAP
        )
    }

    private fun generateIV(): ByteArray {
        return ByteArray(16).also { secureRandom.nextBytes(it) }
    }
}