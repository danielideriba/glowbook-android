package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppointmentsHistoryResponse(
    val id: Long,
    @SerializedName("id_servicos") val idServicos: Long? = null,
    val data: String,
    val horario: String,
    val status: String,
    @SerializedName("ja_passou") val jaPassou: Boolean,
    @SerializedName("profissional_id") val profissionalId: Long,
    val profissional: String,
    val avatar: String? = null,
    val avaliacao: Double,
    val categoria: String,
    @SerializedName("servicos") val servico: ServiceSummary? = null
)
