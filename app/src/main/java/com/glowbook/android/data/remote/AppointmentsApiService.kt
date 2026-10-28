package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsApiService {
    @GET("agendamentos")
    suspend fun appointments(
        @Query("select") select: String = "*",
        @Query("profissional_id") profissionalId: String? = null,  // ex.: "eq.1"
        @Query("status") status: String? = "neq.cancelado",
        @Query("data") data: List<String> = emptyList(),  // ex.: ["gte.2026-10-27", "lte.2026-11-02"]
        @Query("order") order: String = "data.asc,horario.asc"
    ): Response<List<AppointmentsResponse>>
}
