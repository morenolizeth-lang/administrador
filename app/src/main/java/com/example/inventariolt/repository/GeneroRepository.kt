package com.example.inventariolt.repository

import com.example.inventariolt.interfaces.RetrofitClient
import com.example.inventariolt.model.inventario.GeneroRequestDTO
import com.example.inventariolt.model.inventario.GeneroResponseDTO

class GeneroRepository {

    suspend fun getAllGeneros(): Result<List<GeneroResponseDTO>> {
        return try {
            val response = RetrofitClient.generoApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createGenero(nombre: String): Result<GeneroResponseDTO> {
        return try {
            val request = GeneroRequestDTO(nombre)
            val response = RetrofitClient.generoApi.create(request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateGenero(id: Long, nombre: String): Result<GeneroResponseDTO> {
        return try {
            val request = GeneroRequestDTO(nombre)
            val response = RetrofitClient.generoApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteGenero(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.generoApi.delete(id)
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