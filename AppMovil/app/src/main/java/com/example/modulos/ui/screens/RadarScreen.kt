package com.example.modulos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modulos.ui.components.BottomNavigationBar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RadarScreen(onNavigate: (String) -> Unit) {
    Scaffold(
        bottomBar = { BottomNavigationBar(currentRoute = "mensajes", onNavigate = onNavigate) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Radar de Dispositivos", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
            
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Generar algunos puntos aleatorios para dispositivos "rastreados"
                val devices = remember {
                    List(5) {
                        val angle = Random.nextDouble(0.0, 2 * Math.PI)
                        val radius = Random.nextDouble(20.0, 120.0)
                        Offset(
                            x = (radius * cos(angle)).toFloat(),
                            y = (radius * sin(angle)).toFloat()
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Dibujar círculos del radar
                    drawCircle(color = Color.Gray, radius = size.width / 2, center = center, style = Stroke(1f))
                    drawCircle(color = Color.Gray, radius = size.width / 3, center = center, style = Stroke(1f))
                    drawCircle(color = Color.Gray, radius = size.width / 6, center = center, style = Stroke(1f))
                    
                    // Dispositivo principal (Azul)
                    drawCircle(color = Color.Blue, radius = 15f, center = center)
                    
                    // Otros dispositivos (Rojos)
                    devices.forEach { deviceOffset ->
                        drawCircle(
                            color = Color.Red,
                            radius = 10f,
                            center = Offset(center.x + deviceOffset.x, center.y + deviceOffset.y)
                        )
                    }
                }
            }
        }
    }
}
