package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfessionalsApiService {
    @GET("profissionais")
    suspend fun profissionais(
        @Query("select") select: String = "*",
        @Query("disponivel") disponivel: String = "eq.true",
        @Query("order") order: String = "id.asc",
        @Query("limit") limit: Int
    ): Response<List<ProfessionalsResponse>>

    @GET("profissionais")
    suspend fun profissionalById(
        @Query("select") select: String = "*",
        @Query("id") id: String
    ): Response<List<ProfessionalsResponse>>
}