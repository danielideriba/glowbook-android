package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.model.CategoriesResponses
import retrofit2.Response

interface CategoriesDataSource {
    suspend fun fetchCategories(): Response<List<CategoriesResponses>>
}
