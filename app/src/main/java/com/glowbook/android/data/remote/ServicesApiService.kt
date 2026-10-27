package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.ServicesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicesApiService {
    @GET("rest/v1/servicos")
    suspend fun profissionais(
        @Query("select") select: String = "*",
        @Query("ativo") ativo: String = "eq.true",
        @Query("order") order: String = "id.asc"
    ): List<ServicesResponse>
}