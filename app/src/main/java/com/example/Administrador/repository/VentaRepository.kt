package com.example.Administrador.repository

import com.example.Administrador.interfaces.RetrofitClient
import com.example.Administrador.model.inventario.VentaRequestDTO
import com.example.Administrador.model.inventario.VentaResponseDTO

class VentaRepository {
    suspend fun createVenta(request: VentaRequestDTO): Result<VentaResponseDTO> {
        return try {
            val response = RetrofitClient.ventaApi.create(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllVentas(): Result<List<VentaResponseDTO>> {
        return try {
            val response = RetrofitClient.ventaApi.getAll()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
