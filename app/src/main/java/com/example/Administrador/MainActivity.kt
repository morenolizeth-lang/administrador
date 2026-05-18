package com.example.Administrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.Administrador.screen.administrador.InventarioHomeScreen
import com.example.Administrador.screen.administrador.ListaCategoriasScreen
import com.example.Administrador.screen.login.LoginScreen
import com.example.Administrador.screen.login.RegistroScreen
import com.example.Administrador.screen.perfil.ActualizarPerfilScreen
import com.example.Administrador.screen.perfil.PerfilScreen
import com.example.Administrador.screen.administrador.ListaUsuariosScreen
import com.example.Administrador.ui.theme.InventarioTheme
import com.example.Administrador.screen.administrador.ListaColoresScreen
import com.example.Administrador.screen.administrador.ListaGenerosScreen
import com.example.Administrador.screen.administrador.ListaMarcasScreen
import com.example.Administrador.screen.administrador.ListaTiendasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Pantalla de Login
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Pantalla de Registro
        composable("registro") {
            RegistroScreen(navController = navController)
        }

        // Pantalla de Inventario (Home - Panel de Administrador)
        composable(
            route = "inventario_home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            InventarioHomeScreen(navController = navController, userId = userId)
        }

        // Pantalla de Lista de Usuarios (Gestión de Personal)
        composable(
            route = "lista_usuarios/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            ListaUsuariosScreen(
                navController = navController,
                adminId = userId
            )
        }

        // Pantalla de Perfil
        composable(
            route = "perfil/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            PerfilScreen(navController = navController, userId = userId)
        }

        // Pantalla de Actualizar Perfil
        composable(
            route = "actualizar_perfil/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            ActualizarPerfilScreen(navController = navController, userId = userId)
        }

        // Pantalla de Lista de Categorías
        composable(
            route = "lista_categorias/{adminId}",
            arguments = listOf(navArgument("adminId") { type = NavType.LongType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getLong("adminId") ?: 0L
            ListaCategoriasScreen(navController = navController, adminId = adminId)
        }

// Pantalla de Lista de Colores
        composable(
            route = "lista_colores/{adminId}",
            arguments = listOf(navArgument("adminId") { type = NavType.LongType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getLong("adminId") ?: 0L
            ListaColoresScreen(navController = navController, adminId = adminId)
        }

// Pantalla de Lista de Géneros
        composable(
            route = "lista_generos/{adminId}",
            arguments = listOf(navArgument("adminId") { type = NavType.LongType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getLong("adminId") ?: 0L
            ListaGenerosScreen(navController = navController, adminId = adminId)
        }

// Pantalla de Lista de Marcas
        composable(
            route = "lista_marcas/{adminId}",
            arguments = listOf(navArgument("adminId") { type = NavType.LongType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getLong("adminId") ?: 0L
            ListaMarcasScreen(navController = navController, adminId = adminId)
        }


// Pantalla de Lista de Tiendas
        composable(
            route = "lista_tiendas/{adminId}",
            arguments = listOf(navArgument("adminId") { type = NavType.LongType })
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getLong("adminId") ?: 0L
            ListaTiendasScreen(navController = navController, adminId = adminId)
        }
    }
}
