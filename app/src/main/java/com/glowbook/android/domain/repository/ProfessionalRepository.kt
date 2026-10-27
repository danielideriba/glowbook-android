package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.utils.ApiState

interface ProfessionalsRepository {
    suspend fun fetchProfessionalsAvailable(): ApiState<List<ProfessionalsResponse>>
}