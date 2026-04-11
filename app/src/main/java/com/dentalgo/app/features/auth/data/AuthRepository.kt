package com.dentalgo.app.features.auth.data

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class AuthRepository(private val authApi: AuthApi) : BaseRepository() {
    suspend fun login(request: LoginRequest): ApiResult<LoginResponse> =
        safeCall { authApi.login(request) }

    suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse> =
        safeCall { authApi.register(request) }
}
