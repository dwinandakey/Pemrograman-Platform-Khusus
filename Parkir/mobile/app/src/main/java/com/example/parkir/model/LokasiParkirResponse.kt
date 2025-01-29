package com.example.parkir.model

data class LokasiParkirResponse(
    val id: Long,
    val namaLokasi: String,
    val kapasitas: Int,
    val terisi: Int,
    val status: Boolean
)