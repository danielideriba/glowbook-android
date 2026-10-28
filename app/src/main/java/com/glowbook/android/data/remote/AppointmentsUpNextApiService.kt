package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentUpNextResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsUpNextApiService {
    @GET("proximos_agendamentos")
    suspend fun appointmentsUpNext(
        @Query("select") select: String = "*",
        @Query("order") order: String = "data.asc,horario.asc"
    ): Response<List<AppointmentUpNextResponse>>
}
