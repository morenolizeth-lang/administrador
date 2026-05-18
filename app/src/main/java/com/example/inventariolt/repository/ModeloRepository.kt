package com.example.inventariolt.repository

import com.example.inventariolt.interfaces.RetrofitClient
import com.example.inventariolt.model.inventario.ModeloRequestDTO
import com.example.inventariolt.model.inventario.ModeloResponseDTO

class ModeloRepository {

    suspend fun getAllModelos(): Result<List<ModeloResponseDTO>> {
        return try {
            val response = RetrofitClient.modeloApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createModelo(
        nombre: String,
        marcaId: Long,
        categoriaId: Long,
        generoId: Long
    ): Result<ModeloResponseDTO> {
        return try {
            val request = ModeloRequestDTO(nombre, marcaId, categoriaId, generoId)
            val response = RetrofitClient.modeloApi.create(request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateModelo(
        id: Long,
        nombre: String,
        marcaId: Long,
        categoriaId: Long,
        generoId: Long
    ): Result<ModeloResponseDTO> {
        return try {
            val request = ModeloRequestDTO(nombre, marcaId, categoriaId, generoId)
            val response = RetrofitClient.modeloApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteModelo(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.modeloApi.delete(id)
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