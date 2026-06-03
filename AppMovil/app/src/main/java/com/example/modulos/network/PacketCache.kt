package com.example.modulos.network

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*

class PacketCache(private val scope: CoroutineScope) {
    // Estructura: key=packetId, value=timestamp
    private val cache = ConcurrentHashMap<String, Long>()

    init {
        // Corrutina para limpiar entradas viejas cada 60 seg
        scope.launch {
            while (isActive) {
                delay(60_000)
                val now = System.currentTimeMillis()
                cache.entries.removeIf { now - it.value > 60_000 }
                android.util.Log.d("FLOOD", "Limpieza de caché: ${cache.size} activos")
            }
        }
    }

    fun contains(packetId: String): Boolean = cache.containsKey(packetId)

    fun add(packetId: String) {
        cache[packetId] = System.currentTimeMillis()
    }
}
