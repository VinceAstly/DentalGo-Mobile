package com.dentalgo.app.features.dashboard.data

import com.dentalgo.app.core.models.AppointmentData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {
    @GET("api/users/profile")
    suspend fun getProfile(
        @Query("email") email: String
    ): Response<DashboardUserResponse>

    @GET("api/appointments")
    suspend fun getAppointments(
        @Query("email") email: String
    ): Response<List<AppointmentData>>
}

