package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsUpNextDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentUpNextWithServices
import com.glowbook.android.data.remote.model.ServicesResponse
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppointmentsUpNextRepositoryImpl @Inject constructor(
    private val appointmentsUpNextDataSource: AppointmentsUpNextDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val dispatchersProvider: DispatchersProvider
) : AppointmentsUpNextRepository {
    override fun fetchAppointmentsUpNext(): Flow<ApiState<List<AppointmentUpNextWithServices>>> = flow {
        val appointmentsResult = safeApiCall { appointmentsUpNextDataSource.fetchAppointmentsUpNext() }

        if (appointmentsResult is ApiState.Error) {
            emit(appointmentsResult)
            return@flow
        }

        val appointments = if (appointmentsResult is ApiState.Success) {
            appointmentsResult.data
        } else {
            emptyList()
        }

        val serviceIds = appointments.map { it.idServicos }.distinct()
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

        val combined = appointments
            .groupBy { it.id }
            .map { (_, rows) ->
                AppointmentUpNextWithServices(
                    appointment = rows.first(),
                    services = rows.mapNotNull { servicesById[it.idServicos] }
                )
            }

        emit(ApiState.Success(combined))

    }.flowOn(dispatchersProvider.io())
}
