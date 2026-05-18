package com.example.Administrador.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Administrador.model.login.TiendaResponseDTO
import com.example.Administrador.repository.TiendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TiendaViewModel : ViewModel() {

    private val repository = TiendaRepository()

    private val _tiendasState = MutableStateFlow<TiendasState>(TiendasState.Idle)
    val tiendasState: StateFlow<TiendasState> = _tiendasState

    private val _createState = MutableStateFlow<CreateTiendaState>(CreateTiendaState.Idle)
    val createState: StateFlow<CreateTiendaState> = _createState

    private val _updateState = MutableStateFlow<UpdateTiendaState>(UpdateTiendaState.Idle)
    val updateState: StateFlow<UpdateTiendaState> = _updateState

    private val _deleteState = MutableStateFlow<DeleteTiendaState>(DeleteTiendaState.Idle)
    val deleteState: StateFlow<DeleteTiendaState> = _deleteState

    fun cargarTiendas() {
        viewModelScope.launch {
            _tiendasState.value = TiendasState.Loading
            val result = repository.getAllTiendas()
            result.onSuccess { tiendas ->
                _tiendasState.value = TiendasState.Success(tiendas)
            }.onFailure { error ->
                _tiendasState.value = TiendasState.Error(error.message ?: "Error al cargar tiendas")
            }
        }
    }

    fun crearTienda(nombre: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            _createState.value = CreateTiendaState.Loading
            val result = repository.createTienda(nombre, direccion, telefono)
            result.onSuccess { tienda ->
                _createState.value = CreateTiendaState.Success(tienda)
                cargarTiendas()
            }.onFailure { error ->
                _createState.value = CreateTiendaState.Error(error.message ?: "Error al crear tienda")
            }
        }
    }

    fun actualizarTienda(id: Long, nombre: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            _updateState.value = UpdateTiendaState.Loading
            val result = repository.updateTienda(id, nombre, direccion, telefono)
            result.onSuccess { tienda ->
                _updateState.value = UpdateTiendaState.Success(tienda)
                cargarTiendas()
            }.onFailure { error ->
                _updateState.value = UpdateTiendaState.Error(error.message ?: "Error al actualizar tienda")
            }
        }
    }

    fun eliminarTienda(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteTiendaState.Loading
            val result = repository.deleteTienda(id)
            result.onSuccess {
                _deleteState.value = DeleteTiendaState.Success
                cargarTiendas()
            }.onFailure { error ->
                _deleteState.value = DeleteTiendaState.Error(error.message ?: "Error al eliminar tienda")
            }
        }
    }

    fun resetStates() {
        _createState.value = CreateTiendaState.Idle
        _updateState.value = UpdateTiendaState.Idle
        _deleteState.value = DeleteTiendaState.Idle
    }
}

sealed class TiendasState {
    object Idle : TiendasState()
    object Loading : TiendasState()
    data class Success(val tiendas: List<TiendaResponseDTO>) : TiendasState()
    data class Error(val message: String) : TiendasState()
}

sealed class CreateTiendaState {
    object Idle : CreateTiendaState()
    object Loading : CreateTiendaState()
    data class Success(val tienda: TiendaResponseDTO) : CreateTiendaState()
    data class Error(val message: String) : CreateTiendaState()
}

sealed class UpdateTiendaState {
    object Idle : UpdateTiendaState()
    object Loading : UpdateTiendaState()
    data class Success(val tienda: TiendaResponseDTO) : UpdateTiendaState()
    data class Error(val message: String) : UpdateTiendaState()
}

sealed class DeleteTiendaState {
    object Idle : DeleteTiendaState()
    object Loading : DeleteTiendaState()
    object Success : DeleteTiendaState()
    data class Error(val message: String) : DeleteTiendaState()
}