package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentsApiService {
    @GET("rest/v1/agendamentos")
    suspend fun appointments(
        @Query("select") select: String = "*",
        @Query("order") order: String = "data.asc"
    ): List<AppointmentsResponse>
}