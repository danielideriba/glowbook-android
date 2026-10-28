package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import retrofit2.Response
import javax.inject.Inject

class AppointmentsHistoryDataSourceImpl @Inject constructor(
    private val appointmentsHistoryApiService: AppointmentsHistoryApiService
) : AppointmentsHistoryDataSource {
    override suspend fun fetchAppointmentsHistory(): Response<List<AppointmentsHistoryResponse>> =
        appointmentsHistoryApiService.appointmentsHistory()
}
