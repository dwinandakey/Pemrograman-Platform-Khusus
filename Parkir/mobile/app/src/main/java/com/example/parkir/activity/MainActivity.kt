package com.example.parkir.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.Toolbar
import com.example.parkir.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Layanan Manajemen Parkir"

        // Setup click listeners for cards
        setupCardClickListeners()
    }

    private fun setupCardClickListeners() {
        // Parkir Masuk
        findViewById<CardView>(R.id.cardParkirMasuk).setOnClickListener {
            Toast.makeText(this, "Parkir Masuk clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }

        // Parkir Keluar
        findViewById<CardView>(R.id.cardParkirKeluar).setOnClickListener {
            Toast.makeText(this, "Parkir Keluar clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }

        // Lokasi Parkir
        findViewById<CardView>(R.id.cardLokasiParkir).setOnClickListener {
            Toast.makeText(this, "Lokasi Parkir clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }

        // History
        findViewById<CardView>(R.id.cardHistory).setOnClickListener {
            Toast.makeText(this, "Riwayat clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }

        // Profile
        findViewById<CardView>(R.id.cardProfile).setOnClickListener {
            Toast.makeText(this, "Profil clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }

        // Information
        findViewById<CardView>(R.id.cardInfo).setOnClickListener {
            Toast.makeText(this, "Informasi clicked", Toast.LENGTH_SHORT).show()
            // Add your navigation logic here
        }
    }
}