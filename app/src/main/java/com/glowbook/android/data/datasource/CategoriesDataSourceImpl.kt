package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.model.CategoriesResponses
import retrofit2.Response
import javax.inject.Inject

class CategoriesDataSourceImpl @Inject constructor(
    private val categoriesApiServices: CategoriesApiServices
) : CategoriesDataSource {
    override suspend fun fetchCategories(): Response<List<CategoriesResponses>> =
        categoriesApiServices.categories()
}
