package com.example.Administrador.interfaces

import com.example.Administrador.model.login.LoginRequestDTO
import com.example.Administrador.model.login.LoginResponseDTO
import com.example.Administrador.model.login.RegisterRequestDTO
import com.example.Administrador.model.login.UsuarioResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun registrar(
        @Body request: RegisterRequestDTO
    ): UsuarioResponseDTO

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDTO
    ): LoginResponseDTO
}
