package com.example.modulos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modulos.ui.components.BottomNavigationBar
import com.example.modulos.ui.components.NearbyManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RadarScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    var discoveredNodes by remember { mutableStateOf(listOf<NearbyManager.NodeInfo>()) }
    var connectionStatus by remember { mutableStateOf("Buscando nodos...") }

    // Instancia de uso
    val nearbyManager = remember { NearbyManager(context) }

    DisposableEffect(Unit) {
        val listener = object : NearbyManager.NearbyEventListener {
            override fun onNodeDiscovered(node: NearbyManager.NodeInfo) {
                discoveredNodes = nearbyManager.getDiscoveredNodes()
            }

            override fun onNodeLost(node: NearbyManager.NodeInfo) {
                discoveredNodes = nearbyManager.getDiscoveredNodes()
            }

            override fun onNodeConnected(node: NearbyManager.NodeInfo) {
                connectionStatus = "Conectado a ${node.name}"
                discoveredNodes = nearbyManager.getDiscoveredNodes()
            }

            override fun onNodeDisconnected(node: NearbyManager.NodeInfo) {
                connectionStatus = "Buscando nodos..."
                discoveredNodes = nearbyManager.getDiscoveredNodes()
            }

            override fun onMessageReceived(node: NearbyManager.NodeInfo, message: String) {
                // Para simplificar, no manejamos los mensajes en el radar por ahora
            }

            override fun onError(error: String) {
                connectionStatus = "Error: $error"
            }
        }

        nearbyManager.initialize(listener)
        nearbyManager.startDiscovery()

        onDispose {
            nearbyManager.cleanup()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(currentRoute = "informacion", onNavigate = onNavigate) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Radar de Dispositivos", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
            Text(connectionStatus, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Posiciones visuales aleatorizadas para los nodos descubiertos
                val deviceOffsets = remember(discoveredNodes.size) {
                    List(discoveredNodes.size) {
                        val angle = Random.nextDouble(0.0, 2 * Math.PI)
                        val radius = Random.nextDouble(30.0, 140.0)
                        Offset(
                            x = (radius * cos(angle)).toFloat(),
                            y = (radius * sin(angle)).toFloat()
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)

                    // Dibujar círculos concéntricos del radar
                    drawCircle(color = Color.Gray, radius = size.width / 2, center = center, style = Stroke(1f))
                    drawCircle(color = Color.Gray, radius = size.width / 3, center = center, style = Stroke(1f))
                    drawCircle(color = Color.Gray, radius = size.width / 6, center = center, style = Stroke(1f))

                    // Dispositivo principal (Tú - Centro Azul)
                    drawCircle(color = Color.Blue, radius = 15f, center = center)

                    // Referenciar dispositivo real encontrado en el Radar
                    deviceOffsets.forEachIndexed { index, offset ->
                        val devCenter = Offset(center.x + offset.x, center.y + offset.y)
                        val isConnected = discoveredNodes[index].isConnected

                        drawCircle(
                            // Verde si está conectado, Rojo si solo fue descubierto
                            color = if (isConnected) Color.Green else Color.Red,
                            radius = 12f,
                            center = devCenter
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista estructurada de los sub-nodos en cercanía
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                if (discoveredNodes.isEmpty()) {
                    Text("No hay dispositivos cercanos", color = Color.Gray)
                } else {
                    discoveredNodes.forEach { node ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(node.name)
                            Button(
                                onClick = {
                                    if(!node.isConnected) {
                                        nearbyManager.connectToNode(node.endpointId)
                                    } else {
                                        nearbyManager.disconnectFromNode(node.endpointId)
                                    }
                                }
                            ) {
                                Text(if (node.isConnected) "Desconectar" else "Conectar")
                            }
                        }
                    }
                }
            }
        }
    }
}
