package com.dentalgo.app.features.profile.data

import com.dentalgo.app.core.models.UserData

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val bio: String
)

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?
)

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String
)
