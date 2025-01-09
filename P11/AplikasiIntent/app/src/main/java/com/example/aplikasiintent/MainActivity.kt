package com.example.aplikasiintent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        ageInput = findViewById(R.id.ageInput)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("NAME", nameInput.text.toString())
                putExtra("SURNAME", surnameInput.text.toString())
                putExtra("AGE", ageInput.text.toString())
            }
            startActivity(intent)
        }
    }
}