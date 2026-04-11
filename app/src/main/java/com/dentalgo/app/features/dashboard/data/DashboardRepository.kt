package com.dentalgo.app.features.dashboard.data

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class DashboardRepository(private val api: DashboardApi) : BaseRepository() {
    suspend fun getDashboard(token: String): ApiResult<DashboardResponse> =
        safeCall { api.getDashboard("Bearer $token") }
}
