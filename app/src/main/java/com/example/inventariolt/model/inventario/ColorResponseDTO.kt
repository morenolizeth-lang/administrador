package com.example.inventariolt.model.inventario

import com.google.gson.annotations.SerializedName

data class ColorResponseDTO(
    @SerializedName("idColor")
    val idColor: Long,
    val nombre: String
)