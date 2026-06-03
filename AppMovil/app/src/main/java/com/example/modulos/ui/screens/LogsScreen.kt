package com.example.modulos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modulos.ui.components.BottomNavigationBar

@Composable
fun LogsScreen(volverAjustes: () -> Unit, onNavigate: (String) -> Unit) {

    val logs = listOf(
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT",
        "23847238: ENDPOINT"
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(currentRoute = "ajustes", onNavigate = onNavigate) }
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
