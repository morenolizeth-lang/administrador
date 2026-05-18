package com.example.inventariolt.model.inventario

import com.google.gson.annotations.SerializedName

data class GeneroResponseDTO(
    @SerializedName("idGenero")
    val idGenero: Long,
    val nombre: String
)