package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface ProfessionalsRepository {
    fun fetchProfessionalsAvailable(limit: Int): Flow<ApiState<List<ProfessionalsResponse>>>
}