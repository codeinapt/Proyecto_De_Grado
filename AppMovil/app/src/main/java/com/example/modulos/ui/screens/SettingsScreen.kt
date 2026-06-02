package com.example.modulos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modulos.ui.components.BottomNavigationBar

@Composable
fun SettingsScreen(onNavigate: (String) -> Unit, irALogs: () -> Unit) {

    val context = LocalContext.current

    var alias by remember { mutableStateOf("Nodo-Principal") }
    var frase by remember { mutableStateOf("*************") }
    var url by remember { mutableStateOf("https://344.43534.534534") }

    var estadoConexion by remember {
        mutableStateOf("Estado: Desconocido")
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(currentRoute = "ajustes", onNavigate = onNavigate) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            Text(
                text = "Ajustes",
                fontSize = 30.sp,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Alias del Nodo")

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Frase de recuperación")

            OutlinedTextField(
                value = frase,
                onValueChange = { frase = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("URL")

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Configuración guardada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4E5E8B)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Configuración")
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    estadoConexion = "Estado: Conectado correctamente"
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4E5E8B)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Probar Conexión")
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = estadoConexion,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    irALogs()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4E5E8B)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver logs técnicos")
            }
        }
    }
}
