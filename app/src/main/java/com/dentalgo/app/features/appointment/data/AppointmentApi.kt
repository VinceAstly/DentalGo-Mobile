package com.dentalgo.app.features.appointment.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppointmentApi {

    @POST("api/appointments")
    suspend fun bookAppointment(
        @Body request: BookAppointmentRequest
    ): Response<BookAppointmentResponse>

    /** Fetch all appointments belonging to the given user (by email/token). */
    @GET("api/appointments")
    suspend fun getUserAppointments(
        @Query("email") email: String
    ): Response<List<UserAppointmentItem>>
}
