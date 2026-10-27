package com.glowbook.android.data.remote.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentsCompletedResponse(
    val id: Long,
    val servicos: String? = null,
    val valor: Double? = null,
    @SerialName("duracao_total") val duracaoTotal: Int? = null,
    val data: LocalDate,
    val horario: LocalTime,
    val status: String,
    @SerialName("profissional_id") val profissionalId: Long,
    val profissional: String,
    val avatar: String? = null,
    val avaliacao: Double,
    val categoria: String
)
