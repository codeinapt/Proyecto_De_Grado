package com.example.modulos.network

class FloodRouter(
    private val mySid: String,
    private val nearbyManager: INearbyManager,
    private val cache: PacketCache,
    private val forwarder: PacketForwarder,
    private val callback: RouterCallback
) {
    fun start() {
        // Asumimos que el callback del INearbyManager devuelve (senderEndpoint, packet)
        nearbyManager.setOnPacketReceivedListener { senderEndpoint, packet ->

            // 1. Verificar si ya lo vimos en el caché (evita bucles infinitos)
            if (cache.contains(packet.packetId)) {
                android.util.Log.d("FLOOD", "Paquete ${packet.packetId} ya en PacketCache -> descartar")
                return@setOnPacketReceivedListener
            }

            // 2. Agregar al caché
            cache.add(packet.packetId)
            android.util.Log.d("FLOOD", "Paquete ${packet.packetId} agregado a PacketCache")

            // 3. ¿Es para mí? (Zero-Knowledge: verificamos el SID de destino)
            if (packet.destinationSid == mySid) {
                android.util.Log.d("FLOOD", "Paquete recibido para mi SID. Tipo: ${packet.type}")
                when (packet.type) {
                    PacketType.MSG -> callback.onMessageReceived(packet)
                    PacketType.ACK -> callback.onAckReceived(packet)
                    PacketType.HELLO -> callback.onHelloReceived(packet)
                    PacketType.ANNOUNCE -> callback.onAnnounceReceived(packet)
                }
            } else {
                // 4. Si no es para mí, reenviarlo usando la función utilitaria (Multi-hop)
                android.util.Log.d("FLOOD", "Paquete no es para mí. Intentando reenviar...")
                forwarder.forward(packet, senderEndpoint)
            }
        }
    }
}