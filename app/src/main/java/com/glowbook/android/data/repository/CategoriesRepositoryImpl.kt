package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.CategoriesDataSource
import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesDataSource: CategoriesDataSource,
    private val dispatchersProvider: DispatchersProvider
) : CategoriesRepository {
    override fun fetchCategories(): Flow<ApiState<List<CategoriesResponses>>> = flow {
        val categoriesResult = safeApiCall { categoriesDataSource.fetchCategories() }

        if (categoriesResult is ApiState.Error) {
            emit(categoriesResult)
            return@flow
        }

        val categories = if (categoriesResult is ApiState.Success) {
            categoriesResult.data
        } else {
            emptyList()
        }

        emit(ApiState.Success(categories))

    }.flowOn(dispatchersProvider.io())
}
