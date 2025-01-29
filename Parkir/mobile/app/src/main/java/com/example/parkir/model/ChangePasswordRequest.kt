package com.example.parkir.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)