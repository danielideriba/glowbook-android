package com.glowbook.android.di

import com.glowbook.android.data.datasource.AppointmentsCancelledDataSource
import com.glowbook.android.data.datasource.AppointmentsDataSource
import com.glowbook.android.data.datasource.AppointmentsDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsCancelledDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSource
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSource
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSourceImpl
import com.glowbook.android.data.datasource.CategoriesDataSource
import com.glowbook.android.data.datasource.CategoriesDataSourceImpl
import com.glowbook.android.data.datasource.CreateAppointmentDataSource
import com.glowbook.android.data.datasource.CreateAppointmentDataSourceImpl
import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.datasource.ServicesDataSourceImpl
import com.glowbook.android.data.datasource.ProfessionalsDataSourceImpl
import com.glowbook.android.data.repository.AppointmentsCancelledRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsHistoryRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsUpNextRepositoryImpl
import com.glowbook.android.data.repository.CategoriesRepositoryImpl
import com.glowbook.android.data.repository.CreateAppointmentRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalCalendarRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalServicesRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalsRepositoryImpl
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
import com.glowbook.android.domain.repository.AppointmentsHistoryRepository
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.DefaultDispatcherProvider
import com.glowbook.android.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindProfessionalsRepository(impl: ProfessionalsRepositoryImpl): ProfessionalsRepository

    @Singleton
    @Binds
    abstract fun bindProfessionalsDataSource(impl: ProfessionalsDataSourceImpl): ProfessionalsDataSource

    @Singleton
    @Binds
    abstract fun bindCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

    @Singleton
    @Binds
    abstract fun bindCategoriesDataSource(impl: CategoriesDataSourceImpl): CategoriesDataSource

    @Singleton
    @Binds
    abstract fun bindAppointmentsUpNextRepository(impl: AppointmentsUpNextRepositoryImpl): AppointmentsUpNextRepository

    @Singleton
    @Binds
    abstract fun bindAppointmentsUpNextDataSource(impl: AppointmentsUpNextDataSourceImpl): AppointmentsUpNextDataSource

    @Singleton
    @Binds
    abstract fun bindServicesDataSource(impl: ServicesDataSourceImpl): ServicesDataSource

    @Singleton
    @Binds
    abstract fun bindAppointmentsHistoryRepository(impl: AppointmentsHistoryRepositoryImpl): AppointmentsHistoryRepository

    @Singleton
    @Binds
    abstract fun bindAppointmentsHistoryDataSource(impl: AppointmentsHistoryDataSourceImpl): AppointmentsHistoryDataSource

    @Singleton
    @Binds
    abstract fun bindAppointmentsCancelledRepository(impl: AppointmentsCancelledRepositoryImpl): AppointmentsCancelledRepository

    @Singleton
    @Binds
    abstract fun bindAppointmentsCancelledDataSource(impl: AppointmentsCancelledDataSourceImpl): AppointmentsCancelledDataSource

    @Singleton
    @Binds
    abstract fun bindProfessionalServicesRepository(impl: ProfessionalServicesRepositoryImpl): ProfessionalServicesRepository

    @Singleton
    @Binds
    abstract fun bindProfessionalCalendarRepository(impl: ProfessionalCalendarRepositoryImpl): ProfessionalCalendarRepository

    @Singleton
    @Binds
    abstract fun bindAppointmentsDataSource(impl: AppointmentsDataSourceImpl): AppointmentsDataSource

    @Singleton
    @Binds
    abstract fun bindCreateAppointmentRepository(impl: CreateAppointmentRepositoryImpl): CreateAppointmentRepository

    @Singleton
    @Binds
    abstract fun bindCreateAppointmentDataSource(impl: CreateAppointmentDataSourceImpl): CreateAppointmentDataSource

    @Singleton
    @Binds
    abstract fun provideDispatchersProvider(impl: DefaultDispatcherProvider): DispatchersProvider
}
