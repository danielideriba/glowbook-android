package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.ProfessionalWithServices
import com.glowbook.android.data.remote.model.ServicesResponse
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DispatchersProvider
import com.glowbook.android.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfessionalServicesRepositoryImpl @Inject constructor(
    private val professionalsDataSource: ProfessionalsDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val dispatchersProvider: DispatchersProvider
) : ProfessionalServicesRepository {
    override fun fetchProfessionalServices(professionalId: Long): Flow<ApiState<ProfessionalWithServices>> = flow {
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

        val servicesResult = safeApiCall { servicesDataSource.fetchAllServices() }

        if (servicesResult is ApiState.Error) {
            emit(servicesResult)
            return@flow
        }

        val services = if (servicesResult is ApiState.Success) {
            servicesResult.data
        } else {
            emptyList<ServicesResponse>()
        }

        val combined = ProfessionalWithServices(professional = professional, services = services)

        emit(ApiState.Success(combined))

    }.flowOn(dispatchersProvider.io())
}
