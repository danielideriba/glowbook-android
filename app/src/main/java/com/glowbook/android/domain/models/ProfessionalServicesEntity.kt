package com.glowbook.android.domain.models

data class ProfessionalServicesEntity(
    val professional: ProfessionalEntity,
    val services: List<ServiceEntity>
)
