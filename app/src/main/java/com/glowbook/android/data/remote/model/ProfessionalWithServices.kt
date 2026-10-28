package com.glowbook.android.data.remote.model

data class ProfessionalWithServices(
    val professional: ProfessionalsResponse,
    val services: List<ServicesResponse>
)
