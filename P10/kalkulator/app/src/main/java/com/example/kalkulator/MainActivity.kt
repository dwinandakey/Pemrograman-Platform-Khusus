package com.example.kalkulator

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.ComponentActivity
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private lateinit var calculateButton: Button
    private lateinit var sideInput: TextInputEditText
    private lateinit var areaInput: TextInputEditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi elemen UI
        calculateButton = findViewById(R.id.calculateButton)
        sideInput = findViewById(R.id.inputText)
        areaInput = findViewById(R.id.outputText)

        // Set onClickListener untuk tombol hitung
        calculateButton.setOnClickListener { calculate() }
    }

    private fun calculate() {
        val sideText = sideInput.text.toString()
        val areaText = areaInput.text.toString()

        // Hitung luas jika panjang sisi diisi
        if (sideText.isNotEmpty() && areaText.isEmpty()) {
            val side = sideText.toDoubleOrNull()
            if (side == null || side <= 0) {
                sideInput.error = "Masukkan panjang sisi yang valid"
                return
            }
            val area = side * side
            areaInput.setText(area.toString())
            Toast.makeText(this, "Luas persegi dihitung!", Toast.LENGTH_SHORT).show()
        }
        // Hitung panjang sisi jika luas diisi
        else if (areaText.isNotEmpty() && sideText.isEmpty()) {
            val area = areaText.toDoubleOrNull()
            if (area == null || area <= 0) {
                areaInput.error = "Masukkan luas persegi yang valid"
                return
            }
            val side = sqrt(area)
            sideInput.setText(side.toString())
            Toast.makeText(this, "Panjang sisi dihitung!", Toast.LENGTH_SHORT).show()
        }
        // Validasi jika kedua form diisi atau kosong
        else if (sideText.isNotEmpty() && areaText.isNotEmpty()) {
            Toast.makeText(this, "Isi hanya salah satu form!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Isi salah satu form untuk menghitung!", Toast.LENGTH_SHORT).show()
        }
    }
}
