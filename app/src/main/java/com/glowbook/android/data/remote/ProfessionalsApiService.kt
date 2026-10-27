package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfessionalsApiService {
    @GET("rest/v1/profissionais")
    suspend fun profissionais(
        @Query("select") select: String = "*",
        @Query("disponivel") disponivel: String = "eq.true",
        @Query("order") order: String = "id.asc"
    ): Response<List<ProfessionalsResponse>>
}