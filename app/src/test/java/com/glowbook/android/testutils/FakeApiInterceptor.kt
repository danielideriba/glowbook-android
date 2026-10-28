package com.glowbook.android.testutils

import com.glowbook.android.data.remote.ApiPath
import com.glowbook.android.di.NetworkModule
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Answers every request with [responseBody]/[code] without touching the network,
 * and records the requests so tests can assert path, method, query and body.
 */
class FakeApiInterceptor(
    var responseBody: String = "[]",
    var code: Int = 200
) : Interceptor {

    val requests = mutableListOf<Request>()

    val lastRequest: Request get() = requests.last()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        requests += request
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(responseBody.toResponseBody("application/json".toMediaType()))
            .build()
    }
}

fun <T> createApiService(service: Class<T>, interceptor: FakeApiInterceptor): T =
    Retrofit.Builder()
        .baseUrl(ApiPath.BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor(interceptor).build())
        .addConverterFactory(GsonConverterFactory.create(NetworkModule.provideGson()))
        .build()
        .create(service)

val Request.relativePath: String
    get() = url.encodedPath.removePrefix("/rest/v1/")

fun Request.bodyAsString(): String = Buffer().also { body!!.writeTo(it) }.readUtf8()
