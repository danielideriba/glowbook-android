package com.glowbook.android.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class BookedSlotEntity(
    val date: LocalDate,
    val time: LocalTime
)
