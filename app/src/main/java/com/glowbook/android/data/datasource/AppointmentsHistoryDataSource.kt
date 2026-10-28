package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import retrofit2.Response

interface AppointmentsHistoryDataSource {
    suspend fun fetchAppointmentsHistory(): Response<List<AppointmentsHistoryResponse>>
}
