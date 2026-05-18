package com.example.inventariolt.repository

import com.example.inventariolt.interfaces.RetrofitClient
import com.example.inventariolt.model.inventario.CategoriaRequestDTO
import com.example.inventariolt.model.inventario.CategoriaResponseDTO

class CategoriaRepository {

    suspend fun getAllCategorias(): Result<List<CategoriaResponseDTO>> {
        return try {
            val response = RetrofitClient.categoriaApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createCategoria(nombre: String): Result<CategoriaResponseDTO> {
        return try {
            val request = CategoriaRequestDTO(nombre)
            val response = RetrofitClient.categoriaApi.create(request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateCategoria(id: Long, nombre: String): Result<CategoriaResponseDTO> {
        return try {
            val request = CategoriaRequestDTO(nombre)
            val response = RetrofitClient.categoriaApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteCategoria(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.categoriaApi.delete(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: No se pudo eliminar"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}