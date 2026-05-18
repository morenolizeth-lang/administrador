package com.example.Administrador.screen.administrador

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
import com.example.Administrador.model.inventario.GeneroResponseDTO
import com.example.Administrador.ui.theme.*
import com.example.Administrador.viewModel.CreateGeneroState
import com.example.Administrador.viewModel.DeleteGeneroState
import com.example.Administrador.viewModel.GenerosState
import com.example.Administrador.viewModel.GeneroViewModel
import com.example.Administrador.viewModel.UpdateGeneroState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGenerosScreen(
    navController: NavController,
    adminId: Long,
    viewModel: GeneroViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val generosState by viewModel.generosState.collectAsState()
    val createState by viewModel.createState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var generoToEdit by remember { mutableStateOf<GeneroResponseDTO?>(null) }
    var generoToDelete by remember { mutableStateOf<GeneroResponseDTO?>(null) }
    var newGeneroName by remember { mutableStateOf("") }
    var editGeneroName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarGeneros()
    }

    // Manejar creación exitosa
    LaunchedEffect(createState) {
        if (createState is CreateGeneroState.Success) {
            snackbarHostState.showSnackbar("Género creado exitosamente")
            viewModel.resetStates()
            showCreateDialog = false
            newGeneroName = ""
        } else if (createState is CreateGeneroState.Error) {
            snackbarHostState.showSnackbar((createState as CreateGeneroState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar actualización exitosa
    LaunchedEffect(updateState) {
        if (updateState is UpdateGeneroState.Success) {
            snackbarHostState.showSnackbar("Género actualizado exitosamente")
            viewModel.resetStates()
            showEditDialog = false
            generoToEdit = null
            editGeneroName = ""
        } else if (updateState is UpdateGeneroState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdateGeneroState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteGeneroState.Success) {
            snackbarHostState.showSnackbar("Género eliminado exitosamente")
            viewModel.resetStates()
            generoToDelete = null
        } else if (deleteState is DeleteGeneroState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteGeneroState.Error).message)
            viewModel.resetStates()
        }
    }

    // Diálogo para crear género
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                // Solo cerrar si se toca fuera o se presiona Cancelar
                if (createState !is CreateGeneroState.Loading) {
                    showCreateDialog = false
                    newGeneroName = ""
                    viewModel.resetStates()
                }
            },
            title = { Text("Nuevo Género", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newGeneroName,
                    onValueChange = { newGeneroName = it },
                    label = { Text("Nombre del género") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = createState !is CreateGeneroState.Loading
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGeneroName.isNotBlank()) {
                            viewModel.crearGenero(newGeneroName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = createState !is CreateGeneroState.Loading
                ) {
                    if (createState is CreateGeneroState.Loading) {
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
                        newGeneroName = ""
                        viewModel.resetStates()
                    },
                    enabled = createState !is CreateGeneroState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

// Manejar creación exitosa - MODIFICADO (NO cerrar el diálogo automáticamente)
    LaunchedEffect(createState) {
        if (createState is CreateGeneroState.Success) {
            snackbarHostState.showSnackbar("Género creado exitosamente")
            viewModel.resetStates()
            // Limpiar el campo pero mantener el diálogo abierto
            newGeneroName = ""
            // NO cerrar el diálogo: showCreateDialog = false
        } else if (createState is CreateGeneroState.Error) {
            snackbarHostState.showSnackbar((createState as CreateGeneroState.Error).message)
            viewModel.resetStates()
        }
    }

    // Diálogo para editar género
    if (showEditDialog && generoToEdit != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                generoToEdit = null
                editGeneroName = ""
            },
            title = { Text("Editar Género", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editGeneroName,
                    onValueChange = { editGeneroName = it },
                    label = { Text("Nombre del género") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editGeneroName.isNotBlank() && generoToEdit != null) {
                            viewModel.actualizarGenero(generoToEdit!!.idGenero, editGeneroName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = updateState !is UpdateGeneroState.Loading
                ) {
                    if (updateState is UpdateGeneroState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    generoToEdit = null
                    editGeneroName = ""
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para eliminar género
    if (generoToDelete != null) {
        AlertDialog(
            onDismissRequest = { generoToDelete = null },
            title = { Text("Eliminar Género", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar el género '${generoToDelete?.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        generoToDelete?.idGenero?.let { viewModel.eliminarGenero(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { generoToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Géneros", color = Color.White, fontWeight = FontWeight.Bold) },
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

            when (generosState) {
                is GenerosState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AquamarinePrimary)
                    }
                }
                is GenerosState.Error -> {
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
                        Text("Error: ${(generosState as GenerosState.Error).message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.cargarGeneros() }) { Text("Reintentar") }
                    }
                }
                is GenerosState.Success -> {
                    val allGeneros = (generosState as GenerosState.Success).generos
                    val filteredGeneros = allGeneros.filter {
                        it.nombre.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredGeneros.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron géneros", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredGeneros) { genero ->
                                GeneroItem(
                                    genero = genero,
                                    onEdit = {
                                        generoToEdit = genero
                                        editGeneroName = genero.nombre
                                        showEditDialog = true
                                    },
                                    onDelete = { generoToDelete = genero }
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
fun GeneroItem(
    genero: GeneroResponseDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de género
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Wc,
                        contentDescription = null,
                        tint = AquamarinePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre del género
            Text(
                text = genero.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            // Botones de acción
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF4CAF50))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF5350))
            }
        }
    }
}