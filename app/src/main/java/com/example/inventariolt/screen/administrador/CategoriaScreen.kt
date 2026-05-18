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
import com.example.inventariolt.model.inventario.CategoriaResponseDTO
import com.example.inventariolt.ui.theme.*
import com.example.inventariolt.viewModel.CategoriaViewModel
import com.example.inventariolt.viewModel.CategoriasState
import com.example.inventariolt.viewModel.CreateCategoriaState
import com.example.inventariolt.viewModel.DeleteCategoriaState
import com.example.inventariolt.viewModel.UpdateCategoriaState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCategoriasScreen(
    navController: NavController,
    adminId: Long,
    viewModel: CategoriaViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val categoriasState by viewModel.categoriasState.collectAsState()
    val createState by viewModel.createState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var categoriaToEdit by remember { mutableStateOf<CategoriaResponseDTO?>(null) }
    var categoriaToDelete by remember { mutableStateOf<CategoriaResponseDTO?>(null) }
    var newCategoriaName by remember { mutableStateOf("") }
    var editCategoriaName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarCategorias()
    }

    // Manejar creación exitosa
    LaunchedEffect(createState) {
        if (createState is CreateCategoriaState.Success) {
            snackbarHostState.showSnackbar("Categoría creada exitosamente")
            viewModel.resetStates()
            showCreateDialog = false
            newCategoriaName = ""
        } else if (createState is CreateCategoriaState.Error) {
            snackbarHostState.showSnackbar((createState as CreateCategoriaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar actualización exitosa
    LaunchedEffect(updateState) {
        if (updateState is UpdateCategoriaState.Success) {
            snackbarHostState.showSnackbar("Categoría actualizada exitosamente")
            viewModel.resetStates()
            showEditDialog = false
            categoriaToEdit = null
            editCategoriaName = ""
        } else if (updateState is UpdateCategoriaState.Error) {
            snackbarHostState.showSnackbar((updateState as UpdateCategoriaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(deleteState) {
        if (deleteState is DeleteCategoriaState.Success) {
            snackbarHostState.showSnackbar("Categoría eliminada exitosamente")
            viewModel.resetStates()
            categoriaToDelete = null
        } else if (deleteState is DeleteCategoriaState.Error) {
            snackbarHostState.showSnackbar((deleteState as DeleteCategoriaState.Error).message)
            viewModel.resetStates()
        }
    }

    // Diálogo para crear categoría
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nueva Categoría", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCategoriaName,
                    onValueChange = { newCategoriaName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoriaName.isNotBlank()) {
                            viewModel.crearCategoria(newCategoriaName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = createState !is CreateCategoriaState.Loading
                ) {
                    if (createState is CreateCategoriaState.Loading) {
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

    // Diálogo para editar categoría
    if (showEditDialog && categoriaToEdit != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                categoriaToEdit = null
                editCategoriaName = ""
            },
            title = { Text("Editar Categoría", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editCategoriaName,
                    onValueChange = { editCategoriaName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editCategoriaName.isNotBlank() && categoriaToEdit != null) {
                            viewModel.actualizarCategoria(categoriaToEdit!!.idCategoria, editCategoriaName.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                    enabled = updateState !is UpdateCategoriaState.Loading
                ) {
                    if (updateState is UpdateCategoriaState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    categoriaToEdit = null
                    editCategoriaName = ""
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para eliminar categoría
    if (categoriaToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoriaToDelete = null },
            title = { Text("Eliminar Categoría", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar la categoría '${categoriaToDelete?.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        categoriaToDelete?.idCategoria?.let { viewModel.eliminarCategoria(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { categoriaToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Categorías", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // Barra de búsqueda con botón de agregar cuadrado al lado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar por nombre...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = AquamarinePrimary
                        )
                    },
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
                    modifier = Modifier
                        .size(70.dp),
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

            when (categoriasState) {
                is CategoriasState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AquamarinePrimary)
                    }
                }
                is CategoriasState.Error -> {
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
                        Text("Error: ${(categoriasState as CategoriasState.Error).message}", modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.cargarCategorias() }) { Text("Reintentar") }
                    }
                }
                is CategoriasState.Success -> {
                    val allCategorias = (categoriasState as CategoriasState.Success).categorias
                    val filteredCategorias = allCategorias.filter {
                        it.nombre.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredCategorias.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron categorías", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCategorias) { categoria ->
                                CategoriaItem(
                                    categoria = categoria,
                                    onEdit = {
                                        categoriaToEdit = categoria
                                        editCategoriaName = categoria.nombre
                                        showEditDialog = true
                                    },
                                    onDelete = { categoriaToDelete = categoria }
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
fun CategoriaItem(
    categoria: CategoriaResponseDTO,
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
            // Icono de categoría
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = AquamarinePrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = AquamarinePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre de la categoría
            Text(
                text = categoria.nombre,
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