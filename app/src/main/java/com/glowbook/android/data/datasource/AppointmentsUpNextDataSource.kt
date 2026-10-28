package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.AppointmentUpNextResponse
import retrofit2.Response

interface AppointmentsUpNextDataSource {
    suspend fun fetchAppointmentsUpNext(): Response<List<AppointmentUpNextResponse>>
}
