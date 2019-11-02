package com.example.splitpayandroid.fingerprint

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import java.math.BigInteger
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

class FingerPrint(private val context: Context) {

    private val KEY_NAME = UUID.randomUUID().toString()

    val cryptoObject = FingerprintManagerCompat.CryptoObject(
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) newCiptherV23()
//        else newCipher()
        newCipher()
    )

    private fun newCipher(): Cipher{
        val cipher = Cipher.getInstance(
            "RSA/ECB/PKCS1Padding"
        )
        cipher.init(Cipher.ENCRYPT_MODE, getKeyPair().public)
        return cipher
    }

    @Suppress("DEPRECATION")
    private fun getKeyPair(): KeyPair{
        val endTime = Calendar.getInstance()
        endTime.add(Calendar.YEAR, 1)
        val spec = android.security.KeyPairGeneratorSpec.Builder(context)
            .setAlias("anEncryptionAlias")
            .setSubject(X500Principal("CN=AGH O=KI C=Poland"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(Calendar.getInstance().time)
            .setEndDate(endTime.time)
            .build()
        val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
        generator.initialize(spec)
        return generator.generateKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun newCiptherV23(): Cipher {
        val cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
            + KeyProperties.BLOCK_MODE_CBC + "/"
            + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher.init(Cipher.ENCRYPT_MODE, getKeyV23())
        return cipher
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getKeyV23(): Key {
        val keystore = KeyStore.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keystore.load(null)
        generateKeyV23()
        keystore.load(null)
        val key = keystore.getKey(KEY_NAME, null)
        return key
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKeyV23(){
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        keyGenerator.generateKey()
    }




}