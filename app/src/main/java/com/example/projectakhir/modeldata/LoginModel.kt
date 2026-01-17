package com.example.projectakhir.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null
)

@Serializable
data class UserData(
    val user_id: Int,
    val username: String,
    @SerialName("user_role")
    val role: String
)

@Serializable
data class UserResponse(
    val success: Boolean,
    val data: List<UserData> // Ini yang akan menampung array user
)