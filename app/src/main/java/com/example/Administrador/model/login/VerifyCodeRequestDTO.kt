package com.example.Administrador.model.login

data class VerifyCodeRequestDTO(
    val correo: String,
    val codigo: String
)