package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.ProfessionalCalendarResponse
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface ProfessionalCalendarRepository {
    fun fetchProfessionalCalendar(
        professionalId: Long,
        serviceIds: List<Long>,
        startDate: String,
        endDate: String
    ): Flow<ApiState<ProfessionalCalendarResponse>>
}
