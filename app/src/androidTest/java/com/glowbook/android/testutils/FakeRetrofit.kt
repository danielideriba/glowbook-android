package com.glowbook.android.testutils

import com.glowbook.android.di.NetworkModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
object FakeRetrofit {

    private fun retrofit(vararg interceptors: Interceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .apply { interceptors.forEach { addInterceptor(it) } }
            .addInterceptor(FakeSupabase)
            .build()
        return NetworkModule.provideRetrofit(
            client,
            NetworkModule.provideGsonConverterFactory(NetworkModule.provideGson())
        )
    }

    fun read(): Retrofit = retrofit(NetworkModule.provideAuthInterceptor())

    fun createAppointment(): Retrofit =
        retrofit(NetworkModule.provideAuthInterceptor(), NetworkModule.provideBearerInterceptor())
}
