package com.glowbook.android.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServicesResponse(
    val id: Long,
    val nome: String,
    @SerialName("categoria_id") val categoriaId: Long,
    val preco: Double? = null,
    @SerialName("duracao_minutos") val duracaoMinutos: Int? = null,
    val ativo: Boolean = true,
    val icone: String? = null
)
