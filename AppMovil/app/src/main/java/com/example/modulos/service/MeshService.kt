package com.example.modulos.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.modulos.ui.components.NearbyManager

class MeshService : Service() {

    private lateinit var nearbyManager: NearbyManager

    override fun onCreate() {
        super.onCreate()
        nearbyManager = NearbyManager(this)
        
        nearbyManager.initialize(object : NearbyManager.NearbyEventListener {
            override fun onNodeDiscovered(node: NearbyManager.NodeInfo) {
                nearbyManager.connectToNode(node.endpointId)
            }
            override fun onNodeLost(node: NearbyManager.NodeInfo) {}
            override fun onNodeConnected(node: NearbyManager.NodeInfo) {}
            override fun onNodeDisconnected(node: NearbyManager.NodeInfo) {}
            override fun onMessageReceived(node: NearbyManager.NodeInfo, message: String) {
                // Recepción de P2P. Aquí se llama al FloodRouter
            }
            override fun onError(error: String) {}
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "MESH_SERVICE_CHANNEL")
            .setContentTitle("Red Mesh P2P")
            .setContentText("Conectividad resistente en segundo plano activada")
            .build()
        
        startForeground(1, notification)
        nearbyManager.startDiscovery()
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        nearbyManager.cleanup()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "MESH_SERVICE_CHANNEL",
                "Malla P2P de Supervivencia",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
