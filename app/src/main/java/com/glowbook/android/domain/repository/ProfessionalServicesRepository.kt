package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.ProfessionalWithServices
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface ProfessionalServicesRepository {
    fun fetchProfessionalServices(professionalId: Long): Flow<ApiState<ProfessionalWithServices>>
}
