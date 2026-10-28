package com.glowbook.android.domain.models

data class ProfessionalCalendarEntity(
    val professional: ProfessionalEntity,
    val services: List<ServiceEntity>,
    val bookedSlots: List<BookedSlotEntity>
)
