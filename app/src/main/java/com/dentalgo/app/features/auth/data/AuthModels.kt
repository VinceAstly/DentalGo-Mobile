package com.dentalgo.app.features.auth.data

import com.dentalgo.app.core.models.UserData

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String
)

data class RegisterResponse(
    val id: String?,
    val fullName: String?,
    val email: String?,
    val phone: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id: String?,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val profileImage: String?
)

