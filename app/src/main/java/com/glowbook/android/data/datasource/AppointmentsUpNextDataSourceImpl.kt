package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.model.AppointmentUpNextResponse
import retrofit2.Response
import javax.inject.Inject

class AppointmentsUpNextDataSourceImpl @Inject constructor(
    private val appointmentsUpNextApiService: AppointmentsUpNextApiService
) : AppointmentsUpNextDataSource {
    override suspend fun fetchAppointmentsUpNext(): Response<List<AppointmentUpNextResponse>> =
        appointmentsUpNextApiService.appointmentsUpNext()
}
