package com.dentalgo.app.core.network

import com.google.gson.Gson
import retrofit2.Response

abstract class BaseRepository {
    protected suspend fun <T> safeCall(call: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body", response.code())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody, response.code())
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: java.net.UnknownHostException) {
            ApiResult.NetworkError
        } catch (e: java.net.SocketTimeoutException) {
            ApiResult.Error("Request timed out. Please try again.")
        } catch (e: java.io.IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "An unexpected error occurred.")
        }
    }

    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        if (errorBody == null) return httpCodeMessage(code)
        return try {
            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
            when {
                apiError?.message?.isNotBlank() == true -> apiError.message
                else -> httpCodeMessage(code)
            }
        } catch (e: Exception) {
            httpCodeMessage(code)
        }
    }

    private fun httpCodeMessage(code: Int): String = when (code) {
        400 -> "Bad request. Please check your input."
        401 -> "Unauthorized. Please log in again."
        403 -> "You don't have permission to do that."
        404 -> "Resource not found."
        422 -> "Validation failed. Please check your input."
        500 -> "Server error. Please try again later."
        else -> "Something went wrong (Error $code)."
    }
}
