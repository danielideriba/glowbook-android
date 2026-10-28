package com.glowbook.android.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BearerInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CreateAppointmentClient
