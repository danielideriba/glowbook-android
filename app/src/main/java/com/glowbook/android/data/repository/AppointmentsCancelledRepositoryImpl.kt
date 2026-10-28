package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsCancelledDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.data.remote.model.ServicesResponse
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppointmentsCancelledRepositoryImpl @Inject constructor(
    private val appointmentsCancelledDataSource: AppointmentsCancelledDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val dispatchersProvider: DispatchersProvider
) : AppointmentsCancelledRepository {
    override fun fetchAppointmentsCancelled(): Flow<ApiState<List<AppointmentHistoryWithServices>>> = flow {
        val cancelledResult = safeApiCall { appointmentsCancelledDataSource.fetchAppointmentsCancelled() }

        if (cancelledResult is ApiState.Error) {
            emit(cancelledResult)
            return@flow
        }

        val cancelled = if (cancelledResult is ApiState.Success) {
            cancelledResult.data
        } else {
            emptyList()
        }

        val serviceIds = cancelled.mapNotNull { it.idServicos }.distinct()
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

        val combined = cancelled
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
