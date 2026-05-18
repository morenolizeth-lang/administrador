package com.example.Administrador.repository

import com.example.Administrador.interfaces.RetrofitClient
import com.example.Administrador.model.login.UsuarioResponseDTO
import com.example.Administrador.model.login.UsuarioUpdateDTO
import okhttp3.MultipartBody

class UsuarioRepository {

    suspend fun getUsuarioById(id: Long): Result<UsuarioResponseDTO> {
        return try {
            val response = RetrofitClient.usuarioApi.getById(id)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // En UsuarioRepository.kt
    suspend fun updateUsuario(id: Long, request: UsuarioUpdateDTO): Result<UsuarioResponseDTO> {
        return try {
            println("📤 Enviando JSON: ${request}")
            val response = RetrofitClient.usuarioApi.update(id, request)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("❌ Error ${e.code()}: $errorBody")
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFotoPerfil(id: Long, file: MultipartBody.Part): Result<UsuarioResponseDTO> {
        return try {
            val response = RetrofitClient.usuarioApi.uploadFotoPerfil(id, file)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getAllUsuarios(): Result<List<UsuarioResponseDTO>> {
        return try {
            val response = RetrofitClient.usuarioApi.getAll()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun deleteUsuario(id: Long): Result<Unit> {
        return try {
            RetrofitClient.usuarioApi.delete(id)
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Error ${e.code()}: ${errorBody ?: e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}