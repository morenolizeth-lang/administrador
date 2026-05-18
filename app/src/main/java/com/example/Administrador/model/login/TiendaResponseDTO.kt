package com.example.Administrador.model.login

import com.google.gson.annotations.SerializedName


data class TiendaResponseDTO(
    @SerializedName("idTienda")
    val idTienda: Long,
    val nombre: String,
    val direccion: String,
    val telefono: String
)