package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import retrofit2.Response

interface CreateAppointmentDataSource {
    suspend fun createAppointment(request: CreateAppointmentRequest): Response<Long>
}
