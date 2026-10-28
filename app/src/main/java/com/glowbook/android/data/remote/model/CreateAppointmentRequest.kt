package com.glowbook.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class CreateAppointmentRequest(
    @SerializedName("p_data") val data: String,
    @SerializedName("p_horario") val horario: String,
    @SerializedName("p_profissional_id") val profissionalId: Long,
    @SerializedName("p_servicos") val servicos: List<Long>
)
