package com.dentalgo.app.features.appointment.data

import com.google.gson.annotations.SerializedName

data class BookAppointmentRequest(
    @SerializedName("patientEmail") val email: String,
    @SerializedName("serviceName") val serviceType: String,
    @SerializedName("appointmentDate") val date: String,
    @SerializedName("startTime") val time: String = "",
    val notes: String
)

data class BookAppointmentResponse(
    val id: String?,
    @SerializedName("patientEmail") val email: String?,
    @SerializedName("serviceName") val serviceType: String?,
    @SerializedName("appointmentDate") val date: String?,
    @SerializedName("startTime") val time: String?,
    val notes: String?,
    val status: String?
)

/** Represents one appointment returned by the server for a given user. */
data class UserAppointmentItem(
    val id: String?,
    @SerializedName("patientEmail") val email: String?,
    @SerializedName("serviceName") val serviceType: String?,
    @SerializedName("appointmentDate") val date: String?,
    @SerializedName("startTime") val time: String?,
    val notes: String?,
    val status: String?   // e.g. "pending", "completed", "cancelled"
)
