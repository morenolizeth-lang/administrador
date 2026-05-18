package com.example.inventariolt.screen.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventariolt.model.login.TiendaResponseDTO
import com.example.inventariolt.ui.theme.*
import com.example.inventariolt.viewModel.CreateTiendaState
import com.example.inventariolt.viewModel.DeleteTiendaState
import com.example.inventariolt.viewModel.TiendasState
import com.example.inventariolt.viewModel.TiendaViewModel
import com.example.inventariolt.viewModel.UpdateTiendaState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTiendasScreen(
    navController: NavController,
    adminId: Long,
    viewModel: TiendaViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val tiendasState by viewModel.tiendasState.collectAsState()
    val createState by viewModel.createState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tiendaToEdit by remember { mutableStateOf<TiendaResponseDTO?>(null) }
    var tiendaToDelete by remember { mutableStateOf<TiendaResponseDTO?>(null) }

    var newTiendaNombre by remember { mutableStateOf("") }
    var newTiendaDireccion by remember { mutableStateOf("") }
    var newTiendaTelefono by remember { mutableStateOf("") }

    var editTiendaNombre by remember { mutableStateOf("") }
    var editTiendaDireccion by remember { mutableStateOf("") }
    var editTiendaTelefono by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarTiendas()
    }

    // Manejar creación exitosa
    LaunchedEffect(createState) {
        if (createState is CreateTiendaState.Success) {
            snackbarHostState.showSnackbar("Tienda creada exitosamente")
            viewModel.resetStates()
            showCreateDialog = false
            newTiendaNombre = ""
            newTiendaDireccion = ""
            newTiendaTelefono = ""
        } else if (createState is CreateTiendaState.Error) {
            snackbarHostState.showSnackbar((createState as CreateTiendaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar actualización exitosa
    LaunchedEffect(updateState) {
        if (updateState is UpdateTiendaState.Success) {
            snackbarHostState.showSnackbar("Tienda actualizada exitosamente")
            viewModel.resetStates()
            showEditDialog = false
            tiendaToEdit = null
        } else if (updateState is UpdateTiendaState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdateTiendaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteTiendaState.Success) {
            snackbarHostState.showSnackbar("Tienda eliminada exitosamente")
            viewModel.resetStates()
            tiendaToDelete = null
        } else if (deleteState is DeleteTiendaState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteTiendaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Inicializar valores de edición
    LaunchedEffect(tiendaToEdit) {
        if (tiendaToEdit != null) {
            editTiendaNombre = tiendaToEdit!!.nombre
            editTiendaDireccion = tiendaToEdit!!.direccion
            editTiendaTelefono = tiendaToEdit!!.telefono
        }
    }

    // Diálogo para crear tienda
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                if (createState !is CreateTiendaState.Loading) {
                    showCreateDialog = false
                    newTiendaNombre = ""
                    newTiendaDireccion = ""
                    newTiendaTelefono = ""
                    viewModel.resetStates()
                }
            },
            title = { Text("Nueva Tienda", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTiendaNombre,
                        onValueChange = { newTiendaNombre = it },
                        label = { Text("Nombre de la tienda") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = createState !is CreateTiendaState.Loading
                    )
                    OutlinedTextField(
                        value = newTiendaDireccion,
                        onValueChange = { newTiendaDireccion = it },
                        label = { Text("Dirección") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = createState !is CreateTiendaState.Loading
                    )
                    OutlinedTextField(
                        value = newTiendaTelefono,
                        onValueChange = { newTiendaTelefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = createState !is CreateTiendaState.Loading
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTiendaNombre.isNotBlank() &&
                            newTiendaDireccion.isNotBlank() &&
                            newTiendaTelefono.isNotBlank()) {
                            viewModel.crearTienda(
                                newTiendaNombre.trim(),
                                newTiendaDireccion.trim(),
                                newTiendaTelefono.trim()
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = createState !is CreateTiendaState.Loading &&
                            newTiendaNombre.isNotBlank() &&
                            newTiendaDireccion.isNotBlank() &&
                            newTiendaTelefono.isNotBlank()
                ) {
                    if (createState is CreateTiendaState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Crear")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        newTiendaNombre = ""
                        newTiendaDireccion = ""
                        newTiendaTelefono = ""
                        viewModel.resetStates()
                    },
                    enabled = createState !is CreateTiendaState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para editar tienda
    if (showEditDialog && tiendaToEdit != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                tiendaToEdit = null
            },
            title = { Text("Editar Tienda", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editTiendaNombre,
                        onValueChange = { editTiendaNombre = it },
                        label = { Text("Nombre de la tienda") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = updateState !is UpdateTiendaState.Loading
                    )
                    OutlinedTextField(
                        value = editTiendaDireccion,
                        onValueChange = { editTiendaDireccion = it },
                        label = { Text("Dirección") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = updateState !is UpdateTiendaState.Loading
                    )
                    OutlinedTextField(
                        value = editTiendaTelefono,
                        onValueChange = { editTiendaTelefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = updateState !is UpdateTiendaState.Loading
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTiendaNombre.isNotBlank() &&
                            editTiendaDireccion.isNotBlank() &&
                            editTiendaTelefono.isNotBlank()) {
                            viewModel.actualizarTienda(
                                tiendaToEdit!!.idTienda,
                                editTiendaNombre.trim(),
                                editTiendaDireccion.trim(),
                                editTiendaTelefono.trim()
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = updateState !is UpdateTiendaState.Loading &&
                            editTiendaNombre.isNotBlank() &&
                            editTiendaDireccion.isNotBlank() &&
                            editTiendaTelefono.isNotBlank()
                ) {
                    if (updateState is UpdateTiendaState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        tiendaToEdit = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para eliminar tienda
    if (tiendaToDelete != null) {
        AlertDialog(
            onDismissRequest = { tiendaToDelete = null },
            title = { Text("Eliminar Tienda", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar la tienda '${tiendaToDelete?.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        tiendaToDelete?.idTienda?.let { viewModel.eliminarTienda(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { tiendaToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Tiendas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AquamarinePrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Barra de búsqueda con botón de agregar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar por nombre...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AquamarinePrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AquamarinePrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AquamarinePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            when (tiendasState) {
                is TiendasState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AquamarinePrimary)
                    }
                }
                is TiendasState.Error -> {
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
                        Text("Error: ${(tiendasState as TiendasState.Error).message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.cargarTiendas() }) { Text("Reintentar") }
                    }
                }
                is TiendasState.Success -> {
                    val allTiendas = (tiendasState as TiendasState.Success).tiendas
                    val filteredTiendas = allTiendas.filter {
                        it.nombre.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredTiendas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron tiendas", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredTiendas) { tienda ->
                                TiendaItem(
                                    tienda = tienda,
                                    onEdit = {
                                        tiendaToEdit = tienda
                                        showEditDialog = true
                                    },
                                    onDelete = { tiendaToDelete = tienda }
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

@Composable
fun TiendaItem(
    tienda: TiendaResponseDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono de tienda
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AquamarinePrimary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = AquamarinePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tienda.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = tienda.direccion,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = tienda.telefono,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF4CAF50))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF5350))
                    }
                }
            }
        }
    }
}