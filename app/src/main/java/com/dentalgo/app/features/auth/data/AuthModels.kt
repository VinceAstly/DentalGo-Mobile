package com.dentalgo.app.features.auth.data

import com.dentalgo.app.core.models.UserData

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val phone: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserData?
)
