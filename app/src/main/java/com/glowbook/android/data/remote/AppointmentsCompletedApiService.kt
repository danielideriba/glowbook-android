package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsCompletedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsCompletedApiService {
    @GET("rest/v1/agendamentos_completos")
    suspend fun appointmentsCompleted(
        @Query("select") select: String = "*",
        @Query("order") order: String = "data.asc"
    ): List<AppointmentsCompletedResponse>
}