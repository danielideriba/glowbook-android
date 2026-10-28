package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class ServicesResponse(
    val id: Long,
    val nome: String,
    @SerializedName("categoria_id") val categoriaId: Long,
    val preco: Double? = null,
    @SerializedName("duracao_minutos") val duracaoMinutos: Int? = null,
    val ativo: Boolean = true,
    val icone: String? = null
)
