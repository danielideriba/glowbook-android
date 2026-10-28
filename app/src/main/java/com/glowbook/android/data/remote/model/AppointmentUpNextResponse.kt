package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppointmentUpNextResponse(
    val id: Long,
    @SerializedName("id_servicos") val idServicos: Long,
    val data: String,
    val horario: String,
    val status: String,
    @SerializedName("profissional_id") val profissionalId: Long,
    val profissional: String,
    val avatar: String? = null,
    val avaliacao: Double,
    val categoria: String
)
