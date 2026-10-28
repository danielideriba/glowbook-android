package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.model.ProfessionalsResponse
import retrofit2.Response
import javax.inject.Inject

class ProfessionalsDataSourceImpl @Inject constructor(
    private val professionalsApiService: ProfessionalsApiService
) : ProfessionalsDataSource {
    override suspend fun fetchProfessionalsAvailable(limit: Int): Response<List<ProfessionalsResponse>> =
        professionalsApiService.profissionais(limit = limit)

    override suspend fun fetchProfessionalById(id: Long): Response<List<ProfessionalsResponse>> =
        professionalsApiService.profissionalById(id = "eq.$id")
}
