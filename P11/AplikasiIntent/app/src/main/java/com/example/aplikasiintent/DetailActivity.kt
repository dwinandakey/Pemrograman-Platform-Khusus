class package com.example.aplikasiintent

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var nameText: TextView
    private lateinit var surnameText: TextView
    private lateinit var ageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize views
        nameText = findViewById(R.id.nameText)
        surnameText = findViewById(R.id.surnameText)
        ageText = findViewById(R.id.ageText)

        // Get data from intent
        val name = intent.getStringExtra("NAME")
        val surname = intent.getStringExtra("SURNAME")
        val age = intent.getStringExtra("AGE")

        // Display data
        nameText.text = "Name: $name"
        surnameText.text = "Surname: $surname"
        ageText.text = "Age: $age"
    }
}