package com.quetoquenana.and.core.network

import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class NetworkCallTest {

    @Test
    fun `networkCall unwraps api response data`() = runTest {
        val result = networkCall {
            ApiResponse(
                message = "Success",
                data = 42,
            )
        }

        assertTrue(result == 42)
    }

    @Test
    fun `networkCall maps http 404 to not found`() = runTest {
        val exception = runCatching {
            networkCall<Int> {
                throw HttpException(
                    Response.error<String>(
                        404,
                        "missing".toResponseBody("text/plain".toMediaType())
                    )
                )
            }
        }.exceptionOrNull()

        assertTrue(exception is NetworkException.NotFound)
    }

    @Test
    fun `networkCall maps timeouts to timeout exception`() = runTest {
        val exception = runCatching {
            networkCall<Int> {
                throw SocketTimeoutException("timeout")
            }
        }.exceptionOrNull()

        assertTrue(exception is NetworkException.Timeout)
    }

    @Test
    fun `networkCall maps connection errors to no connection`() = runTest {
        val exception = runCatching {
            networkCall<Int> {
                throw ConnectException("connection refused")
            }
        }.exceptionOrNull()

        assertTrue(exception is NetworkException.NoConnection)
    }

    @Test
    fun `networkCall maps json parsing failures to serialization`() = runTest {
        val exception = runCatching {
            networkCall<Int> {
                throw JsonDataException("bad json")
            }
        }.exceptionOrNull()

        assertTrue(exception is NetworkException.Serialization)
    }

    @Test
    fun `networkCall maps generic io to transport`() = runTest {
        val exception = runCatching {
            networkCall<Int> {
                throw IOException("broken pipe")
            }
        }.exceptionOrNull()

        assertTrue(exception is NetworkException.Transport)
    }
}


