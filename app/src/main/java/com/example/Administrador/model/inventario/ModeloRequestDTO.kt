package com.example.Administrador.model.inventario

data class ModeloRequestDTO(
    val nombre: String,
    val marcaId: Long,
    val categoriaId: Long,
    val generoId: Long
)