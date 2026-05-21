package com.dentalgo.app.features.appointment.data

import com.dentalgo.app.core.network.ApiResult
import com.dentalgo.app.core.network.BaseRepository

class AppointmentRepository(private val api: AppointmentApi) : BaseRepository() {

    suspend fun bookAppointment(request: BookAppointmentRequest): ApiResult<BookAppointmentResponse> =
        safeCall { api.bookAppointment(request) }

    suspend fun getUserAppointments(email: String): ApiResult<List<UserAppointmentItem>> =
        safeCall { api.getUserAppointments(email) }
}

