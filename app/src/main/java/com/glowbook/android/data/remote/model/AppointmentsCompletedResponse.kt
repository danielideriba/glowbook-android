package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppointmentsCompletedResponse(
    val id: Long,
    val servicos: String? = null,
    val valor: Double? = null,
    @SerializedName("duracao_total") val duracaoTotal: Int? = null,
    val data: String,
    val horario: String,
    val status: String,
    @SerializedName("profissional_id") val profissionalId: Long,
    val profissional: String,
    val avatar: String? = null,
    val avaliacao: Double,
    val categoria: String
)
