package com.example.meshapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaLogsTecnicos(
    volverAjustes: () -> Unit
) {

    val logs = listOf(
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT"
    )

    Scaffold(

        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFEAF1FF)
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    },
                    label = {
                        Text("Info")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Mensajes")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    },
                    label = {
                        Text("Ajustes")
                    }
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        volverAjustes()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF42A5F5)
                    )
                }

                Text(
                    text = "Logs Técnicos",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Logs de la red Mesh",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {
                items(logs) { log ->

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = log,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            }
        }
    }
}

