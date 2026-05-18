package com.example.Administrador.interfaces

import com.example.Administrador.model.inventario.MarcaRequestDTO
import com.example.Administrador.model.inventario.MarcaResponseDTO
import retrofit2.Response
import retrofit2.http.*


interface MarcaApi {
    @POST("api/marcas")
    suspend fun create(@Body request: MarcaRequestDTO): MarcaResponseDTO

    @GET("api/marcas")
    suspend fun getAll(): List<MarcaResponseDTO>

    @GET("api/marcas/{id}")
    suspend fun getById(@Path("id") id: Long): MarcaResponseDTO

    @PUT("api/marcas/{id}")
    suspend fun update(@Path("id") id: Long, @Body request: MarcaRequestDTO): MarcaResponseDTO

    @DELETE("api/marcas/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}