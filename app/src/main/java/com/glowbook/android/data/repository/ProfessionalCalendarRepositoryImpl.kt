package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsDataSource
import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentsResponse
import com.glowbook.android.data.remote.model.ProfessionalCalendarResponse
import com.glowbook.android.data.remote.model.ServicesResponse
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfessionalCalendarRepositoryImpl @Inject constructor(
    private val professionalsDataSource: ProfessionalsDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val appointmentsDataSource: AppointmentsDataSource,
    private val dispatchersProvider: DispatchersProvider
) : ProfessionalCalendarRepository {
    override fun fetchProfessionalCalendar(
        professionalId: Long,
        serviceIds: List<Long>,
        startDate: String,
        endDate: String
    ): Flow<ApiState<ProfessionalCalendarResponse>> = flow {
        val professionalResult = safeApiCall { professionalsDataSource.fetchProfessionalById(professionalId) }

        if (professionalResult is ApiState.Error) {
            emit(professionalResult)
            return@flow
        }

        val professional = (professionalResult as? ApiState.Success)?.data?.firstOrNull()
        if (professional == null) {
            emit(ApiState.Error("Professional $professionalId not found"))
            return@flow
        }

        val servicesResult = if (serviceIds.isEmpty()) {
            ApiState.Success(emptyList<ServicesResponse>())
        } else {
            safeApiCall { servicesDataSource.fetchServicesByIds(serviceIds) }
        }

        if (servicesResult is ApiState.Error) {
            emit(servicesResult)
            return@flow
        }

        val appointmentsResult = safeApiCall {
            appointmentsDataSource.fetchProfessionalAppointments(professionalId, startDate, endDate)
        }

        if (appointmentsResult is ApiState.Error) {
            emit(appointmentsResult)
            return@flow
        }

        val services = if (servicesResult is ApiState.Success) servicesResult.data else emptyList()
        val appointments = if (appointmentsResult is ApiState.Success) {
            appointmentsResult.data
        } else {
            emptyList<AppointmentsResponse>()
        }

        val combined = ProfessionalCalendarResponse(
            professional = professional,
            services = services,
            appointments = appointments
        )

        emit(ApiState.Success(combined))

    }.flowOn(dispatchersProvider.io())
}
