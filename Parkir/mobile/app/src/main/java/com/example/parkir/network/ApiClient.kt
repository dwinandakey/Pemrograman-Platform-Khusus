package com.example.parkir.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ApiClient.kt
object ApiClient {
    private const val BASE_URL = "http://192.168.235.51:8080/";

    // localhost untuk Android emulator
    // private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}