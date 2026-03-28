package com.dentalgo.app.data.api

import com.dentalgo.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /** Register a new user */
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    /** Login with email + password */
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /** Get authenticated user's dashboard data */
    @GET("api/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    /** Get authenticated user's profile */
    @GET("api/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    /** Update authenticated user's profile */
    @PUT("api/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    /** Change the authenticated user's password */
    @PUT("api/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}
