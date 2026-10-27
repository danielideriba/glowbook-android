package com.glowbook.android.utils

import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    call: suspend () -> Response<T>
): ApiState<T> {
    return try {
        val response = call()
        when {
            response.isSuccessful -> {
                val body = response.body()

                body?.let {
                    ApiState.Success(it)
                } ?: apiError("Empty body")
            }
            response.code() == 404 -> ApiState.Error("Not found", 404)
            else -> ApiState.Error(code = response.code(), message = response.message())
        }
    } catch (e: SocketTimeoutException) {
        ApiState.Error(code = -1, message = e.message ?: "Servidor inacessível")
    } catch (e: UnknownHostException) {
        ApiState.Error(code = -1, message = "Sem conexão com a internet")
    } catch (e: Exception) {
        ApiState.Error(code = -1, message = e.message ?: "Erro inesperado")
    }
}

sealed class ApiState<out T> {
    data class Success<T>(val data: T) : ApiState<T>()
    data class Error(val message: String, val code: Int? = null) : ApiState<Nothing>()
}

fun <T> apiError(errorMessage: String): ApiState<T> = ApiState.Error(errorMessage)