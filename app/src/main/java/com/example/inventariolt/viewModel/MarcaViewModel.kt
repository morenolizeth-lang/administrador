package com.example.inventariolt.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventariolt.model.inventario.MarcaResponseDTO
import com.example.inventariolt.repository.MarcaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarcaViewModel : ViewModel() {

    private val repository = MarcaRepository()

    private val _marcasState = MutableStateFlow<MarcasState>(MarcasState.Idle)
    val marcasState: StateFlow<MarcasState> = _marcasState

    private val _createState = MutableStateFlow<CreateMarcaState>(CreateMarcaState.Idle)
    val createState: StateFlow<CreateMarcaState> = _createState

    private val _updateState = MutableStateFlow<UpdateMarcaState>(UpdateMarcaState.Idle)
    val updateState: StateFlow<UpdateMarcaState> = _updateState

    private val _deleteState = MutableStateFlow<DeleteMarcaState>(DeleteMarcaState.Idle)
    val deleteState: StateFlow<DeleteMarcaState> = _deleteState

    fun cargarMarcas() {
        viewModelScope.launch {
            _marcasState.value = MarcasState.Loading
            val result = repository.getAllMarcas()
            result.onSuccess { marcas ->
                _marcasState.value = MarcasState.Success(marcas)
            }.onFailure { error ->
                _marcasState.value = MarcasState.Error(error.message ?: "Error al cargar marcas")
            }
        }
    }

    fun crearMarca(nombre: String) {
        viewModelScope.launch {
            _createState.value = CreateMarcaState.Loading
            val result = repository.createMarca(nombre)
            result.onSuccess { marca ->
                _createState.value = CreateMarcaState.Success(marca)
                cargarMarcas()
            }.onFailure { error ->
                _createState.value = CreateMarcaState.Error(error.message ?: "Error al crear marca")
            }
        }
    }

    fun actualizarMarca(id: Long, nombre: String) {
        viewModelScope.launch {
            _updateState.value = UpdateMarcaState.Loading
            val result = repository.updateMarca(id, nombre)
            result.onSuccess { marca ->
                _updateState.value = UpdateMarcaState.Success(marca)
                cargarMarcas()
            }.onFailure { error ->
                _updateState.value = UpdateMarcaState.Error(error.message ?: "Error al actualizar marca")
            }
        }
    }

    fun eliminarMarca(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteMarcaState.Loading
            val result = repository.deleteMarca(id)
            result.onSuccess {
                _deleteState.value = DeleteMarcaState.Success
                cargarMarcas()
            }.onFailure { error ->
                _deleteState.value = DeleteMarcaState.Error(error.message ?: "Error al eliminar marca")
            }
        }
    }

    fun resetStates() {
        _createState.value = CreateMarcaState.Idle
        _updateState.value = UpdateMarcaState.Idle
        _deleteState.value = DeleteMarcaState.Idle
    }
}

sealed class MarcasState {
    object Idle : MarcasState()
    object Loading : MarcasState()
    data class Success(val marcas: List<MarcaResponseDTO>) : MarcasState()
    data class Error(val message: String) : MarcasState()
}

sealed class CreateMarcaState {
    object Idle : CreateMarcaState()
    object Loading : CreateMarcaState()
    data class Success(val marca: MarcaResponseDTO) : CreateMarcaState()
    data class Error(val message: String) : CreateMarcaState()
}

sealed class UpdateMarcaState {
    object Idle : UpdateMarcaState()
    object Loading : UpdateMarcaState()
    data class Success(val marca: MarcaResponseDTO) : UpdateMarcaState()
    data class Error(val message: String) : UpdateMarcaState()
}

sealed class DeleteMarcaState {
    object Idle : DeleteMarcaState()
    object Loading : DeleteMarcaState()
    object Success : DeleteMarcaState()
    data class Error(val message: String) : DeleteMarcaState()
}