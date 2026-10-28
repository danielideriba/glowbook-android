package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsCompletedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsCompletedApiService {
    @GET("agendamentos_completos")
    suspend fun appointmentsCompleted(
        @Query("select") select: String = "*",
        @Query("status") status: String = "in.(confirmado,pendente)",
        @Query("order") order: String = "data.asc,horario.asc",
        @Query("limit") limit: Int
    ): Response<List<AppointmentsCompletedResponse>>
}