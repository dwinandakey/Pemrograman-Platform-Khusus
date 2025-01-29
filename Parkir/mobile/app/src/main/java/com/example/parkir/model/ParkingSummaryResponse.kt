package com.example.parkir.model

data class ParkingSummaryResponse(
    val totalLokasi: Int,
    val totalKapasitas: Int,
    val totalTerisi: Int,
    val totalTersedia: Int
)