package com.glowbook.android.domain.models

data class ServiceEntity(
    val id: Long,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val icon: String?,
    val categoryId: Long
)
