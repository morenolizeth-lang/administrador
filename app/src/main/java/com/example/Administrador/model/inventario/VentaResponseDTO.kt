package com.example.Administrador.model.inventario

import java.time.LocalDateTime

data class VentaResponseDTO(
    val idVenta: Long,
    val fechaVenta: LocalDateTime,
    val cantidad: Int,
    val precioUnitario: Int,
    val total: Int,
    val productoId: Long,
    val productoDescripcion: String,
    val usuarioId: Long,
    val usuarioNombre: String
)