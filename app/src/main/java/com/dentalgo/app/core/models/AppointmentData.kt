package com.dentalgo.app.core.models

data class AppointmentData(
    val id: Int,
    val service: String,
    val date: String,
    val time: String,
    val status: String,
    val dentist: String?
)
