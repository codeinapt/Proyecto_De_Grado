// Pruebas completas de todos los componentes
// Verificación automática de que todo funciona correctamente
// Reporte de estado detallado del sistema de seguridad

package com.example.modulos.utils

/**
 * Clase de prueba para verificar el funcionamiento de los componentes de seguridad
 */
class SecurityTest(private val keyManager: KeyManager, private val cryptoUtils: CryptoUtils) {
    
    /**
     * Prueba completa del sistema de seguridad
     */
    fun runCompleteTest(): Boolean {
        println("=== INICIANDO PRUEBA DE SEGURIDAD ===")
        
        // 1. Verificar generación de claves
        if (!testKeyGeneration()) {
            println("ERROR: Falló la prueba de generación de claves")
            return false
        }
        println("OK: Generación de claves funciona")
        
        // 2. Verificar cifrado/descifrado RSA
        if (!testRSAEncryption()) {
            println("ERROR: Falló la prueba de cifrado RSA")
            return false
        }
        println("OK: Cifrado RSA funciona")
        
        // 3. Verificar firma digital
        if (!testDigitalSignature()) {
            println("ERROR: Falló la prueba de firma digital")
            return false
        }
        println("OK: Firma digital funciona")
        
        // 4. Verificar transmisión segura
        if (!testSecureTransmission()) {
            println("ERROR: Falló la prueba de transmisión segura")
            return false
        }
        println("OK: Transmisión segura funciona")
        
        println("=== TODAS LAS PRUEBAS DE SEGURIDAD PASARON ===")
        return true
    }
    
    /**
     * Prueba la generación de claves
     */
    private fun testKeyGeneration(): Boolean {
        return try {
            // Verificar si ya existen claves
            val hasKeys = keyManager.hasKeyPair()
            println("Claves existentes: $hasKeys")
            
            // Si no existen, generar nuevas
            if (!hasKeys) {
                val generated = keyManager.generateKeyPair()
                println("Claves generadas: $generated")
                
                if (!generated) return false
            }
            
            // Verificar que podemos obtener las claves
            val publicKey = keyManager.getPublicKey()
            val privateKey = keyManager.getPrivateKey()
            
            publicKey != null && privateKey != null
        } catch (e: Exception) {
            println("Error en prueba de claves: ${e.message}")
            false
        }
    }
    
    /**
     * Prueba el cifrado y descifrado RSA
     */
    private fun testRSAEncryption(): Boolean {
        return try {
            val publicKey = keyManager.getPublicKey() ?: return false
            
            val originalMessage = "Este es un mensaje de prueba para la red rural WiFi"
            println("Mensaje original: $originalMessage")
            
            // Cifrar
            val encrypted = cryptoUtils.encryptMessageRSA(originalMessage, publicKey)
            println("Mensaje cifrado: $encrypted")
            
            if (encrypted == null) return false
            
            // Descifrar
            val decrypted = cryptoUtils.decryptMessageRSA(encrypted)
            println("Mensaje descifrado: $decrypted")
            
            originalMessage == decrypted
        } catch (e: Exception) {
            println("Error en prueba RSA: ${e.message}")
            false
        }
    }
    
    /**
     * Prueba la firma digital y verificación
     */
    private fun testDigitalSignature(): Boolean {
        return try {
            val message = "Mensaje para firmar digitalmente"
            println("Mensaje a firmar: $message")
            
            // Firmar
            val signature = cryptoUtils.signMessage(message)
            println("Firma generada: $signature")
            
            if (signature == null) return false
            
            // Verificar
            val publicKey = keyManager.getPublicKey() ?: return false
            val isValid = cryptoUtils.verifySignature(message, signature, publicKey)
            println("Firma válida: $isValid")
            
            // Intentar verificar con mensaje modificado (debe fallar)
            val modifiedMessage = message + " modificado"
            val isInvalid = cryptoUtils.verifySignature(modifiedMessage, signature, publicKey)
            println("Verificación con mensaje modificado: $isInvalid")
            
            isValid && !isInvalid
        } catch (e: Exception) {
            println("Error en prueba de firma: ${e.message}")
            false
        }
    }
    
    /**
     * Prueba la transmisión segura completa
     */
    private fun testSecureTransmission(): Boolean {
        return try {
            val originalMessage = "Mensaje seguro para transmisión entre nodos"
            println("Mensaje original: $originalMessage")
            
            // Simular dos nodos: emisor y receptor
            val emitterPublicKey = keyManager.getPublicKey() ?: return false
            
            // Preparar mensaje para transmisión
            val secureMessage = cryptoUtils.encryptForTransmission(originalMessage, emitterPublicKey)
            println("Mensaje seguro preparado: ${secureMessage != null}")
            
            if (secureMessage == null) return false
            
            // Procesar mensaje recibido
            val decryptedMessage = cryptoUtils.decryptFromTransmission(secureMessage)
            println("Mensaje recibido: $decryptedMessage")
            
            originalMessage == decryptedMessage
        } catch (e: Exception) {
            println("Error en prueba de transmisión: ${e.message}")
            false
        }
    }
    
    /**
     * Prueba el almacenamiento seguro de datos
     */
    fun testSecureStorage(): Boolean {
        return try {
            val testData = "Dato sensible para almacenar"
            val testKey = "test_secure_key"
            
            // Guardar
            val saved = keyManager.saveSecureData(testKey, testData)
            println("Dato guardado: $saved")
            
            if (!saved) return false
            
            // Recuperar
            val retrieved = keyManager.getSecureData(testKey)
            println("Dato recuperado: $retrieved")
            
            testData == retrieved
        } catch (e: Exception) {
            println("Error en prueba de almacenamiento: ${e.message}")
            false
        }
    }
    
    /**
     * Genera un reporte del estado actual del sistema de seguridad
     */
    fun generateSecurityReport(): String {
        val report = StringBuilder()
        report.appendLine("=== REPORTE DE ESTADO DE SEGURIDAD ===")
        report.appendLine("Timestamp: ${System.currentTimeMillis()}")
        report.appendLine()
        
        // Estado de claves
        report.appendLine("ESTADO DE CLAVES:")
        report.appendLine("- Tiene par de claves: ${keyManager.hasKeyPair()}")
        report.appendLine("- Clave pública disponible: ${keyManager.getPublicKey() != null}")
        report.appendLine("- Clave privada disponible: ${keyManager.getPrivateKey() != null}")
        report.appendLine("- Clave AES disponible: ${keyManager.getAESKey() != null}")
        report.appendLine()
        
        // Exportar clave pública
        val publicKey = keyManager.exportPublicKey()
        report.appendLine("CLAVE PÚBLICA (Base64):")
        report.appendLine(publicKey ?: "No disponible")
        report.appendLine()
        
        // Hash de ejemplo
        val testMessage = "mensaje de prueba"
        val hash = cryptoUtils.hashMessage(testMessage)
        report.appendLine("EJEMPLO DE HASH SHA-256:")
        report.appendLine("Mensaje: $testMessage")
        report.appendLine("Hash: $hash")
        
        return report.toString()
    }
}
