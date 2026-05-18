package com.example.inventariolt.model.inventario

import com.google.gson.annotations.SerializedName

data class ApiErrorDTO(
    @SerializedName("status")
    val status: Int,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("path")
    val path: String?,
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("details")
    val details: List<String>? = null
)
