package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsHistoryDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.data.remote.model.ServicesResponse
import com.glowbook.android.domain.repository.AppointmentsHistoryRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppointmentsHistoryRepositoryImpl @Inject constructor(
    private val appointmentsHistoryDataSource: AppointmentsHistoryDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val dispatchersProvider: DispatchersProvider
) : AppointmentsHistoryRepository {
    override fun fetchAppointmentsHistory(): Flow<ApiState<List<AppointmentHistoryWithServices>>> = flow {
        val historyResult = safeApiCall { appointmentsHistoryDataSource.fetchAppointmentsHistory() }

        if (historyResult is ApiState.Error) {
            emit(historyResult)
            return@flow
        }

        val history = if (historyResult is ApiState.Success) {
            historyResult.data
        } else {
            emptyList()
        }

        val serviceIds = history.mapNotNull { it.idServicos }.distinct()
        val servicesResult = if (serviceIds.isEmpty()) {
            ApiState.Success(emptyList<ServicesResponse>())
        } else {
            safeApiCall { servicesDataSource.fetchServicesByIds(serviceIds) }
        }

        if (servicesResult is ApiState.Error) {
            emit(servicesResult)
            return@flow
        }

        val servicesById = if (servicesResult is ApiState.Success) {
            servicesResult.data.associateBy { it.id }
        } else {
            emptyMap()
        }

        val combined = history
            .groupBy { it.id }
            .map { (_, rows) ->
                AppointmentHistoryWithServices(
                    appointment = rows.first(),
                    services = rows.mapNotNull { row -> row.idServicos?.let { servicesById[it] } }
                )
            }

        emit(ApiState.Success(combined))

    }.flowOn(dispatchersProvider.io())
}
