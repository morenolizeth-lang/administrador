package com.example.Administrador.model.inventario

import com.google.gson.annotations.SerializedName

data class CategoriaResponseDTO(
    @SerializedName("idCategoria")
    val idCategoria: Long,
    val nombre: String
)