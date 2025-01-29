package com.example.parkir.model

// UserDto.kt
data class UserDto(
    val id: Long?,
    val name: String,
    val email: String,
    val password: String,
    val role: Role,
)