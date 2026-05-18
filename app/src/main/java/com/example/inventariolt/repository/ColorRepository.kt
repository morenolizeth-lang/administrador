package com.example.inventariolt.repository

import com.example.inventariolt.interfaces.RetrofitClient
import com.example.inventariolt.model.inventario.ColorRequestDTO
import com.example.inventariolt.model.inventario.ColorResponseDTO

class ColorRepository {

    suspend fun getAllColores(): Result<List<ColorResponseDTO>> {
        return try {
            val response = RetrofitClient.colorApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createColor(nombre: String): Result<ColorResponseDTO> {
        return try {
            val request = ColorRequestDTO(nombre)
            val response = RetrofitClient.colorApi.create(request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateColor(id: Long, nombre: String): Result<ColorResponseDTO> {
        return try {
            val request = ColorRequestDTO(nombre)
            val response = RetrofitClient.colorApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteColor(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.colorApi.delete(id)
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