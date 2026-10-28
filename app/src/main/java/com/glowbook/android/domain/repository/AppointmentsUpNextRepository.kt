package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.AppointmentUpNextWithServices
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface AppointmentsUpNextRepository {
    fun fetchAppointmentsUpNext(): Flow<ApiState<List<AppointmentUpNextWithServices>>>
}
