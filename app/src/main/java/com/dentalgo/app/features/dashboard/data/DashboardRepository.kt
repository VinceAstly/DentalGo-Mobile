package com.dentalgo.app.features.dashboard.data

import com.dentalgo.app.core.models.AppointmentData
import com.dentalgo.app.core.models.UserData
import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class DashboardRepository(private val api: DashboardApi) : BaseRepository() {

    suspend fun getDashboard(email: String): ApiResult<DashboardResponse> {
        val profileResult = safeCall { api.getProfile(email) }
        val userData: UserData? = when (profileResult) {
            is ApiResult.Success -> profileResult.data.let { u ->
                UserData(
                    id    = u.id,
                    name  = u.fullName ?: "",
                    email = u.email ?: email,
                    phone = u.phone,
                    bio   = u.bio,
                    avatar = null
                )
            }
            else -> null
        }

        val appointmentsResult = safeCall { api.getAppointments(email) }
        val appointments: List<AppointmentData> = when (appointmentsResult) {
            is ApiResult.Success -> appointmentsResult.data
            else -> emptyList()
        }

        return ApiResult.Success(
            DashboardResponse(
                success      = true,
                message      = "OK",
                user         = userData,
                appointments = appointments
            )
        )
    }
}

