package com.example.modulos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Información") },
            label = { Text("Información") },
            selected = currentRoute == "informacion",
            onClick = { onNavigate("informacion") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Mensajes") },
            label = { Text("Mensajes") },
            selected = currentRoute == "mensajes",
            onClick = { onNavigate("mensajes") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes") },
            selected = currentRoute == "ajustes",
            onClick = { onNavigate("ajustes") }
        )
    }
}
