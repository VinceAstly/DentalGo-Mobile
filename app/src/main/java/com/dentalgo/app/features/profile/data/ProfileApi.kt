package com.dentalgo.app.features.profile.data

import com.dentalgo.app.core.models.AppointmentData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {
    @GET("api/users/profile")
    suspend fun getProfile(
        @Query("email") email: String
    ): Response<ProfileResponse>

    @PUT("api/users/profile/edit/{email}")
    suspend fun updateProfile(
        @Path("email") email: String,
        @Body request: UpdateProfileRequest
    ): Response<String>

    @PUT("api/users/change-password/{email}")
    suspend fun changePassword(
        @Path("email") email: String,
        @Body request: ChangePasswordRequest
    ): Response<String>

    @GET("api/appointments")
    suspend fun getAppointments(
        @Query("email") email: String
    ): Response<List<AppointmentData>>
}

