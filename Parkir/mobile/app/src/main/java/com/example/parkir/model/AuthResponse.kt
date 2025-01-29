// AuthResponse.kt
package com.example.parkir.model

data class AuthResponse(
    val email: String,
    val accessToken: String,
    val authorities: String? = null
)