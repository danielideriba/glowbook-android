package com.glowbook.android.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentEntity(
    val id: Long,
    val services: String,
    val price: Double?,
    val durationMinutes: Int?,
    val date: LocalDate,
    val time: LocalTime,
    val status: String,
    val professionalId: Long,
    val professionalName: String,
    val professionalAvatar: String?,
    val professionalRating: Double,
    val category: String,
    val isPast: Boolean = false
)
