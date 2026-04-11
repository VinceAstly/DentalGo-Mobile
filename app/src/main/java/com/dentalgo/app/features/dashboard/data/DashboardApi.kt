package com.dentalgo.app.features.dashboard.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardApi {
    @GET("api/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>
}
