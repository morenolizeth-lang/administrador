package com.example.Administrador.screen.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.Administrador.viewModel.PerfilState
import com.example.Administrador.viewModel.UsuarioViewModel
import com.example.Administrador.ui.theme.*
import kotlinx.coroutines.launch

data class HomeMenuOption(val title: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioHomeScreen(
    navController: NavController,
    userId: Long,
    viewModel: UsuarioViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val perfilState by viewModel.perfilState.collectAsState()

    // Cargar perfil para el Drawer
    LaunchedEffect(userId) {
        viewModel.cargarPerfil(userId)
    }

    var showExitDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Opciones del menú principal (5 opciones)
    val primeraFila = listOf(
        HomeMenuOption("Categoría", Icons.Default.Category),
        HomeMenuOption("Color", Icons.Default.Palette)
    )

    val segundaFila = listOf(
        HomeMenuOption("Género", Icons.Default.Wc),
        HomeMenuOption("Marca", Icons.Default.Sell)
    )

    val terceraFila = listOf(
        HomeMenuOption("Tiendas", Icons.Default.Store)
    )

    // Diálogo de cierre de sesión
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Cerrar Sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sí, cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo de información
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Acerca de", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "Logo",
                        modifier = Modifier.size(64.dp),
                        tint = AquamarineDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sistema de Inventario", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Versión 1.0.0", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Desarrollado por:", fontWeight = FontWeight.Bold)
                    Text("Lizeth M. & Tomas P.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("© 2026 - Todos los derechos reservados", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("Cerrar") }
            }
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = Color.White,
                windowInsets = WindowInsets(0),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                val usuario = (perfilState as? PerfilState.Success)?.usuario

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AquamarineGradient)
                        .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Surface(
                            modifier = Modifier.size(70.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 6.dp
                        ) {
                            if (usuario?.fotoPerfil != null) {
                                SubcomposeAsyncImage(
                                    model = usuario.fotoPerfil,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    loading = { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) },
                                    error = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray) }
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Usuario",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = usuario?.nombre ?: "Cargando...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = usuario?.correo ?: "",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light
                        )

                        if (usuario?.rol != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (usuario.rol == "CONSULTA") "ADMINISTRADOR" else usuario.rol,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración de cuenta", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("actualizar_perfil/$userId")
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Info, contentDescription = "Información") },
                    label = { Text("Información de la app", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showInfoDialog = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión") },
                    label = { Text("Cerrar sesión", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showExitDialog = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Versión 1.0.0",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Box {
                    HeaderConImagen(
                        titulo = "Administrador Global",
                        subtitulo = "Gestión de Catálogos",
                        altura = 160.dp
                    )
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Usuarios") },
                        label = { Text("Usuarios") },
                        selected = false,
                        onClick = {
                            navController.navigate("lista_usuarios/$userId")
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = {
                            navController.navigate("perfil/$userId")
                        }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Panel de Control",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                // Primera fila - 2 elementos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    primeraFila.forEach { option ->
                        HomeMenuCard(
                            option = option,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val route = when (option.title) {
                                    "Categoría" -> "lista_categorias/$userId"
                                    "Color" -> "lista_colores/$userId"
                                    else -> ""
                                }
                                if (route.isNotEmpty()) {
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
                }

                // Segunda fila - 2 elementos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),  // Aumentado el espacio
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    segundaFila.forEach { option ->
                        HomeMenuCard(
                            option = option,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val route = when (option.title) {
                                    "Género" -> "lista_generos/$userId"
                                    "Marca" -> "lista_marcas/$userId"
                                    else -> ""
                                }
                                if (route.isNotEmpty()) {
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
                }

                // Tercera fila - 1 elemento centrado con más espacio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    terceraFila.forEach { option ->
                        HomeMenuCard(
                            option = option,
                            modifier = Modifier.width(160.dp),  // Reducido el ancho
                            onClick = {
                                val route = when (option.title) {
                                    "Tiendas" -> "lista_tiendas/$userId"
                                    else -> ""
                                }
                                if (route.isNotEmpty()) {
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun HomeMenuCard(
    option: HomeMenuOption,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),  // Reducido el padding interno
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(50.dp),  // Reducido el tamaño del icono
                shape = CircleShape,
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.title,
                        modifier = Modifier.size(28.dp),  // Reducido el tamaño del icono
                        tint = AquamarineDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))  // Reducido el espacio
            Text(
                text = option.title,
                fontSize = 14.sp,  // Reducido el tamaño de la fuente
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}