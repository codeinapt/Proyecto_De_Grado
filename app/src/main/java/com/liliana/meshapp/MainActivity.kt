package com.liliana.meshapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liliana.meshapp.ui.theme.MeshAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeshAppTheme {
                AppController()
            }
        }
    }
}

@Composable
fun AppController() {
    var pantallaActual by remember { mutableStateOf(0) }

    when (pantallaActual) {
        0 -> PantallaBienvenida(
            irAlDashboard = { pantallaActual = 1 },
            irAConfig = { pantallaActual = 2 }
        )
        1 -> PantallaPrincipal(
            irAConfig = { pantallaActual = 2 }
        )
        2 -> PantallaConfiguracion(
            volverAtras = { pantallaActual = 1 }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBienvenida(irAlDashboard: () -> Unit, irAConfig: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var recoveryPhrase by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            IconSpeechBubble(modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Bienvenido a MeshApp", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tu identidad es privada", fontSize = 14.sp, color = Color(0xFF757575))
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF9E9E9E),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color.White) {
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("Nombre de usuario", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color.White) {
                        TextField(
                            value = recoveryPhrase,
                            onValueChange = { recoveryPhrase = it },
                            placeholder = { Text("Frase de recuperacion", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = irAlDashboard,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                            shape = RoundedCornerShape(6.dp),
                            content = { Text("Empezar", color = Color.White) }
                        )
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(bottomEnd = 12.dp, topEnd = 6.dp, topStart = 6.dp, bottomStart = 6.dp),
                            content = { Text("Recuperar", color = Color.White) }
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = irAConfig,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 60.dp, end = 16.dp).size(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Configurar", tint = Color.Gray, modifier = Modifier.size(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(irAConfig: () -> Unit) {
    Scaffold(
        bottomBar = {
            // CORREGIDO: Usar NavigationBar en lugar de BottomNavigation
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
                    label = { Text("Información", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Message, contentDescription = "Mensajes") },
                    label = { Text("Mensajes", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = irAConfig,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes", fontSize = 12.sp) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Direccion IP:", fontSize = 16.sp, color = Color.Black, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Dispositivos Conectados:", fontSize = 16.sp, color = Color.Black, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Estado:", fontSize = 16.sp, color = Color.Black, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))
            Text("Mensajes no leidos/Notificaciones", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            repeat(3) { index ->
                Surface(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (index == 2) Color(0xFFBDBDBD) else Color(0xFFE0E0E0)
                ) {
                    Box(contentAlignment = Alignment.CenterStart) {
                        Text("  Usuario", color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.7f).height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Ver Radar de vecinos", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(volverAtras: () -> Unit) {
    var serverUrl by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f).height(240.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF9E9E9E),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Configurar Backend", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Ingresa la URL de tu servidor Red Mesh", color = Color.White, fontSize = 14.sp, lineHeight = 18.sp)

                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color.White) {
                    TextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = volverAtras,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text("Cancelar", color = Color.White) }
                    )
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp),
                        content = { Text("Guardar", color = Color.White) }
                    )
                }
            }
        }
    }
}

@Composable
fun IconSpeechBubble(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 5.dp.toPx()
        val circlePath = Path().apply { addOval(Rect(width * 0.15f, height * 0.10f, width * 0.85f, height * 0.75f)) }
        val tailPath = Path().apply {
            moveTo(width * 0.60f, height * 0.65f)
            lineTo(width * 0.85f, height * 0.90f)
            lineTo(width * 0.75f, height * 0.65f)
            close()
        }
        val finalPath = Path().apply { op(circlePath, tailPath, PathOperation.Union) }
        drawPath(path = finalPath, color = Color.White)
        drawPath(path = finalPath, color = Color.Black, style = Stroke(width = strokeWidth))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MeshAppTheme {
        AppController()
    }
}