package com.dentalgo.app.core.models

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val bio: String?,
    val avatar: String?
)
