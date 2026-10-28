package com.glowbook.android.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiStateTest {

    private fun <T> errorResponse(code: Int): Response<T> =
        Response.error(code, "{}".toResponseBody("application/json".toMediaType()))

    @Test
    fun `safeApiCall returns success with body`() = runTest {
        val result = safeApiCall { Response.success(listOf(1, 2)) }

        assertEquals(ApiState.Success(listOf(1, 2)), result)
    }

    @Test
    fun `safeApiCall returns error when successful body is null`() = runTest {
        val result = safeApiCall<String> { Response.success(null) }

        assertEquals(ApiState.Error("Empty body"), result)
    }

    @Test
    fun `safeApiCall maps 404 to not found`() = runTest {
        val result = safeApiCall<String> { errorResponse(404) }

        assertEquals(ApiState.Error("Not found", 404), result)
    }

    @Test
    fun `safeApiCall keeps code and message of other http errors`() = runTest {
        val result = safeApiCall<String> { errorResponse(500) }

        assertEquals(ApiState.Error("Response.error()", 500), result)
    }

    @Test
    fun `safeApiCall maps socket timeout with its message`() = runTest {
        val result = safeApiCall<String> { throw SocketTimeoutException("timeout") }

        assertEquals(ApiState.Error("timeout", -1), result)
    }

    @Test
    fun `safeApiCall maps socket timeout without message to unreachable server`() = runTest {
        val result = safeApiCall<String> { throw SocketTimeoutException() }

        assertEquals(ApiState.Error("Servidor inacessível", -1), result)
    }

    @Test
    fun `safeApiCall maps unknown host to no internet`() = runTest {
        val result = safeApiCall<String> { throw UnknownHostException("ntwcnrtclnsjlyfamztq.supabase.co") }

        assertEquals(ApiState.Error("Sem conexão com a internet", -1), result)
    }

    @Test
    fun `safeApiCall maps other exceptions with their message`() = runTest {
        val result = safeApiCall<String> { throw IOException("connection reset") }

        assertEquals(ApiState.Error("connection reset", -1), result)
    }

    @Test
    fun `safeApiCall maps exceptions without message to unexpected error`() = runTest {
        val result = safeApiCall<String> { throw IllegalStateException() }

        assertEquals(ApiState.Error("Erro inesperado", -1), result)
    }

    @Test
    fun `safeApiCall also catches cancellation`() = runTest {
        // Documents current behavior: CancellationException is an Exception and is swallowed.
        val result = safeApiCall<String> { throw CancellationException("cancelled") }

        assertEquals(ApiState.Error("cancelled", -1), result)
    }

    @Test
    fun `apiError builds error without code`() {
        val error = apiError<String>("boom") as ApiState.Error

        assertEquals("boom", error.message)
        assertNull(error.code)
    }

    @Test
    fun `api state success holds data`() {
        assertEquals("data", ApiState.Success("data").data)
    }
}
