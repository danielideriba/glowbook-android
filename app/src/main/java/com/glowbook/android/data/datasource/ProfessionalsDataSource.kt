package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import retrofit2.Response

interface ProfessionalsDataSource {
    suspend fun fetchProfessionalsAvailable(limit: Int): Response<List<ProfessionalsResponse>>
    suspend fun fetchProfessionalById(id: Long): Response<List<ProfessionalsResponse>>
}