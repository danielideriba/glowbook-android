package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.CategoriesResponses
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoriesApiServices {
    @GET("categorias")
    suspend fun categories(
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.asc"
    ): Response<List<CategoriesResponses>>
}