package com.dentalgo.app.core.models

import com.google.gson.annotations.SerializedName

data class AppointmentData(
    val id: String?,
    val patientEmail: String?,
    @SerializedName("serviceName") val serviceType: String?,
    val appointmentDate: String?,
    val status: String?,
    val createdAt: String?
)

