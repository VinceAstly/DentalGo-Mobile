package com.dentalgo.app.core.network

data class ApiError(
    val success: Boolean,
    val message: String,
    val errors: Map<String, List<String>>?
)
