// UsuarioUpdateDTO.kt
package com.example.Administrador.model.login

data class UsuarioUpdateDTO(
    val nombre: String,
    val correo: String,
    val rol: String,
    val estado: Boolean,
    val tiendaId: Long?,
    val password: String  // ← Opcional, solo se envía si se cambia
)