package com.dentalgo.app.data.model

/* ─────────────── AUTH ─────────────── */

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

/* ─────────────── USER / PROFILE ─────────────── */

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val bio: String?,
    val avatar: String?
)

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

/* ─────────────── CHANGE PASSWORD ─────────────── */

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String
)

/* ─────────────── DASHBOARD ─────────────── */

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?,
    val appointments: List<AppointmentData>?
)

data class AppointmentData(
    val id: Int,
    val service: String,
    val date: String,
    val time: String,
    val status: String,
    val dentist: String?
)

/* ─────────────── GENERIC ─────────────── */

data class ApiError(
    val success: Boolean,
    val message: String,
    val errors: Map<String, List<String>>?
)
