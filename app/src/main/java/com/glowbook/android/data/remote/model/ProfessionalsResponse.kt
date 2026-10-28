package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProfessionalsResponse(
    val id: Long,
    val nome: String,
    val especialidade: String,
    val avaliacao: Double,
    val avatar: String? = null,
    @SerializedName("cor_fundo") val corFundo: String? = null,
    val disponivel: Boolean = true,
    @SerializedName("categoria_id") val categoriaId: Long
)
