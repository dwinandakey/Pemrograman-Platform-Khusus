package com.example.parkir.model

data class KendaraanMasukRequest(
    val nomorPlat: String,
    val jenisKendaraan: String,
    val idLokasiParkir: Long
)