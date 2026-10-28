package com.glowbook.android.data.remote.model

data class AppointmentUpNextWithServices(
    val appointment: AppointmentUpNextResponse,
    val services: List<ServicesResponse>
)
