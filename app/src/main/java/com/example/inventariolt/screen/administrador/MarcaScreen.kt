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
import com.example.inventariolt.model.inventario.MarcaResponseDTO
import com.example.inventariolt.ui.theme.*
import com.example.inventariolt.viewModel.CreateMarcaState
import com.example.inventariolt.viewModel.DeleteMarcaState
import com.example.inventariolt.viewModel.MarcasState
import com.example.inventariolt.viewModel.MarcaViewModel
import com.example.inventariolt.viewModel.UpdateMarcaState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaMarcasScreen(
    navController: NavController,
    adminId: Long,
    viewModel: MarcaViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val marcasState by viewModel.marcasState.collectAsState()
    val createState by viewModel.createState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var marcaToEdit by remember { mutableStateOf<MarcaResponseDTO?>(null) }
    var marcaToDelete by remember { mutableStateOf<MarcaResponseDTO?>(null) }
    var newMarcaName by remember { mutableStateOf("") }
    var editMarcaName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarMarcas()
    }

    // Manejar creación exitosa
    LaunchedEffect(createState) {
        if (createState is CreateMarcaState.Success) {
            snackbarHostState.showSnackbar("Marca creada exitosamente")
            viewModel.resetStates()
            showCreateDialog = false
            newMarcaName = ""
        } else if (createState is CreateMarcaState.Error) {
            snackbarHostState.showSnackbar((createState as CreateMarcaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar actualización exitosa
    LaunchedEffect(updateState) {
        if (updateState is UpdateMarcaState.Success) {
            snackbarHostState.showSnackbar("Marca actualizada exitosamente")
            viewModel.resetStates()
            showEditDialog = false
            marcaToEdit = null
            editMarcaName = ""
        } else if (updateState is UpdateMarcaState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdateMarcaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteMarcaState.Success) {
            snackbarHostState.showSnackbar("Marca eliminada exitosamente")
            viewModel.resetStates()
            marcaToDelete = null
        } else if (deleteState is DeleteMarcaState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteMarcaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Diálogo para crear marca
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                if (createState !is CreateMarcaState.Loading) {
                    showCreateDialog = false
                    newMarcaName = ""
                    viewModel.resetStates()
                }
            },
            title = { Text("Nueva Marca", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newMarcaName,
                    onValueChange = { newMarcaName = it },
                    label = { Text("Nombre de la marca") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = createState !is CreateMarcaState.Loading
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMarcaName.isNotBlank()) {
                            viewModel.crearMarca(newMarcaName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = createState !is CreateMarcaState.Loading
                ) {
                    if (createState is CreateMarcaState.Loading) {
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
                        newMarcaName = ""
                        viewModel.resetStates()
                    },
                    enabled = createState !is CreateMarcaState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para editar marca
    if (showEditDialog && marcaToEdit != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                marcaToEdit = null
                editMarcaName = ""
            },
            title = { Text("Editar Marca", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editMarcaName,
                    onValueChange = { editMarcaName = it },
                    label = { Text("Nombre de la marca") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editMarcaName.isNotBlank() && marcaToEdit != null) {
                            viewModel.actualizarMarca(marcaToEdit!!.idMarca, editMarcaName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = updateState !is UpdateMarcaState.Loading
                ) {
                    if (updateState is UpdateMarcaState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    marcaToEdit = null
                    editMarcaName = ""
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para eliminar marca
    if (marcaToDelete != null) {
        AlertDialog(
            onDismissRequest = { marcaToDelete = null },
            title = { Text("Eliminar Marca", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar la marca '${marcaToDelete?.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        marcaToDelete?.idMarca?.let { viewModel.eliminarMarca(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { marcaToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Marcas", color = Color.White, fontWeight = FontWeight.Bold) },
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

            when (marcasState) {
                is MarcasState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AquamarinePrimary)
                    }
                }
                is MarcasState.Error -> {
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
                        Text("Error: ${(marcasState as MarcasState.Error).message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.cargarMarcas() }) { Text("Reintentar") }
                    }
                }
                is MarcasState.Success -> {
                    val allMarcas = (marcasState as MarcasState.Success).marcas
                    val filteredMarcas = allMarcas.filter {
                        it.nombre.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredMarcas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron marcas", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredMarcas) { marca ->
                                MarcaItem(
                                    marca = marca,
                                    onEdit = {
                                        marcaToEdit = marca
                                        editMarcaName = marca.nombre
                                        showEditDialog = true
                                    },
                                    onDelete = { marcaToDelete = marca }
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
fun MarcaItem(
    marca: MarcaResponseDTO,
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
            // Icono de marca
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Sell,
                        contentDescription = null,
                        tint = AquamarinePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre de la marca
            Text(
                text = marca.nombre,
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