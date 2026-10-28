package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.AppointmentsResponse
import retrofit2.Response

interface AppointmentsDataSource {
    suspend fun fetchProfessionalAppointments(
        professionalId: Long,
        startDate: String,
        endDate: String
    ): Response<List<AppointmentsResponse>>
}
