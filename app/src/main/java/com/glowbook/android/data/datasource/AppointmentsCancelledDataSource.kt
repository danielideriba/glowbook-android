package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import retrofit2.Response

interface AppointmentsCancelledDataSource {
    suspend fun fetchAppointmentsCancelled(): Response<List<AppointmentsHistoryResponse>>
}
