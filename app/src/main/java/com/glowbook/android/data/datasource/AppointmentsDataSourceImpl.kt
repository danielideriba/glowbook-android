package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.model.AppointmentsResponse
import retrofit2.Response
import javax.inject.Inject

class AppointmentsDataSourceImpl @Inject constructor(
    private val appointmentsApiService: AppointmentsApiService
) : AppointmentsDataSource {
    override suspend fun fetchProfessionalAppointments(
        professionalId: Long,
        startDate: String,
        endDate: String
    ): Response<List<AppointmentsResponse>> =
        appointmentsApiService.appointments(
            profissionalId = "eq.$professionalId",
            data = listOf("gte.$startDate", "lte.$endDate")
        )
}
