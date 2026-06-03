package com.example.modulos.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.modulos.data.local.MessageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GatewayManager(
    private val context: Context,
    private val apiService: ApiService,
    private val messageDao: MessageDao,
    private val gatewayId: String
) {
    fun checkAndSync() {
        if (isInternetAvailable()) {
            Log.d("GatewayManager", "Internet detectado. Iniciando Sync UP...")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pending = messageDao.getPendingMessages()
                    if (pending.isNotEmpty()) {
                        val batch = pending.map {
                            mapOf(
                                "uuid_mensaje" to it.packetId,
                                "contenido_cifrado" to it.encryptedContent,
                                "emisor_id" to it.senderId,
                                "receptor_id" to it.receiverId,
                                "tipo_mensaje" to "TEXTO"
                            )
                        }
                        val request = SyncUpRequest(gatewayId, batch)
                        val response = apiService.syncUp(request)
                        
                        if (response.isSuccessful) {
                            pending.forEach { messageDao.updateMessageStatus(it.packetId, "SYNCED") }
                            Log.d("GatewayManager", "Sync UP exitoso. Mensajes marcados como SYNCED.")
                        } else {
                            Log.e("GatewayManager", "Error en Sync UP: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GatewayManager", "Excepción durante Sync UP", e)
                }
            }
        } else {
            Log.d("GatewayManager", "Sin internet. Mensajes permanecen en cola o en malla.")
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
