package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.CreateAppointmentDataSource
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateAppointmentRepositoryImpl @Inject constructor(
    private val createAppointmentDataSource: CreateAppointmentDataSource,
    private val dispatchersProvider: DispatchersProvider
) : CreateAppointmentRepository {
    override fun createAppointment(request: CreateAppointmentRequest): Flow<ApiState<Long>> = flow {
        val createResult = safeApiCall { createAppointmentDataSource.createAppointment(request) }

        emit(createResult)

    }.flowOn(dispatchersProvider.io())
}
