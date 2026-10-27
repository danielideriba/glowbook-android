package com.glowbook.android.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentsServiceResponse(
    @SerialName("agendamento_id") val agendamentoId: Long,
    val preco: Double,
    @SerialName("servicos") val servico: ServiceSummary
)

@Serializable
data class ServiceSummary(
    val nome: String,
    val icone: String? = null,
    @SerialName("duracao_minutos") val duracaoMinutos: Int? = null
)
