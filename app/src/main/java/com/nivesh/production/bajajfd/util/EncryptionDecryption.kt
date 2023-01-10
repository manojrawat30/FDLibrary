package com.nivesh.production.bajajfd.util

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.nivesh.production.bajajfd.util.Constants.Companion.passphrase
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionDecryption {
    val TAG = "Crypto"


    private var aesCipher: Cipher? = null
    private var secretKey: SecretKey? = null

    private var ivParameterSpec: IvParameterSpec? = null

    private val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private val CIPHER_ALGORITHM = "AES"

    // Replace me with a 16-byte key, share between Java and C#
    private val rawSecretKey = byteArrayOf(
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    )

    private val MESSAGEDIGEST_ALGORITHM = "MD5"

    @SuppressLint("NotConstructor")
    fun EncryptionDecryption() {
        val passwordKey =
            encodeDigest(passphrase) //new NativeClass().localName(KeyConstant.passphrase));
        try {
            aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "No such algorithm $CIPHER_ALGORITHM", e)
        } catch (e: NoSuchPaddingException) {
            Log.e(TAG, "No such padding PKCS5", e)
        }
        secretKey = SecretKeySpec(passwordKey, CIPHER_ALGORITHM)
        ivParameterSpec = IvParameterSpec(rawSecretKey)
    }

    fun encryptAsBase64(dataToEncrypt: String): String? {
        var dataToEncrypt = dataToEncrypt
        val dateFormat = SimpleDateFormat("SSS", Locale.US)
        dataToEncrypt = dataToEncrypt + dateFormat.format(Date())
        val encryptedData = encrypt(dataToEncrypt.toByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

//    public String decrypt(String textToDecrypt) throws Exception {
//
//
//        //SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
//        //Cipher cipher = Cipher.getInstance(cypherInstance);
//        //cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
//        byte[] decrypted = aesCipher.doFinal(encryted_bytes);
//        return new String(decrypted, "UTF-8");
//    }

    //    public String decrypt(String textToDecrypt) throws Exception {
    //
    //
    //        //SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
    //        //Cipher cipher = Cipher.getInstance(cypherInstance);
    //        //cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
    //        byte[] decrypted = aesCipher.doFinal(encryted_bytes);
    //        return new String(decrypted, "UTF-8");
    //    }
    fun decrypt(encryptString: String?): String? {
        var decryptedData = ""
        try {
            aesCipher!!.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "Invalid key", e)
            return null
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, "Invalid algorithm $CIPHER_ALGORITHM", e)
            return null
        }
        val decryptBytes: ByteArray
        try {
            val encryted_bytes = Base64.decode(encryptString, Base64.DEFAULT)
            decryptBytes = aesCipher!!.doFinal(encryted_bytes)
            decryptedData = String(decryptBytes)
            decryptedData = decryptedData.substring(0, decryptedData.length - 3)
        } catch (e: IllegalBlockSizeException) {
            Log.e(TAG, "Illegal block size", e)
            return null
        } catch (e: BadPaddingException) {
            Log.e(TAG, "Bad padding", e)
            return null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return decryptedData
    }

    fun encrypt(clearData: ByteArray?): ByteArray? {
        try {
            aesCipher!!.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "Invalid key", e)
            return null
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, "Invalid algorithm $CIPHER_ALGORITHM", e)
            return null
        }
        val encryptedData: ByteArray
        encryptedData = try {
            aesCipher!!.doFinal(clearData)
        } catch (e: IllegalBlockSizeException) {
            Log.e(TAG, "Illegal block size", e)
            return null
        } catch (e: BadPaddingException) {
            Log.e(TAG, "Bad padding", e)
            return null
        }
        return encryptedData
    }

    private fun encodeDigest(text: String): ByteArray? {
        val digest: MessageDigest
        try {
            digest = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM)
            return digest.digest(text.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "No such algorithm $MESSAGEDIGEST_ALGORITHM", e)
        }
        return null
    }
}