package com.example.inventariolt.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventariolt.model.inventario.ProductoResponseDTO
import com.example.inventariolt.model.login.TiendaResponseDTO
import com.example.inventariolt.model.login.UsuarioRequestDTO
import com.example.inventariolt.model.login.UsuarioResponseDTO
import com.example.inventariolt.model.login.UsuarioUpdateDTO
import com.example.inventariolt.repository.ProductoRepository
import com.example.inventariolt.repository.TiendaRepository
import com.example.inventariolt.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UsuarioViewModel : ViewModel() {
    private val usuarioRepository = UsuarioRepository()
    private val tiendaRepository = TiendaRepository()

    private val _perfilState = MutableStateFlow<PerfilState>(PerfilState.Idle)
    val perfilState: StateFlow<PerfilState> = _perfilState

    private val _updateState = MutableStateFlow<UpdatePerfilState>(UpdatePerfilState.Idle)
    val updateState: StateFlow<UpdatePerfilState> = _updateState

    private val productoRepository = ProductoRepository()

    private val _productosState = MutableStateFlow<ProductosState>(ProductosState.Idle)
    val productosState: StateFlow<ProductosState> = _productosState

    private val _usuariosState = MutableStateFlow<UsuariosState>(UsuariosState.Idle)
    val usuariosState: StateFlow<UsuariosState> = _usuariosState

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState

    fun cargarPerfil(usuarioId: Long) {
        viewModelScope.launch {
            _perfilState.value = PerfilState.Loading
            val usuarioResult = usuarioRepository.getUsuarioById(usuarioId)

            usuarioResult.onSuccess { usuario ->
                if (usuario.tiendaId != null) {
                    val tiendaResult = tiendaRepository.getTiendaById(usuario.tiendaId)
                    tiendaResult.onSuccess { tienda ->
                        _perfilState.value = PerfilState.Success(usuario, tienda)
                    }.onFailure { error ->
                        _perfilState.value = PerfilState.Success(usuario, null)
                    }
                } else {
                    _perfilState.value = PerfilState.Success(usuario, null)
                }
            }.onFailure { error ->
                _perfilState.value = PerfilState.Error(error.message ?: "Error al cargar perfil")
            }
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            _usuariosState.value = UsuariosState.Loading
            val result = usuarioRepository.getAllUsuarios()
            result.onSuccess { usuarios ->
                _usuariosState.value = UsuariosState.Success(usuarios)
            }.onFailure { error ->
                _usuariosState.value = UsuariosState.Error(error.message ?: "Error al cargar usuarios")
            }
        }
    }

    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            val result = usuarioRepository.deleteUsuario(id)
            result.onSuccess {
                _deleteState.value = DeleteState.Success
                cargarUsuarios()
            }.onFailure { error ->
                _deleteState.value = DeleteState.Error(error.message ?: "Error al eliminar usuario")
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }

    // ✅ Método actualizado para usar UsuarioUpdateDTO
    fun actualizarPerfil(
        id: Long,
        nombre: String,
        correo: String,
        rol: String,
        estado: Boolean,
        tiendaId: Long?,
        nuevaPassword: String  // ← Ya no es nullable
    ) {
        viewModelScope.launch {
            _updateState.value = UpdatePerfilState.Loading
            val request = UsuarioUpdateDTO(
                nombre = nombre,
                correo = correo,
                rol = rol,
                estado = estado,
                tiendaId = tiendaId,
                password = nuevaPassword
            )
            val result = usuarioRepository.updateUsuario(id, request)
            result.onSuccess { usuario ->
                _updateState.value = UpdatePerfilState.Success(usuario)
                cargarPerfil(id)
            }.onFailure { error ->
                _updateState.value = UpdatePerfilState.Error(error.message ?: "Error al actualizar perfil")
            }
        }
    }

    fun subirFotoPerfil(id: Long, file: MultipartBody.Part) {
        viewModelScope.launch {
            _updateState.value = UpdatePerfilState.Loading
            val result = usuarioRepository.uploadFotoPerfil(id, file)
            result.onSuccess { usuario ->
                _updateState.value = UpdatePerfilState.Success(usuario)
                cargarPerfil(id)
            }.onFailure { error ->
                _updateState.value = UpdatePerfilState.Error(error.message ?: "Error al subir imagen")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdatePerfilState.Idle
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _productosState.value = ProductosState.Loading
            val result = productoRepository.getAllProductos()
            result.onSuccess { productos ->
                _productosState.value = ProductosState.Success(productos)
            }.onFailure { error ->
                _productosState.value = ProductosState.Error(error.message ?: "Error al cargar productos")
            }
        }
    }
}

sealed class ProductosState {
    object Idle : ProductosState()
    object Loading : ProductosState()
    data class Success(val productos: List<ProductoResponseDTO>) : ProductosState()
    data class Error(val message: String) : ProductosState()
}

sealed class PerfilState {
    object Idle : PerfilState()
    object Loading : PerfilState()
    data class Success(val usuario: UsuarioResponseDTO, val tienda: TiendaResponseDTO?) : PerfilState()
    data class Error(val message: String) : PerfilState()
}

sealed class UpdatePerfilState {
    object Idle : UpdatePerfilState()
    object Loading : UpdatePerfilState()
    data class Success(val usuario: UsuarioResponseDTO) : UpdatePerfilState()
    data class Error(val message: String) : UpdatePerfilState()
}

sealed class UsuariosState {
    object Idle : UsuariosState()
    object Loading : UsuariosState()
    data class Success(val usuarios: List<UsuarioResponseDTO>) : UsuariosState()
    data class Error(val message: String) : UsuariosState()
}

sealed class DeleteState {
    object Idle : DeleteState()
    object Loading : DeleteState()
    object Success : DeleteState()
    data class Error(val message: String) : DeleteState()
}