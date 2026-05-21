package com.dentalgo.app.features.appointment.data

data class BookAppointmentRequest(
    val email: String,
    val serviceType: String,
    val date: String,
    val notes: String
)

data class BookAppointmentResponse(
    val id: String?,
    val email: String?,
    val serviceType: String?,
    val date: String?,
    val notes: String?,
    val status: String?
)

/** Represents one appointment returned by the server for a given user. */
data class UserAppointmentItem(
    val id: String?,
    val email: String?,
    val serviceType: String?,
    val date: String?,
    val notes: String?,
    val status: String?   // e.g. "pending", "completed", "cancelled"
)
