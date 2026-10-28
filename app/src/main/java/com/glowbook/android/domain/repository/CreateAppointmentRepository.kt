package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface CreateAppointmentRepository {
    fun createAppointment(request: CreateAppointmentRequest): Flow<ApiState<Long>>
}
