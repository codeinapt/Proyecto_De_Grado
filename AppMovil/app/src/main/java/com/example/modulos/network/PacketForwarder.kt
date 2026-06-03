package com.example.modulos.network

class PacketForwarder(private val nearbyManager: INearbyManager) {

    // Ahora recibimos el senderEndpoint para no devolverle el paquete al vecino que nos lo dio
    fun forward(packet: Packet, senderEndpoint: String) {
        if (packet.ttl > 0) {
            packet.ttl -= 1

            // Reenvía a todos los vecinos, EXCEPTO al que nos lo envió
            // (La sintaxis exacta dependerá de cómo tu compañero programó el INearbyManager)
            nearbyManager.sendToAll(packet, senderEndpoint)

            android.util.Log.d("FLOOD", "Reenviando paquete ${packet.packetId}. Nuevo TTL: ${packet.ttl}. Excluyendo a: $senderEndpoint")
        } else {
            android.util.Log.d("FLOOD", "TTL agotado para ${packet.packetId}. No se reenvía.")
        }
    }
}