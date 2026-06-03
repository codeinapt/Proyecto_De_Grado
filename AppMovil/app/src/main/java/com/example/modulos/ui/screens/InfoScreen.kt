package com.example.modulos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modulos.ui.components.BottomNavigationBar

@Composable
fun InfoScreen(onNavigate: (String) -> Unit) {
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
            Text("Información", fontSize = 24.sp)
            Text("Detalles del sistema y dispositivos", modifier = Modifier.padding(16.dp))
        }
    }
}
