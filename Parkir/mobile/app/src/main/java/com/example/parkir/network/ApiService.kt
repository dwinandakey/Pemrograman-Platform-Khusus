// ApiService.kt
package com.example.parkir.network

import com.example.parkir.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body user: UserDto): Response<UserDto>

    @PUT("change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    @GET("search/parkir/{keyword}")
    suspend fun searchLokasiParkir(
        @Header("Authorization") token: String,
        @Path("keyword") keyword: String
    ): Response<List<LokasiParkirResponse>>

    @POST("parkir/masuk")
    suspend fun parkirMasuk(
        @Header("Authorization") token: String,
        @Body request: KendaraanMasukRequest
    ): Response<TransaksiParkirResponse>

    @POST("parkir/keluar")
    suspend fun parkirKeluar(
        @Header("Authorization") token: String
    ): Response<TransaksiParkirResponse>

    @GET("parkir/lokasi")
    suspend fun getLokasiParkir(
        @Header("Authorization") token: String
    ): Response<List<LokasiParkirResponse>>

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserDto>

    @PATCH("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ): Response<UserDto>

    @GET("search/parkir/summary")
    suspend fun getParkingSummary(
        @Header("Authorization") token: String
    ): Response<ParkingSummaryResponse>
}