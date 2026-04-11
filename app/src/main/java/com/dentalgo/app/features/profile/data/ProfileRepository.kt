package com.dentalgo.app.features.profile.data

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class ProfileRepository(private val api: ProfileApi) : BaseRepository() {
    suspend fun getProfile(token: String): ApiResult<ProfileResponse> =
        safeCall { api.getProfile("Bearer $token") }

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): ApiResult<UpdateProfileResponse> =
        safeCall { api.updateProfile("Bearer $token", request) }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): ApiResult<ChangePasswordResponse> =
        safeCall { api.changePassword("Bearer $token", request) }
}
