package com.example.Administrador.screen.perfil

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.Administrador.viewModel.PerfilState
import com.example.Administrador.viewModel.UpdatePerfilState
import com.example.Administrador.viewModel.UsuarioViewModel
import com.example.Administrador.ui.theme.*
import com.example.Administrador.utils.FileUtils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarPerfilScreen(
    navController: NavController,
    userId: Long,
    viewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val perfilState by viewModel.perfilState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // Cargar datos iniciales
    LaunchedEffect(userId) {
        viewModel.cargarPerfil(userId)
    }

    // Sincronizar campos cuando el perfil se carga
    LaunchedEffect(perfilState) {
        if (perfilState is PerfilState.Success) {
            val usuario = (perfilState as PerfilState.Success).usuario
            nombre = usuario.nombre
            correo = usuario.correo
        }
    }

    // Manejar estados de actualización
    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdatePerfilState.Success -> {
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateState()
                // Ya no cerramos la pantalla automáticamente para permitir seguir editando
                // o ver el cambio de la foto.
                viewModel.cargarPerfil(userId) 
            }
            is UpdatePerfilState.Error -> {
                Toast.makeText(
                    context,
                    (updateState as UpdatePerfilState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                viewModel.subirFotoPerfil(userId, body)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (perfilState) {
                is PerfilState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AquamarinePrimary
                    )
                }

                is PerfilState.Success -> {
                    val usuario = (perfilState as PerfilState.Success).usuario

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 🔥 HEADER CON FOTO SUPERPUESTA
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(AquamarineDark, AquamarinePrimary)
                                        )
                                    )
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
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Volver",
                                                tint = Color.White
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Editar Perfil",
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Actualiza tu información",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // FOTO SUPERPUESTA CON CLICK PARA EDITAR
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .offset(y = 55.dp)
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 8.dp
                                ) {
                                    if (usuario.fotoPerfil == null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFFE0E0E0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                null,
                                                modifier = Modifier.size(60.dp),
                                                tint = Color.Gray
                                            )
                                        }
                                    } else {
                                        SubcomposeAsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(usuario.fotoPerfil)
                                                .crossfade(true)
                                                .diskCacheKey(System.currentTimeMillis().toString()) // Forzar refresco
                                                .build(),
                                            contentDescription = "Foto de perfil",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                
                                // Icono de edición (Lápiz)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(32.dp)
                                        .background(AquamarinePrimary, CircleShape)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Cambiar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(70.dp))

                        // 🔥 FORMULARIO
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Información del Usuario",
                                style = MaterialTheme.typography.titleMedium,
                                color = AquamarinePrimary,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre Completo") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AquamarinePrimary,
                                    focusedLabelColor = AquamarinePrimary,
                                    focusedLeadingIconColor = AquamarinePrimary
                                )
                            )

                            OutlinedTextField(
                                value = correo,
                                onValueChange = { correo = it },
                                label = { Text("Correo Electrónico") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AquamarinePrimary,
                                    focusedLabelColor = AquamarinePrimary,
                                    focusedLeadingIconColor = AquamarinePrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Validación para guardar
                            val canSave = nombre.isNotEmpty() && correo.isNotEmpty()

                            Button(
                                onClick = {
                                    viewModel.actualizarPerfil(
                                        id = userId,
                                        nombre = nombre,
                                        correo = correo,
                                        rol = usuario.rol,
                                        estado = usuario.estado,
                                        tiendaId = usuario.tiendaId
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AquamarinePrimary),
                                enabled = updateState !is UpdatePerfilState.Loading && canSave
                            ) {
                                if (updateState is UpdatePerfilState.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                is PerfilState.Error -> {
                    Text(
                        "Error: ${(perfilState as PerfilState.Error).message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }
    }
}

// Se eliminaron las funciones locales uriToFile y getFileName para usar las de utils.FileUtils
