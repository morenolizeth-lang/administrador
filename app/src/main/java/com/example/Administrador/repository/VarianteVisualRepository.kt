package com.example.Administrador.repository

import com.example.Administrador.interfaces.RetrofitClient
import com.example.Administrador.model.inventario.VarianteVisualRequestDTO
import com.example.Administrador.model.inventario.VarianteVisualResponseDTO
import okhttp3.MultipartBody

class VarianteVisualRepository {
    suspend fun getAllVariantes(): Result<List<VarianteVisualResponseDTO>> {
        return try {
            val response = RetrofitClient.varianteVisualApi.getAll()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createVariante(request: VarianteVisualRequestDTO): Result<VarianteVisualResponseDTO> {
        return try {
            val response = RetrofitClient.varianteVisualApi.create(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImagen(varianteId: Long, file: MultipartBody.Part): Result<VarianteVisualResponseDTO> {
        return try {
            val response = RetrofitClient.varianteVisualApi.uploadImagen(varianteId, file)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVariante(id: Long, request: VarianteVisualRequestDTO): Result<VarianteVisualResponseDTO> {
        return try {
            val response = RetrofitClient.varianteVisualApi.update(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVariante(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.varianteVisualApi.delete(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVarianteById(id: Long): Result<VarianteVisualResponseDTO> {
        return try {
            val response = RetrofitClient.varianteVisualApi.getById(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
