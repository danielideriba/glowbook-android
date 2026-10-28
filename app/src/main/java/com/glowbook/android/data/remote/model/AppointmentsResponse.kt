package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppointmentsResponse(
    val id: Long,
    val data: String,
    val horario: String,
    val status: String,
    @SerializedName("profissional_id") val profissionalId: Long? = null
)
