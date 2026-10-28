package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsHistoryApiService {
    @GET("historico_agendamentos")
    suspend fun appointmentsHistory(
        @Query("select") select: String = "*",
    ): Response<List<AppointmentsHistoryResponse>>

    @GET("historico_agendamentos")
    suspend fun appointmentsCancelled(
        @Query("select") select: String = "*",
        @Query("status") status: String = "eq.cancelado",
    ): Response<List<AppointmentsHistoryResponse>>
}
