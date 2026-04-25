package com.dentalgo.app.features.profile.data

import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class ProfileRepository(private val api: ProfileApi) : BaseRepository() {

    suspend fun getProfile(email: String): ApiResult<ProfileResponse> =
        safeCall { api.getProfile(email) }

    suspend fun updateProfile(email: String, request: UpdateProfileRequest): ApiResult<String> =
        safeCall { api.updateProfile(email, request) }

    suspend fun changePassword(email: String, request: ChangePasswordRequest): ApiResult<String> =
        safeCall { api.changePassword(email, request) }
}

