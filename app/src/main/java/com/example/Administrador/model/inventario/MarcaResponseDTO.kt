package com.example.Administrador.model.inventario

import com.google.gson.annotations.SerializedName

data class MarcaResponseDTO(
    @SerializedName("idMarca")
    val idMarca: Long,
    val nombre: String
)