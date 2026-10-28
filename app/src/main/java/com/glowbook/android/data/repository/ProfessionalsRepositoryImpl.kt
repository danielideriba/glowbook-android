package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfessionalsRepositoryImpl @Inject constructor(
    private val professionalsDataSource: ProfessionalsDataSource,
    private val dispatchersProvider: DispatchersProvider
) : ProfessionalsRepository {
    override fun fetchProfessionalsAvailable(limit: Int): Flow<ApiState<List<ProfessionalsResponse>>> = flow {
        val professionalsResult = safeApiCall { professionalsDataSource.fetchProfessionalsAvailable(limit) }

        if (professionalsResult is ApiState.Error) {
            emit(professionalsResult)
            return@flow
        }

        val professionals = if (professionalsResult is ApiState.Success) {
            professionalsResult.data
        } else {
            emptyList()
        }

        emit(ApiState.Success(professionals))

    }.flowOn(dispatchersProvider.io())
}
