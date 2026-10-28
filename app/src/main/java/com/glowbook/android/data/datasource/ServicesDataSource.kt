package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.ServicesResponse
import retrofit2.Response

interface ServicesDataSource {
    suspend fun fetchServicesByIds(ids: List<Long>): Response<List<ServicesResponse>>
    suspend fun fetchAllServices(): Response<List<ServicesResponse>>
}
