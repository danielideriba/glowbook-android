package com.glowbook.android.domain.entity

import com.glowbook.android.data.remote.model.ProfessionalsResponse

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

fun ProfessionalsResponse.toEntity() = ProfessionalEntity(
    id = id,
    name = nome,
    specialty = especialidade,
    rating = avaliacao,
    avatar = avatar,
    avatarColor = corFundo,
    available = disponivel,
    categoryId = categoriaId
)
