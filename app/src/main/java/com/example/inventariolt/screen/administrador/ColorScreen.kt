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
import com.example.inventariolt.model.inventario.ColorResponseDTO
import com.example.inventariolt.ui.theme.*
import com.example.inventariolt.viewModel.ColorViewModel
import com.example.inventariolt.viewModel.ColoresState
import com.example.inventariolt.viewModel.CreateColorState
import com.example.inventariolt.viewModel.DeleteColorState
import com.example.inventariolt.viewModel.UpdateColorState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaColoresScreen(
    navController: NavController,
    adminId: Long,
    viewModel: ColorViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val coloresState by viewModel.coloresState.collectAsState()
    val createState by viewModel.createState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var colorToEdit by remember { mutableStateOf<ColorResponseDTO?>(null) }
    var colorToDelete by remember { mutableStateOf<ColorResponseDTO?>(null) }
    var newColorName by remember { mutableStateOf("") }
    var editColorName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarColores()
    }

    // Manejar creación exitosa
    LaunchedEffect(createState) {
        if (createState is CreateColorState.Success) {
            snackbarHostState.showSnackbar("Color creado exitosamente")
            viewModel.resetStates()
            showCreateDialog = false
            newColorName = ""
        } else if (createState is CreateColorState.Error) {
            snackbarHostState.showSnackbar((createState as CreateColorState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar actualización exitosa
    LaunchedEffect(updateState) {
        if (updateState is UpdateColorState.Success) {
            snackbarHostState.showSnackbar("Color actualizado exitosamente")
            viewModel.resetStates()
            showEditDialog = false
            colorToEdit = null
            editColorName = ""
        } else if (updateState is UpdateColorState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdateColorState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteColorState.Success) {
            snackbarHostState.showSnackbar("Color eliminado exitosamente")
            viewModel.resetStates()
            colorToDelete = null
        } else if (deleteState is DeleteColorState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteColorState.Error).message)
            viewModel.resetStates()
        }
    }

    // Diálogo para crear color
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nuevo Color", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newColorName,
                    onValueChange = { newColorName = it },
                    label = { Text("Nombre del color") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newColorName.isNotBlank()) {
                            viewModel.crearColor(newColorName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = createState !is CreateColorState.Loading
                ) {
                    if (createState is CreateColorState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Crear")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para editar color
    if (showEditDialog && colorToEdit != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                colorToEdit = null
                editColorName = ""
            },
            title = { Text("Editar Color", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editColorName,
                    onValueChange = { editColorName = it },
                    label = { Text("Nombre del color") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editColorName.isNotBlank() && colorToEdit != null) {
                            viewModel.actualizarColor(colorToEdit!!.idColor, editColorName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = updateState !is UpdateColorState.Loading
                ) {
                    if (updateState is UpdateColorState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    colorToEdit = null
                    editColorName = ""
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para eliminar color
    if (colorToDelete != null) {
        AlertDialog(
            onDismissRequest = { colorToDelete = null },
            title = { Text("Eliminar Color", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar el color '${colorToDelete?.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        colorToDelete?.idColor?.let { viewModel.eliminarColor(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { colorToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Colores", color = Color.White, fontWeight = FontWeight.Bold) },
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

            when (coloresState) {
                is ColoresState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AquamarinePrimary)
                    }
                }
                is ColoresState.Error -> {
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
                        Text("Error: ${(coloresState as ColoresState.Error).message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.cargarColores() }) { Text("Reintentar") }
                    }
                }
                is ColoresState.Success -> {
                    val allColores = (coloresState as ColoresState.Success).colores
                    val filteredColores = allColores.filter {
                        it.nombre.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredColores.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron colores", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredColores) { color ->
                                ColorItem(
                                    color = color,
                                    onEdit = {
                                        colorToEdit = color
                                        editColorName = color.nombre
                                        showEditDialog = true
                                    },
                                    onDelete = { colorToDelete = color }
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
fun ColorItem(
    color: ColorResponseDTO,
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
            // Icono de color (paleta)
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = AquamarinePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre del color
            Text(
                text = color.nombre,
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