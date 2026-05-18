package com.example.Administrador.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Administrador.model.inventario.ColorResponseDTO
import com.example.Administrador.repository.ColorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ColorViewModel : ViewModel() {

    private val repository = ColorRepository()

    private val _coloresState = MutableStateFlow<ColoresState>(ColoresState.Idle)
    val coloresState: StateFlow<ColoresState> = _coloresState

    private val _createState = MutableStateFlow<CreateColorState>(CreateColorState.Idle)
    val createState: StateFlow<CreateColorState> = _createState

    private val _updateState = MutableStateFlow<UpdateColorState>(UpdateColorState.Idle)
    val updateState: StateFlow<UpdateColorState> = _updateState

    private val _deleteState = MutableStateFlow<DeleteColorState>(DeleteColorState.Idle)
    val deleteState: StateFlow<DeleteColorState> = _deleteState

    fun cargarColores() {
        viewModelScope.launch {
            _coloresState.value = ColoresState.Loading
            val result = repository.getAllColores()
            result.onSuccess { colores ->
                _coloresState.value = ColoresState.Success(colores)
            }.onFailure { error ->
                _coloresState.value = ColoresState.Error(error.message ?: "Error al cargar colores")
            }
        }
    }

    fun crearColor(nombre: String) {
        viewModelScope.launch {
            _createState.value = CreateColorState.Loading
            val result = repository.createColor(nombre)
            result.onSuccess { color ->
                _createState.value = CreateColorState.Success(color)
                cargarColores()
            }.onFailure { error ->
                _createState.value = CreateColorState.Error(error.message ?: "Error al crear color")
            }
        }
    }

    fun actualizarColor(id: Long, nombre: String) {
        viewModelScope.launch {
            _updateState.value = UpdateColorState.Loading
            val result = repository.updateColor(id, nombre)
            result.onSuccess { color ->
                _updateState.value = UpdateColorState.Success(color)
                cargarColores()
            }.onFailure { error ->
                _updateState.value = UpdateColorState.Error(error.message ?: "Error al actualizar color")
            }
        }
    }

    fun eliminarColor(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteColorState.Loading
            val result = repository.deleteColor(id)
            result.onSuccess {
                _deleteState.value = DeleteColorState.Success
                cargarColores()
            }.onFailure { error ->
                _deleteState.value = DeleteColorState.Error(error.message ?: "Error al eliminar color")
            }
        }
    }

    fun resetStates() {
        _createState.value = CreateColorState.Idle
        _updateState.value = UpdateColorState.Idle
        _deleteState.value = DeleteColorState.Idle
    }
}

sealed class ColoresState {
    object Idle : ColoresState()
    object Loading : ColoresState()
    data class Success(val colores: List<ColorResponseDTO>) : ColoresState()
    data class Error(val message: String) : ColoresState()
}

sealed class CreateColorState {
    object Idle : CreateColorState()
    object Loading : CreateColorState()
    data class Success(val color: ColorResponseDTO) : CreateColorState()
    data class Error(val message: String) : CreateColorState()
}

sealed class UpdateColorState {
    object Idle : UpdateColorState()
    object Loading : UpdateColorState()
    data class Success(val color: ColorResponseDTO) : UpdateColorState()
    data class Error(val message: String) : UpdateColorState()
}

sealed class DeleteColorState {
    object Idle : DeleteColorState()
    object Loading : DeleteColorState()
    object Success : DeleteColorState()
    data class Error(val message: String) : DeleteColorState()
}