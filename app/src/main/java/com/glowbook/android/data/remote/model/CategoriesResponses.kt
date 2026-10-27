package com.glowbook.android.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponses(
    val id: Long,
    val nome: String
)
