package com.glowbook.android.data.remote.model

data class ProfessionalCalendarResponse(
    val professional: ProfessionalsResponse,
    val services: List<ServicesResponse>,
    val appointments: List<AppointmentsResponse>
)
