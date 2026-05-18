package com.example.inventariolt.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventariolt.model.inventario.GeneroResponseDTO
import com.example.inventariolt.repository.GeneroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeneroViewModel : ViewModel() {

    private val repository = GeneroRepository()

    private val _generosState = MutableStateFlow<GenerosState>(GenerosState.Idle)
    val generosState: StateFlow<GenerosState> = _generosState

    private val _createState = MutableStateFlow<CreateGeneroState>(CreateGeneroState.Idle)
    val createState: StateFlow<CreateGeneroState> = _createState

    private val _updateState = MutableStateFlow<UpdateGeneroState>(UpdateGeneroState.Idle)
    val updateState: StateFlow<UpdateGeneroState> = _updateState

    private val _deleteState = MutableStateFlow<DeleteGeneroState>(DeleteGeneroState.Idle)
    val deleteState: StateFlow<DeleteGeneroState> = _deleteState

    fun cargarGeneros() {
        viewModelScope.launch {
            _generosState.value = GenerosState.Loading
            val result = repository.getAllGeneros()
            result.onSuccess { generos ->
                _generosState.value = GenerosState.Success(generos)
            }.onFailure { error ->
                _generosState.value = GenerosState.Error(error.message ?: "Error al cargar géneros")
            }
        }
    }

    fun crearGenero(nombre: String) {
        viewModelScope.launch {
            _createState.value = CreateGeneroState.Loading
            val result = repository.createGenero(nombre)
            result.onSuccess { genero ->
                _createState.value = CreateGeneroState.Success(genero)
                cargarGeneros()
            }.onFailure { error ->
                _createState.value = CreateGeneroState.Error(error.message ?: "Error al crear género")
            }
        }
    }

    fun actualizarGenero(id: Long, nombre: String) {
        viewModelScope.launch {
            _updateState.value = UpdateGeneroState.Loading
            val result = repository.updateGenero(id, nombre)
            result.onSuccess { genero ->
                _updateState.value = UpdateGeneroState.Success(genero)
                cargarGeneros()
            }.onFailure { error ->
                _updateState.value = UpdateGeneroState.Error(error.message ?: "Error al actualizar género")
            }
        }
    }

    fun eliminarGenero(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteGeneroState.Loading
            val result = repository.deleteGenero(id)
            result.onSuccess {
                _deleteState.value = DeleteGeneroState.Success
                cargarGeneros()
            }.onFailure { error ->
                _deleteState.value = DeleteGeneroState.Error(error.message ?: "Error al eliminar género")
            }
        }
    }

    fun resetStates() {
        _createState.value = CreateGeneroState.Idle
        _updateState.value = UpdateGeneroState.Idle
        _deleteState.value = DeleteGeneroState.Idle
    }
}

sealed class GenerosState {
    object Idle : GenerosState()
    object Loading : GenerosState()
    data class Success(val generos: List<GeneroResponseDTO>) : GenerosState()
    data class Error(val message: String) : GenerosState()
}

sealed class CreateGeneroState {
    object Idle : CreateGeneroState()
    object Loading : CreateGeneroState()
    data class Success(val genero: GeneroResponseDTO) : CreateGeneroState()
    data class Error(val message: String) : CreateGeneroState()
}

sealed class UpdateGeneroState {
    object Idle : UpdateGeneroState()
    object Loading : UpdateGeneroState()
    data class Success(val genero: GeneroResponseDTO) : UpdateGeneroState()
    data class Error(val message: String) : UpdateGeneroState()
}

sealed class DeleteGeneroState {
    object Idle : DeleteGeneroState()
    object Loading : DeleteGeneroState()
    object Success : DeleteGeneroState()
    data class Error(val message: String) : DeleteGeneroState()
}