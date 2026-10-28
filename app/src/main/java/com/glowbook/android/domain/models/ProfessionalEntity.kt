package com.glowbook.android.domain.models

data class ProfessionalEntity(
    val id: Long,
    val name: String,
    val specialty: String,
    val rating: Double,
    val avatar: String?,
    val avatarColor: String?,
    val available: Boolean,
    val categoryId: Long
)
