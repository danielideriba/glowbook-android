package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateAppointmentApiService {
    @POST("rpc/criar_agendamento")
    suspend fun createAppointment(
        @Body body: CreateAppointmentRequest
    ): Response<Long>
}
