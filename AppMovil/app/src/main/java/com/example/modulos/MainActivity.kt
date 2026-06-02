package com.example.modulos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.modulos.ui.screens.InfoScreen
import com.example.modulos.ui.screens.MessagingScreen
import com.example.modulos.ui.screens.RadarScreen
import com.example.modulos.ui.screens.SettingsScreen
import com.example.modulos.ui.theme.ModulosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModulosTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "mensajes") {
        composable("informacion") {
            RadarScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })
        }
        composable("mensajes") {
            MessagingScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })
        }
        composable("ajustes") {
            SettingsScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })
        }
    }
}
