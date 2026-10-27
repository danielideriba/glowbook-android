package com.glowbook.android.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfessionalsResponse(
    val id: Long,
    val nome: String,
    val especialidade: String,
    val avaliacao: Double,
    val avatar: String? = null,
    @SerialName("cor_fundo") val corFundo: String? = null,
    val disponivel: Boolean = true,
    @SerialName("categoria_id") val categoriaId: Long
)
