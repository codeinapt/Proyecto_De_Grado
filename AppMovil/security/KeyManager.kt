// Generación automática de par de claves RSA (2048 bits)
// Clave AES para cifrado simétrico eficiente
// Almacenamiento seguro usando Android Keystore y EncryptedSharedPreferences
// Exportación/Importación de claves públicas para compartir con otros nodos
// Persistencia de claves entre reinicios de la aplicación

package com.redrural.wifi.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import javax.crypto.KeyGenerator

class KeyManager(private val context: Context) {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "red_rural_wifi_key_pair"
        private const val SHARED_PREFS_NAME = "secure_prefs"
        private const val MASTER_KEY_ALIAS = "master_key"
        private const val AES_KEY_ALIAS = "aes_key"
    }
    
    private val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val securePrefs = EncryptedSharedPreferences.create(
        context,
        SHARED_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * Genera un par de claves RSA para firma digital y cifrado asimétrico
     */
    fun generateKeyPair(): Boolean {
        return try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                KEYSTORE_PROVIDER
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT or
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setAlgorithmParameterSpec(
                    java.security.spec.RSAKeyGenParameterSpec(
                        2048,
                        java.security.spec.RSAKeyGenParameterSpec.F4
                    )
                )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyPairGenerator.initialize(keyGenParameterSpec)
            keyPairGenerator.generateKeyPair()
            
            // También generar clave AES para cifrado simétrico
            generateAESKey()
            
            true
        } catch (e: Exception) {
            println("Error al generar par de claves: ${e.message}")
            false
        }
    }
    
    /**
     * Genera una clave AES para cifrado simétrico
     */
    private fun generateAESKey(): Boolean {
        return try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                AES_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
            
            true
        } catch (e: Exception) {
            println("Error al generar clave AES: ${e.message}")
            false
        }
    }
    
    /**
     * Verifica si ya existe un par de claves
     */
    fun hasKeyPair(): Boolean {
        return keyStore.containsAlias(KEY_ALIAS) && keyStore.containsAlias(AES_KEY_ALIAS)
    }
    
    /**
     * Obtiene la clave pública RSA
     */
    fun getPublicKey(): PublicKey? {
        return try {
            val certificate = keyStore.getCertificate(KEY_ALIAS)
            certificate?.publicKey
        } catch (e: Exception) {
            println("Error al obtener clave pública: ${e.message}")
            null
        }
    }
    
    /**
     * Obtiene la clave privada RSA
     */
    fun getPrivateKey(): PrivateKey? {
        return try {
            keyStore.getKey(KEY_ALIAS, null) as? PrivateKey
        } catch (e: Exception) {
            println("Error al obtener clave privada: ${e.message}")
            null
        }
    }
    
    /**
     * Obtiene la clave AES para cifrado simétrico
     */
    fun getAESKey(): java.security.Key? {
        return try {
            keyStore.getKey(AES_KEY_ALIAS, null)
        } catch (e: Exception) {
            println("Error al obtener clave AES: ${e.message}")
            null
        }
    }
    
    /**
     * Guarda datos de forma segura en SharedPreferences
     */
    fun saveSecureData(key: String, value: String): Boolean {
        return try {
            securePrefs.edit().putString(key, value).apply()
            true
        } catch (e: Exception) {
            println("Error al guardar datos seguros: ${e.message}")
            false
        }
    }
    
    /**
     * Recupera datos guardados de forma segura
     */
    fun getSecureData(key: String): String? {
        return try {
            securePrefs.getString(key, null)
        } catch (e: Exception) {
            println("Error al recuperar datos seguros: ${e.message}")
            null
        }
    }
    
    /**
     * Elimina todas las claves criptográficas
     */
    fun deleteAllKeys(): Boolean {
        return try {
            keyStore.deleteEntry(KEY_ALIAS)
            keyStore.deleteEntry(AES_KEY_ALIAS)
            securePrefs.edit().clear().apply()
            true
        } catch (e: Exception) {
            println("Error al eliminar claves: ${e.message}")
            false
        }
    }
    
    /**
     * Exporta la clave pública en formato PEM para compartir con otros nodos
     */
    fun exportPublicKey(): String? {
        return try {
            val publicKey = getPublicKey()
            publicKey?.let {
                // Convertir a formato Base64 para transmisión
                android.util.Base64.encodeToString(it.encoded, android.util.Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            println("Error al exportar clave pública: ${e.message}")
            null
        }
    }
    
    /**
     * Importa una clave pública desde otro nodo
     */
    fun importPublicKey(pemKey: String): PublicKey? {
        return try {
            val keyBytes = android.util.Base64.decode(pemKey, android.util.Base64.NO_WRAP)
            val keyFactory = java.security.KeyFactory.getInstance("RSA")
            val x509EncodedKeySpec = java.security.spec.X509EncodedKeySpec(keyBytes)
            keyFactory.generatePublic(x509EncodedKeySpec)
        } catch (e: Exception) {
            println("Error al importar clave pública: ${e.message}")
            null
        }
    }
}
