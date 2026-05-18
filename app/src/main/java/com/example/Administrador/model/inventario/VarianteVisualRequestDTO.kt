package com.example.Administrador.model.inventario

data class VarianteVisualRequestDTO(
    val modeloId: Long,
    val colorPrimarioId: Long,
    val colorSecundarioId: Long?,
    val imagen: String?,
    val estado: Boolean
)