package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface AppointmentsCancelledRepository {
    fun fetchAppointmentsCancelled(): Flow<ApiState<List<AppointmentHistoryWithServices>>>
}
