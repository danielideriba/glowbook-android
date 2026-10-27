package com.glowbook.android.data.remote.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentsResponse(
    val id: Long,
    val data: LocalDate,
    val horario: LocalTime,
    val status: String,
    @SerialName("profissional_id") val profissionalId: Long? = null
)
