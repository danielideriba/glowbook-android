package com.glowbook.android.data.repository

import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.safeApiCall
import javax.inject.Inject

class ProfessionalsRepositoryImpl @Inject constructor(
    private val professionalsApiService: ProfessionalsApiService
) : ProfessionalsRepository {
    override suspend fun fetchProfessionalsAvailable(): ApiState<List<ProfessionalsResponse>> =
        safeApiCall { professionalsApiService.profissionais() }
}
