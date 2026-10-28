package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import retrofit2.Response
import javax.inject.Inject

class CreateAppointmentDataSourceImpl @Inject constructor(
    private val createAppointmentApiService: CreateAppointmentApiService
) : CreateAppointmentDataSource {
    override suspend fun createAppointment(request: CreateAppointmentRequest): Response<Long> =
        createAppointmentApiService.createAppointment(request)
}
