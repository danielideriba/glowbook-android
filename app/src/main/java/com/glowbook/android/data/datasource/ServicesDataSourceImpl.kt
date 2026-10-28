package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.data.remote.model.ServicesResponse
import retrofit2.Response
import javax.inject.Inject

class ServicesDataSourceImpl @Inject constructor(
    private val servicesApiService: ServicesApiService
) : ServicesDataSource {
    override suspend fun fetchServicesByIds(ids: List<Long>): Response<List<ServicesResponse>> =
        servicesApiService.services(id = "in.(${ids.joinToString(",")})")

    override suspend fun fetchAllServices(): Response<List<ServicesResponse>> =
        servicesApiService.services()
}
