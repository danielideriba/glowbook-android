package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.ServicesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicesApiService {
    @GET("servicos")
    suspend fun services(
        @Query("select") select: String = "*",
        @Query("ativo") ativo: String = "eq.true",
        @Query("id") id: String? = null,  // ex.: "in.(1,3)"
        @Query("order") order: String = "id.asc"
    ): Response<List<ServicesResponse>>
}
