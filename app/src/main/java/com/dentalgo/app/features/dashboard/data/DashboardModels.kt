package com.dentalgo.app.features.dashboard.data

import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.core.models.AppointmentData

data class DashboardUserResponse(
    val id: String?,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val profileImage: String?
)

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?,
    val appointments: List<AppointmentData>?
)

