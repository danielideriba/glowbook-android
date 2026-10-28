package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import retrofit2.Response
import javax.inject.Inject

class AppointmentsCancelledDataSourceImpl @Inject constructor(
    private val appointmentsHistoryApiService: AppointmentsHistoryApiService
) : AppointmentsCancelledDataSource {
    override suspend fun fetchAppointmentsCancelled(): Response<List<AppointmentsHistoryResponse>> =
        appointmentsHistoryApiService.appointmentsCancelled()
}
