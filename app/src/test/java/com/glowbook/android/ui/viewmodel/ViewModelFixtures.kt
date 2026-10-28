package com.glowbook.android.ui.viewmodel

import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ServiceEntity
import java.time.LocalDate
import java.time.LocalTime

internal fun professionalEntity(id: Long = 1) =
    ProfessionalEntity(id, "Camila Rocha", "Cabeleireira", 4.9, null, null, true, 1)

internal fun serviceEntity(id: Long) = ServiceEntity(id, "Serviço $id", 100.0, 60, null, 1)

internal fun appointmentEntity(id: Long, status: String = "confirmado") = AppointmentEntity(
    id = id,
    services = "Corte",
    price = 120.0,
    durationMinutes = 45,
    date = LocalDate.of(2026, 10, 30),
    time = LocalTime.of(14, 0),
    status = status,
    professionalId = 1,
    professionalName = "Camila Rocha",
    professionalAvatar = null,
    professionalRating = 4.9,
    category = "Cabelo"
)
