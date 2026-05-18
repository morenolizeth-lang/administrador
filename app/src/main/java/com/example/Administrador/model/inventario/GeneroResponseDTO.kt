package com.example.Administrador.model.inventario

import com.google.gson.annotations.SerializedName

data class GeneroResponseDTO(
    @SerializedName("idGenero")
    val idGenero: Long,
    val nombre: String
)