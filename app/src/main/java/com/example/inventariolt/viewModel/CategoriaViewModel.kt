package com.example.inventariolt.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventariolt.model.inventario.CategoriaResponseDTO
import com.example.inventariolt.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaViewModel : ViewModel() {

    private val repository = CategoriaRepository()

    private val _categoriasState = MutableStateFlow<CategoriasState>(CategoriasState.Idle)
    val categoriasState: StateFlow<CategoriasState> = _categoriasState

    private val _createState = MutableStateFlow<CreateCategoriaState>(CreateCategoriaState.Idle)
    val createState: StateFlow<CreateCategoriaState> = _createState

    private val _updateState = MutableStateFlow<UpdateCategoriaState>(UpdateCategoriaState.Idle)
    val updateState: StateFlow<UpdateCategoriaState> = _updateState

    private val _deleteState = MutableStateFlow<DeleteCategoriaState>(DeleteCategoriaState.Idle)
    val deleteState: StateFlow<DeleteCategoriaState> = _deleteState

    fun cargarCategorias() {
        viewModelScope.launch {
            _categoriasState.value = CategoriasState.Loading
            val result = repository.getAllCategorias()
            result.onSuccess { categorias ->
                _categoriasState.value = CategoriasState.Success(categorias)
            }.onFailure { error ->
                _categoriasState.value = CategoriasState.Error(error.message ?: "Error al cargar categorías")
            }
        }
    }

    fun crearCategoria(nombre: String) {
        viewModelScope.launch {
            _createState.value = CreateCategoriaState.Loading
            val result = repository.createCategoria(nombre)
            result.onSuccess { categoria ->
                _createState.value = CreateCategoriaState.Success(categoria)
                cargarCategorias() // Recargar lista
            }.onFailure { error ->
                _createState.value = CreateCategoriaState.Error(error.message ?: "Error al crear categoría")
            }
        }
    }

    fun actualizarCategoria(id: Long, nombre: String) {
        viewModelScope.launch {
            _updateState.value = UpdateCategoriaState.Loading
            val result = repository.updateCategoria(id, nombre)
            result.onSuccess { categoria ->
                _updateState.value = UpdateCategoriaState.Success(categoria)
                cargarCategorias()
            }.onFailure { error ->
                _updateState.value = UpdateCategoriaState.Error(error.message ?: "Error al actualizar categoría")
            }
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteCategoriaState.Loading
            val result = repository.deleteCategoria(id)
            result.onSuccess {
                _deleteState.value = DeleteCategoriaState.Success
                cargarCategorias()
            }.onFailure { error ->
                _deleteState.value = DeleteCategoriaState.Error(error.message ?: "Error al eliminar categoría")
            }
        }
    }

    fun resetStates() {
        _createState.value = CreateCategoriaState.Idle
        _updateState.value = UpdateCategoriaState.Idle
        _deleteState.value = DeleteCategoriaState.Idle
    }
}

sealed class CategoriasState {
    object Idle : CategoriasState()
    object Loading : CategoriasState()
    data class Success(val categorias: List<CategoriaResponseDTO>) : CategoriasState()
    data class Error(val message: String) : CategoriasState()
}

sealed class CreateCategoriaState {
    object Idle : CreateCategoriaState()
    object Loading : CreateCategoriaState()
    data class Success(val categoria: CategoriaResponseDTO) : CreateCategoriaState()
    data class Error(val message: String) : CreateCategoriaState()
}

sealed class UpdateCategoriaState {
    object Idle : UpdateCategoriaState()
    object Loading : UpdateCategoriaState()
    data class Success(val categoria: CategoriaResponseDTO) : UpdateCategoriaState()
    data class Error(val message: String) : UpdateCategoriaState()
}

sealed class DeleteCategoriaState {
    object Idle : DeleteCategoriaState()
    object Loading : DeleteCategoriaState()
    object Success : DeleteCategoriaState()
    data class Error(val message: String) : DeleteCategoriaState()
}