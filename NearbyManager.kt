package com.redrural.wifi.connection

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.Task
import java.util.*

/**
 * NearbyManager - Gestiona el descubrimiento y conexión con nodos cercanos
 * Implementa Google Nearby Connections API para comunicación peer-to-peer
 */
class NearbyManager(private val context: Context) {
    
    companion object {
        private const val TAG = "NearbyManager"
        private const val SERVICE_ID = "com.redrural.wifi.SERVICE"
        private val STRATEGY = com.google.android.gms.nearby.connection.Strategy.P2P_STAR
    }
    
    // Callbacks para eventos de conexión
    private lateinit var connectionLifecycleCallback: ConnectionLifecycleCallback
    private lateinit var endpointDiscoveryCallback: EndpointDiscoveryCallback
    private lateinit var payloadCallback: PayloadCallback
    
    // Lista de nodos descubiertos y conectados
    private val discoveredNodes = mutableMapOf<String, NodeInfo>()
    private val connectedNodes = mutableMapOf<String, NodeInfo>()
    
    // Listener para eventos de la aplicación
    private var eventListener: NearbyEventListener? = null
    
    /**
     * Información de un nodo en la red
     */
    data class NodeInfo(
        val endpointId: String,
        val name: String,
        val serviceId: String = SERVICE_ID,
        var isConnected: Boolean = false
    )
    
    /**
     * Interface para escuchar eventos de NearbyManager
     */
    interface NearbyEventListener {
        fun onNodeDiscovered(node: NodeInfo)
        fun onNodeLost(node: NodeInfo)
        fun onNodeConnected(node: NodeInfo)
        fun onNodeDisconnected(node: NodeInfo)
        fun onMessageReceived(node: NodeInfo, message: String)
        fun onError(error: String)
    }
    
    /**
     * Inicializa el servicio de descubrimiento de nodos
     */
    fun initialize(listener: NearbyEventListener): Boolean {
        return try {
            this.eventListener = listener
            setupCallbacks()
            Log.d(TAG, "NearbyManager inicializado correctamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar NearbyManager", e)
            eventListener?.onError("Error al inicializar: ${e.message}")
            false
        }
    }
    
    /**
     * Configura los callbacks para eventos de Nearby Connections
     */
    private fun setupCallbacks() {
        // Callback para ciclo de vida de conexiones
        connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                Log.d(TAG, "Conexión iniciada con: $endpointId")
                
                // Aceptar automáticamente todas las conexiones
                // En producción, deberías mostrar una UI para confirmar
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            }
            
            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.SUCCESS -> {
                        Log.d(TAG, "Conexión exitosa con: $endpointId")
                        handleConnectionSuccess(endpointId)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        Log.d(TAG, "Conexión rechazada por: $endpointId")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Log.e(TAG, "Error de conexión con: $endpointId")
                        eventListener?.onError("Error de conexión con $endpointId")
                    }
                    else -> {
                        Log.w(TAG, "Estado desconocido de conexión: ${result.status.statusCode}")
                    }
                }
            }
            
            override fun onDisconnected(endpointId: String) {
                Log.d(TAG, "Desconectado de: $endpointId")
                handleDisconnection(endpointId)
            }
        }
        
        // Callback para descubrimiento de endpoints
        endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
                Log.d(TAG, "Nodo descubierto: $endpointId - ${discoveredEndpointInfo.endpointName}")
                
                val nodeInfo = NodeInfo(
                    endpointId = endpointId,
                    name = discoveredEndpointInfo.endpointName
                )
                
                discoveredNodes[endpointId] = nodeInfo
                eventListener?.onNodeDiscovered(nodeInfo)
            }
            
            override fun onEndpointLost(endpointId: String) {
                Log.d(TAG, "Nodo perdido: $endpointId")
                val nodeInfo = discoveredNodes.remove(endpointId)
                if (nodeInfo != null) {
                    eventListener?.onNodeLost(nodeInfo)
                }
            }
        }
        
        // Callback para recepción de payloads (mensajes)
        payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                when (payload.type) {
                    Payload.Type.BYTES -> {
                        val message = String(payload.asBytes()!!)
                        Log.d(TAG, "Mensaje recibido de $endpointId: $message")
                        
                        connectedNodes[endpointId]?.let { node ->
                            eventListener?.onMessageReceived(node, message)
                        }
                    }
                    else -> {
                        Log.w(TAG, "Payload tipo no soportado: ${payload.type}")
                    }
                }
            }
            
            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                // Manejar actualizaciones de transferencia si es necesario
                Log.v(TAG, "Transferencia update de $endpointId: ${update.status}")
            }
        }
    }
    
    /**
     * Comienza a buscar nodos cercanos
     */
    fun startDiscovery(): Boolean {
        return try {
            if (!::endpointDiscoveryCallback.isInitialized) {
                Log.e(TAG, "Callbacks no inicializados")
                return false
            }
            
            val advertisingOptions = AdvertisingOptions.Builder()
                .setStrategy(com.google.android.gms.nearby.connection.Strategy.P2P_STAR)
                .build()
            
            Nearby.getConnectionsClient(context)
                .startAdvertising(
                    "RedRural-${UUID.randomUUID().toString().substring(0, 8)}",
                    SERVICE_ID,
                    connectionLifecycleCallback,
                    advertisingOptions
                )
                .addOnSuccessListener {
                    Log.d(TAG, "Advertising iniciado correctamente")
                    startDiscoveryInternal()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al iniciar advertising", e)
                    eventListener?.onError("Error al iniciar advertising: ${e.message}")
                }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar descubrimiento", e)
            eventListener?.onError("Error al iniciar descubrimiento: ${e.message}")
            false
        }
    }
    
    /**
     * Inicia el descubrimiento interno de endpoints
     */
    private fun startDiscoveryInternal() {
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(com.google.android.gms.nearby.connection.Strategy.P2P_STAR)
            .build()
        
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                Log.d(TAG, "Descubrimiento iniciado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al iniciar descubrimiento", e)
                eventListener?.onError("Error al iniciar descubrimiento: ${e.message}")
            }
    }
    
    /**
     * Detiene el descubrimiento de nodos
     */
    fun stopDiscovery() {
        try {
            Nearby.getConnectionsClient(context).stopAdvertising()
            Nearby.getConnectionsClient(context).stopDiscovery()
            Log.d(TAG, "Descubrimiento detenido")
        } catch (e: Exception) {
            Log.e(TAG, "Error al detener descubrimiento", e)
        }
    }
    
    /**
     * Conecta con un nodo específico
     */
    fun connectToNode(nodeId: String): Boolean {
        return try {
            val nodeInfo = discoveredNodes[nodeId]
            if (nodeInfo == null) {
                Log.e(TAG, "Nodo no encontrado: $nodeId")
                return false
            }
            
            Log.d(TAG, "Iniciando conexión con: $nodeId")
            
            val connectionOptions = ConnectionOptions.Builder()
                .build()
            
            Nearby.getConnectionsClient(context)
                .requestConnection(
                    "RedRural-${UUID.randomUUID().toString().substring(0, 8)}",
                    nodeId,
                    connectionLifecycleCallback,
                    connectionOptions
                )
                .addOnSuccessListener {
                    Log.d(TAG, "Solicitud de conexión enviada a: $nodeId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al solicitar conexión con $nodeId", e)
                    eventListener?.onError("Error al conectar con $nodeId: ${e.message}")
                }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al conectar con nodo: $nodeId", e)
            eventListener?.onError("Error al conectar: ${e.message}")
            false
        }
    }
    
    /**
     * Desconecta de un nodo
     */
    fun disconnectFromNode(nodeId: String) {
        try {
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(nodeId)
            Log.d(TAG, "Desconectando del nodo: $nodeId")
        } catch (e: Exception) {
            Log.e(TAG, "Error al desconectar del nodo: $nodeId", e)
        }
    }
    
    /**
     * Envía un mensaje a un nodo conectado
     */
    fun sendMessage(nodeId: String, message: String): Boolean {
        return try {
            if (!connectedNodes.containsKey(nodeId)) {
                Log.e(TAG, "Nodo no conectado: $nodeId")
                return false
            }
            
            val payload = Payload.fromBytes(message.toByteArray())
            
            Nearby.getConnectionsClient(context)
                .sendPayload(nodeId, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "Mensaje enviado a $nodeId: $message")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al enviar mensaje a $nodeId", e)
                    eventListener?.onError("Error al enviar mensaje: ${e.message}")
                }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar mensaje", e)
            eventListener?.onError("Error al enviar mensaje: ${e.message}")
            false
        }
    }
    
    /**
     * Envía un mensaje a todos los nodos conectados
     */
    fun broadcastMessage(message: String): Boolean {
        var success = true
        connectedNodes.keys.forEach { nodeId ->
            success = sendMessage(nodeId, message) && success
        }
        return success
    }
    
    /**
     * Maneja una conexión exitosa
     */
    private fun handleConnectionSuccess(endpointId: String) {
        val nodeInfo = discoveredNodes[endpointId] ?: return
        nodeInfo.isConnected = true
        connectedNodes[endpointId] = nodeInfo
        eventListener?.onNodeConnected(nodeInfo)
    }
    
    /**
     * Maneja una desconexión
     */
    private fun handleDisconnection(endpointId: String) {
        val nodeInfo = connectedNodes.remove(endpointId)
        if (nodeInfo != null) {
            nodeInfo.isConnected = false
            eventListener?.onNodeDisconnected(nodeInfo)
        }
    }
    
    /**
     * Obtiene la lista de nodos descubiertos
     */
    fun getDiscoveredNodes(): List<NodeInfo> {
        return discoveredNodes.values.toList()
    }
    
    /**
     * Obtiene la lista de nodos conectados
     */
    fun getConnectedNodes(): List<NodeInfo> {
        return connectedNodes.values.toList()
    }
    
    /**
     * Verifica si hay nodos conectados
     */
    fun hasConnectedNodes(): Boolean {
        return connectedNodes.isNotEmpty()
    }
    
    /**
     * Limpia todos los recursos
     */
    fun cleanup() {
        try {
            stopDiscovery()
            connectedNodes.keys.forEach { nodeId ->
                disconnectFromNode(nodeId)
            }
            discoveredNodes.clear()
            connectedNodes.clear()
            Log.d(TAG, "Recursos limpiados")
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar recursos", e)
        }
    }
}
