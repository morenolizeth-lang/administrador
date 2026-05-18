package com.example.inventariolt.repository

import com.example.inventariolt.interfaces.RetrofitClient
import com.example.inventariolt.model.inventario.MarcaRequestDTO
import com.example.inventariolt.model.inventario.MarcaResponseDTO

class MarcaRepository {

    suspend fun getAllMarcas(): Result<List<MarcaResponseDTO>> {
        return try {
            val response = RetrofitClient.marcaApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createMarca(nombre: String): Result<MarcaResponseDTO> {
        return try {
            val request = MarcaRequestDTO(nombre)
            val response = RetrofitClient.marcaApi.create(request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateMarca(id: Long, nombre: String): Result<MarcaResponseDTO> {
        return try {
            val request = MarcaRequestDTO(nombre)
            val response = RetrofitClient.marcaApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteMarca(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.marcaApi.delete(id)
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