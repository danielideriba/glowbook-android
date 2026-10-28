package com.glowbook.android.domain.repository

import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.utils.ApiState
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun fetchCategories(): Flow<ApiState<List<CategoriesResponses>>>
}
