package com.quetoquenana.and.core.network

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class ApiResponse<T>(
    val message: String,
    val errorCode: Int = 0,
    val data: T
)

fun Throwable.toNetworkException(): NetworkException {
    if (this is CancellationException) throw this
    if (this is NetworkException) return this

    return when (this) {
        is HttpException -> when (code()) {
            400, 422 -> NetworkException.Validation(message())
            401 -> NetworkException.Unauthorized(message())
            403 -> NetworkException.Forbidden(message())
            404 -> NetworkException.NotFound(message())
            409 -> NetworkException.Conflict(message())
            in 500..599 -> NetworkException.Server(message())
            else -> NetworkException.Unknown(cause = this)
        }

        is SocketTimeoutException,
        is InterruptedIOException -> NetworkException.Timeout(cause = this)
        is UnknownHostException,
        is ConnectException,
        is NoRouteToHostException -> NetworkException.NoConnection(cause = this)

        is JsonDataException,
        is JsonEncodingException,
        is EOFException -> NetworkException.Serialization(cause = this)

        is IOException -> NetworkException.Transport(cause = this)
        else -> NetworkException.Unknown(cause = this)
    }
}

suspend fun <T> networkCall(block: suspend () -> ApiResponse<T>): T {
    try {
        return block().data
    } catch (t: Throwable) {
        throw t.toNetworkException()
    }
}

sealed class NetworkException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class Unauthorized(message: String? = null) : NetworkException(message)
    class Forbidden(message: String? = null) : NetworkException(message)
    class NotFound(message: String? = null) : NetworkException(message)
    class Conflict(message: String? = null) : NetworkException(message)
    class Validation(message: String? = null) : NetworkException(message)
    class Server(message: String? = null) : NetworkException(message)
    class Timeout(cause: Throwable? = null) : NetworkException(cause = cause)
    class NoConnection(cause: Throwable? = null) : NetworkException(cause = cause)
    class Transport(cause: Throwable? = null) : NetworkException(cause = cause)
    class Serialization(cause: Throwable? = null) : NetworkException(cause = cause)
    class Unknown(cause: Throwable? = null) : NetworkException(cause = cause)
}