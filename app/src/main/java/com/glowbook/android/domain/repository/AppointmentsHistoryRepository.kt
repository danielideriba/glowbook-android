package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface AppointmentsHistoryRepository {
    fun fetchAppointmentsHistory(): Flow<ApiState<List<AppointmentHistoryWithServices>>>
}
