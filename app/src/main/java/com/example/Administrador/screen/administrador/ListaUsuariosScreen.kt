package com.example.Administrador.screen.administrador

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.Administrador.model.login.UsuarioResponseDTO
import com.example.Administrador.ui.theme.*
import com.example.Administrador.viewModel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuariosScreen(
    navController: NavController,
    adminId: Long,
    viewModel: UsuarioViewModel = viewModel(),
    tiendaViewModel: TiendaViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val usuariosState by viewModel.usuariosState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val perfilState by viewModel.perfilState.collectAsState()
    val tiendasState by tiendaViewModel.tiendasState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var userToDelete by remember { mutableStateOf<UsuarioResponseDTO?>(null) }
    var userToEditStore by remember { mutableStateOf<UsuarioResponseDTO?>(null) }

    // Estado para la tienda seleccionada en el diálogo
    var selectedTiendaId by remember { mutableStateOf<Long?>(null) }

    var showExitDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Inicializar estados cuando se abre el diálogo
    LaunchedEffect(userToEditStore) {
        if (userToEditStore != null) {
            selectedTiendaId = userToEditStore?.tiendaId
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
        viewModel.cargarPerfil(adminId)
        tiendaViewModel.cargarTiendas()
    }

    // Manejo de eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteState.Success) {
            snackbarHostState.showSnackbar("Usuario eliminado correctamente")
            viewModel.resetDeleteState()
            viewModel.cargarUsuarios() // Recargar lista
        } else if (deleteState is DeleteState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteState.Error).message)
            viewModel.resetDeleteState()
        }
    }

    // Manejo de actualización exitosa (para cambio de tienda)
    LaunchedEffect(updateState) {
        if (updateState is UpdatePerfilState.Success) {
            snackbarHostState.showSnackbar("Tienda reasignada correctamente")
            viewModel.resetUpdateState()
            viewModel.cargarUsuarios()
            userToEditStore = null
        } else if (updateState is UpdatePerfilState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdatePerfilState.Error).message)
            viewModel.resetUpdateState()
        }
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

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Eliminar Usuario", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar a ${userToDelete?.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.idUsuario?.let { viewModel.eliminarUsuario(it) }
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para Cambiar Tienda
    if (userToEditStore != null) {
        AlertDialog(
            onDismissRequest = { userToEditStore = null },
            title = { Text("Asignar Tienda", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Selecciona una tienda para ${userToEditStore?.nombre}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    when (tiendasState) {
                        is TiendasState.Loading -> Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is TiendasState.Success -> {
                            val tiendas = (tiendasState as TiendasState.Success).tiendas
                            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTiendaId = null }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedTiendaId == null,
                                            onClick = { selectedTiendaId = null }
                                        )
                                        Text("Sin tienda", modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                                items(tiendas) { tienda ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTiendaId = tienda.idTienda }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedTiendaId == tienda.idTienda,
                                            onClick = { selectedTiendaId = tienda.idTienda }
                                        )
                                        Text(tienda.nombre, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        }
                        is TiendasState.Error -> Text("Error al cargar tiendas", color = Color.Red)
                        else -> {}
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userToEditStore?.let {
                            viewModel.cambiarTiendaUsuario(it, selectedTiendaId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToEditStore = null }) { Text("Cancelar") }
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
                                    text = when (usuario.rol) {
                                        "ADMIN" -> "ADMINISTRADOR"
                                        "EMPLEADO" -> "ADMIN TIENDA"
                                        "CONSULTA" -> "EMPLEADO"
                                        else -> usuario.rol
                                    },
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
                            navController.navigate("actualizar_perfil/$adminId")
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Box {
                    HeaderConImagen(
                        titulo = "Gestión de Usuarios",
                        subtitulo = "Administra las cuentas del sistema",
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
                        selected = false,
                        onClick = {
                            navController.navigate("inventario_home/$adminId") {
                                popUpTo("lista_usuarios/$adminId") { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Usuarios") },
                        label = { Text("Usuarios") },
                        selected = true,  // ✅ Ahora está seleccionada la opción de Usuarios
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = {
                            navController.navigate("perfil/$adminId")
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
            ) {
                // Barra de Búsqueda
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por nombre, rol...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AquamarinePrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = AquamarinePrimary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AquamarinePrimary,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                when (usuariosState) {
                    is UsuariosState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AquamarinePrimary)
                        }
                    }
                    is UsuariosState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Text("Error: ${(usuariosState as UsuariosState.Error).message}", modifier = Modifier.padding(16.dp))
                            Button(
                                onClick = { viewModel.cargarUsuarios() },
                                colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary)
                            ) { Text("Reintentar") }
                        }
                    }
                    is UsuariosState.Success -> {
                        val allUsers = (usuariosState as UsuariosState.Success).usuarios
                        val filteredUsers = allUsers.filter { usuario ->
                            val rolMostrado = when (usuario.rol) {
                                "ADMIN" -> "ADMINISTRADOR"
                                "EMPLEADO" -> "ADMIN TIENDA"
                                "CONSULTA" -> "EMPLEADO"
                                else -> usuario.rol
                            }
                            usuario.nombre.contains(searchQuery, ignoreCase = true) ||
                                    (usuario.tiendaNombre?.contains(searchQuery, ignoreCase = true) ?: false) ||
                                    rolMostrado.contains(searchQuery, ignoreCase = true)
                        }.filter { it.idUsuario != adminId } // No mostrarse a sí mismo

                        if (filteredUsers.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        if (searchQuery.isNotEmpty()) "No se encontraron usuarios para \"$searchQuery\""
                                        else "No hay usuarios registrados",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredUsers) { usuario ->
                                    UsuarioItem(
                                        usuario = usuario,
                                        onDelete = { userToDelete = usuario },
                                        onEditStore = { userToEditStore = usuario },
                                        onToggleStatus = { nuevoEstado ->
                                            viewModel.cambiarEstadoUsuario(usuario, nuevoEstado)
                                        },
                                        onToggleRole = {
                                            val nuevoRol = if (usuario.rol == "EMPLEADO") "CONSULTA" else "EMPLEADO"
                                            viewModel.cambiarRolUsuario(usuario, nuevoRol)
                                        }
                                    )
                                }
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
fun UsuarioItem(
    usuario: UsuarioResponseDTO,
    onDelete: () -> Unit,
    onEditStore: () -> Unit,
    onToggleStatus: (Boolean) -> Unit,
    onToggleRole: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Foto de perfil
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color(0xFFE0E0E0)
                ) {
                    SubcomposeAsyncImage(
                        model = usuario.fotoPerfil,
                        contentDescription = null,
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = { CircularProgressIndicator(modifier = Modifier.padding(10.dp)) },
                        error = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(10.dp), tint = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AquamarineDark)
                    Text(
                        text = when (usuario.rol) {
                            "ADMIN" -> "ADMINISTRADOR"
                            "EMPLEADO" -> "ADMIN TIENDA"
                            "CONSULTA" -> "EMPLEADO"
                            else -> usuario.rol
                        },
                        fontSize = 12.sp,
                        color = AquamarinePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Switch para el estado (Activo/Inactivo)
                Switch(
                    checked = usuario.estado,
                    onCheckedChange = onToggleStatus,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AquamarinePrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.scale(0.8f)
                )

                IconButton(onClick = onToggleRole) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Cambiar Rol", tint = AquamarinePrimary)
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF5350))
                }

                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            // Información extendida
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(icon = Icons.Default.Email, label = "Correo", value = usuario.correo)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailRow(
                            icon = Icons.Default.Store,
                            label = "Tienda",
                            value = usuario.tiendaNombre ?: "Sin tienda asignada"
                        )
                        
                        TextButton(onClick = onEditStore) {
                            Text(if (usuario.tiendaId == null) "Asignar" else "Cambiar", fontSize = 12.sp)
                        }
                    }

                    DetailRow(
                        icon = Icons.Default.Badge,
                        label = "Estado",
                        value = if (usuario.estado) "Activo" else "Inactivo"
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: ", fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}