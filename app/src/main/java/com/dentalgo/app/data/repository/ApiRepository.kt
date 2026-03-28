package com.dentalgo.app.data.repository

import com.dentalgo.app.data.api.ApiService
import com.dentalgo.app.data.model.*
import com.google.gson.Gson
import retrofit2.Response

/** Sealed wrapper for API results */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = -1) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

/**
 * Repository layer — handles all API call logic, error parsing,
 * and returns clean ApiResult objects to ViewModels.
 */
class ApiRepository(private val api: ApiService) {

    /* ───────── REGISTER ───────── */
    suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse> =
        safeCall { api.register(request) }

    /* ───────── LOGIN ───────── */
    suspend fun login(request: LoginRequest): ApiResult<LoginResponse> =
        safeCall { api.login(request) }

    /* ───────── DASHBOARD ───────── */
    suspend fun getDashboard(token: String): ApiResult<DashboardResponse> =
        safeCall { api.getDashboard("Bearer $token") }

    /* ───────── PROFILE ───────── */
    suspend fun getProfile(token: String): ApiResult<ProfileResponse> =
        safeCall { api.getProfile("Bearer $token") }

    /* ───────── UPDATE PROFILE ───────── */
    suspend fun updateProfile(token: String, request: UpdateProfileRequest): ApiResult<UpdateProfileResponse> =
        safeCall { api.updateProfile("Bearer $token", request) }

    /* ───────── CHANGE PASSWORD ───────── */
    suspend fun changePassword(token: String, request: ChangePasswordRequest): ApiResult<ChangePasswordResponse> =
        safeCall { api.changePassword("Bearer $token", request) }

    /* ───────── Generic safe-call helper ───────── */
    private suspend fun <T> safeCall(call: suspend () -> Response<T>): ApiResult<T> {
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
