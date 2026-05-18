package com.example.Administrador.model.inventario

import com.google.gson.annotations.SerializedName

data class ColorResponseDTO(
    @SerializedName("idColor")
    val idColor: Long,
    val nombre: String
)