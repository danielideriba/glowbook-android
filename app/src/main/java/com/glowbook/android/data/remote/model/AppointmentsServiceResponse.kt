package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppointmentsServiceResponse(
    @SerializedName("agendamento_id") val agendamentoId: Long,
    val preco: Double,
    @SerializedName("servicos") val servico: ServiceSummary
)

data class ServiceSummary(
    val nome: String,
    val icone: String? = null,
    @SerializedName("duracao_minutos") val duracaoMinutos: Int? = null
)
