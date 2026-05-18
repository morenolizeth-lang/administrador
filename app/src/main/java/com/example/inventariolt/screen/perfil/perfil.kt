package com.example.inventariolt.screen.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.inventariolt.viewModel.PerfilState
import com.example.inventariolt.viewModel.UsuarioViewModel
import com.example.inventariolt.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    userId: Long,
    viewModel: UsuarioViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val perfilState by viewModel.perfilState.collectAsState()

    // Estados del menú
    var showExitDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.cargarPerfil(userId)
    }

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

    // Modal Drawer (Menú desplegable)
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
                // Header del menú con datos del usuario
                val usuario = (perfilState as? PerfilState.Success)?.usuario

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AquamarineGradient)
                        .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        // Imagen del usuario
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
                                    modifier = Modifier.fillMaxSize(),
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

                        // Nombre del usuario
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

                        // Etiqueta de Rol
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

                // ✅ ELIMINADA la opción "Variantes de producto"

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
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = false,
                        onClick = {
                            navController.navigate("inventario_home/$userId") {
                                popUpTo("perfil/$userId") { inclusive = true }
                            }
                        }
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
                        selected = true,
                        onClick = { }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                when (perfilState) {
                    is PerfilState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AquamarinePrimary)
                    }
                    is PerfilState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text((perfilState as PerfilState.Error).message, textAlign = TextAlign.Center)
                            Button(onClick = { viewModel.cargarPerfil(userId) }, modifier = Modifier.padding(top = 16.dp)) {
                                Text("Reintentar")
                            }
                        }
                    }
                    is PerfilState.Success -> {
                        val data = perfilState as PerfilState.Success
                        val usuario = data.usuario
                        val tienda = data.tienda

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Header con foto superpuesta y texto superior
                            Box(contentAlignment = Alignment.BottomCenter) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                                        .background(Brush.verticalGradient(listOf(AquamarineDark, AquamarinePrimary)))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp, start = 8.dp, end = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                                Icon(Icons.Default.Menu, "Menú", tint = Color.White)
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Mi Perfil",
                                                color = Color.White,
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "Información de administrador",
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                // Foto de perfil superpuesta
                                Surface(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .offset(y = 55.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 8.dp
                                ) {
                                    if (usuario.fotoPerfil == null) {
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                                        }
                                    } else {
                                        SubcomposeAsyncImage(
                                            model = usuario.fotoPerfil,
                                            contentDescription = "Foto de perfil",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            loading = {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = AquamarinePrimary)
                                                }
                                            },
                                            error = {
                                                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(70.dp))

                            Column(modifier = Modifier.padding(20.dp)) {
                                // Info Usuario
                                Text(
                                    "Información Personal",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AquamarinePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                InfoCard(
                                    icon = Icons.Default.Person,
                                    label = "Nombre Completo",
                                    value = usuario.nombre
                                )
                                InfoCard(
                                    icon = Icons.Default.Email,
                                    label = "Correo Electrónico",
                                    value = usuario.correo
                                )
                                InfoCard(
                                    icon = Icons.Default.Badge,
                                    label = "Rol del usuario",
                                    value = if (usuario.rol == "CONSULTA") "Administrador" else usuario.rol
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Sección de Cargo / Tienda
                                if (usuario.rol == "CONSULTA") {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = AquamarineLight),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(0.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = AquamarinePrimary.copy(alpha = 0.2f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.AdminPanelSettings,
                                                        contentDescription = null,
                                                        tint = AquamarinePrimary,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    "Tipo de Cuenta",
                                                    fontSize = 12.sp,
                                                    color = AquamarineDark,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "Administrador General",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                                Text(
                                                    "Acceso total a todas las tiendas y usuarios",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                } else if (usuario.tiendaId != null && tienda != null) {
                                    Text(
                                        "Lugar de Trabajo",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AquamarinePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    InfoCard(
                                        icon = Icons.Default.Store,
                                        label = "Tienda",
                                        value = tienda.nombre
                                    )
                                    InfoCard(
                                        icon = Icons.Default.LocationOn,
                                        label = "Dirección",
                                        value = tienda.direccion
                                    )
                                    InfoCard(
                                        icon = Icons.Default.Phone,
                                        label = "Teléfono",
                                        value = tienda.telefono
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = AquamarinePrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}