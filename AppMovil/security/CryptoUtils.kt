// Cifrado RSA para mensajes cortos y distribución de claves
// Cifrado AES para mensajes largos (más eficiente)
// Firma digital SHA256withRSA para autenticación
// Verificación de integridad de mensajes
// Transmisión segura completa con firma y cifrado
// Hash SHA-256 para integridad de datos

package com.redrural.wifi.security

import android.util.Base64
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoUtils(private val keyManager: KeyManager) {
    
    companion object {
        private const val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12 // 96 bits
        private const val GCM_TAG_LENGTH = 16 // 128 bits
        private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        private const val KEY_AGREEMENT_ALGORITHM = "ECDH"
    }
    
    private val secureRandom = SecureRandom()
    
    /**
     * Cifra un mensaje usando la clave pública del destinatario (RSA)
     */
    fun encryptMessageRSA(message: String, recipientPublicKey: PublicKey): String? {
        return try {
            val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
            
            val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            println("Error al cifrar mensaje con RSA: ${e.message}")
            null
        }
    }
    
    /**
     * Descifra un mensaje usando la clave privada local (RSA)
     */
    fun decryptMessageRSA(encryptedMessage: String): String? {
        return try {
            val privateKey = keyManager.getPrivateKey() ?: return null
            
            val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            
            val encryptedBytes = Base64.decode(encryptedMessage, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            println("Error al descifrar mensaje con RSA: ${e.message}")
            null
        }
    }
    
    /**
     * Cifra un mensaje usando AES (simétrico) - más eficiente para mensajes largos
     */
    fun encryptMessageAES(message: String, aesKey: java.security.Key): EncryptedMessage? {
        return try {
            // Generar IV aleatorio
            val iv = ByteArray(GCM_IV_LENGTH)
            secureRandom.nextBytes(iv)
            
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec)
            
            val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
            
            EncryptedMessage(
                encryptedData = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP)
            )
        } catch (e: Exception) {
            println("Error al cifrar mensaje con AES: ${e.message}")
            null
        }
    }
    
    /**
     * Descifra un mensaje usando AES (simétrico)
     */
    fun decryptMessageAES(encryptedMessage: EncryptedMessage, aesKey: java.security.Key): String? {
        return try {
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = Base64.decode(encryptedMessage.iv, Base64.NO_WRAP)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec)
            
            val encryptedBytes = Base64.decode(encryptedMessage.encryptedData, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            println("Error al descifrar mensaje con AES: ${e.message}")
            null
        }
    }
    
    /**
     * Firma digitalmente un mensaje usando la clave privada
     */
    fun signMessage(message: String): String? {
        return try {
            val privateKey = keyManager.getPrivateKey() ?: return null
            
            val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
            signature.initSign(privateKey)
            signature.update(message.toByteArray(Charsets.UTF_8))
            
            val signatureBytes = signature.sign()
            Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            println("Error al firmar mensaje: ${e.message}")
            null
        }
    }
    
    /**
     * Verifica la firma digital de un mensaje usando la clave pública del remitente
     */
    fun verifySignature(message: String, signature: String, publicKey: PublicKey): Boolean {
        return try {
            val sig = Signature.getInstance(SIGNATURE_ALGORITHM)
            sig.initVerify(publicKey)
            sig.update(message.toByteArray(Charsets.UTF_8))
            
            val signatureBytes = Base64.decode(signature, Base64.NO_WRAP)
            sig.verify(signatureBytes)
        } catch (e: Exception) {
            println("Error al verificar firma: ${e.message}")
            false
        }
    }
    
    /**
     * Genera un hash SHA-256 de un mensaje
     */
    fun hashMessage(message: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(message.toByteArray(Charsets.UTF_8))
            
            // Convertir a representación hexadecimal
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            println("Error al generar hash: ${e.message}")
            ""
        }
    }
    
    /**
     * Cifra un mensaje para transmisión segura (incluye firma)
     */
    fun encryptForTransmission(message: String, recipientPublicKey: PublicKey): SecureMessage? {
        return try {
            // Generar firma digital
            val signature = signMessage(message) ?: return null
            
            // Cifrar el mensaje
            val encryptedContent = encryptMessageRSA(message, recipientPublicKey) ?: return null
            
            SecureMessage(
                encryptedContent = encryptedContent,
                signature = signature,
                timestamp = System.currentTimeMillis(),
                senderPublicKey = keyManager.exportPublicKey() ?: return null
            )
        } catch (e: Exception) {
            println("Error al preparar mensaje seguro: ${e.message}")
            null
        }
    }
    
    /**
     * Descifra y verifica un mensaje recibido
     */
    fun decryptFromTransmission(secureMessage: SecureMessage): String? {
        return try {
            // Importar clave pública del remitente
            val senderPublicKey = keyManager.importPublicKey(secureMessage.senderPublicKey) ?: return null
            
            // Descifrar el contenido
            val decryptedContent = decryptMessageRSA(secureMessage.encryptedContent) ?: return null
            
            // Verificar la firma
            if (!verifySignature(decryptedContent, secureMessage.signature, senderPublicKey)) {
                println("Error: La firma digital no es válida")
                return null
            }
            
            decryptedContent
        } catch (e: Exception) {
            println("Error al procesar mensaje seguro: ${e.message}")
            null
        }
    }
    
    /**
     * Genera una clave secreta compartida usando Diffie-Hellman
     */
    fun generateSharedSecret(peerPublicKey: PublicKey): ByteArray? {
        return try {
            val privateKey = keyManager.getPrivateKey() ?: return null
            
            val keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM)
            keyAgreement.init(privateKey)
            keyAgreement.doPhase(peerPublicKey, true)
            
            keyAgreement.generateSecret()
        } catch (e: Exception) {
            println("Error al generar secreto compartido: ${e.message}")
            null
        }
    }
    
    /**
     * Genera una clave AES a partir de un secreto compartido
     */
    fun deriveAESKey(sharedSecret: ByteArray): java.security.Key {
        // Usar los primeros 32 bytes del secreto compartido para AES-256
        val keyBytes = if (sharedSecret.size >= 32) {
            sharedSecret.sliceArray(0..31)
        } else {
            // Si el secreto es más corto, usar hash para extenderlo
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            digest.digest(sharedSecret)
        }
        
        return SecretKeySpec(keyBytes, "AES")
    }
}

/**
 * Clases de datos para mensajes cifrados
 */
data class EncryptedMessage(
    val encryptedData: String,
    val iv: String
)

data class SecureMessage(
    val encryptedContent: String,
    val signature: String,
    val timestamp: Long,
    val senderPublicKey: String
)
