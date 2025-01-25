// ApiService.kt
package com.example.parkir.network

import com.example.parkir.model.AuthRequest
import com.example.parkir.model.AuthResponse
import com.example.parkir.model.UserDto
import retrofit2.Response  // Change this import
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body user: UserDto): Response<UserDto>
}