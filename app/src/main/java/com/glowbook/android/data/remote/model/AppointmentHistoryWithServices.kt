package com.glowbook.android.data.remote.model

data class AppointmentHistoryWithServices(
    val appointment: AppointmentsHistoryResponse,
    val services: List<ServicesResponse>
)
