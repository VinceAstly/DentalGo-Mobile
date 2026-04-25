package com.dentalgo.app.features.profile.data

import com.dentalgo.app.core.models.UserData

data class ProfileResponse(
    val id: String?,   // MongoDB ObjectId
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val profileImage: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val phone: String,
    val bio: String
)

data class ChangePasswordRequest(
    val password: String
)

data class UpdateProfileResponse(val success: Boolean, val message: String, val user: UserData?)
data class ChangePasswordResponse(val success: Boolean, val message: String)

