package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsServiceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsServiceApiService {
    @GET("rest/v1/agendamento_servicos")
    suspend fun appointmentsServices(
        @Query("select") select: String =
            "agendamento_id,preco,servicos(nome,icone,duracao_minutos)",
        @Query("agendamento_id") agendamentoId: String? = null,  // ex.: "eq.1"
        @Query("order") order: String = "agendamento_id.asc"
    ): List<AppointmentsServiceResponse>
}