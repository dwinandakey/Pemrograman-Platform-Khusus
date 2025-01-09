package com.example.intent

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)

        btn1.setOnClickListener { btnClick(it) }
        btn2.setOnClickListener { btn2Click(it) }
        btn3.setOnClickListener { btn3Click(it) }
    }

    private fun btnClick(view: View) {
        val tlp = Intent(Intent.ACTION_DIAL, Uri.parse("tel:082213104552"))
        startActivity(tlp)
    }

    private fun btn2Click(view: View) {
        val setting = Intent(android.provider.Settings.ACTION_SETTINGS)
        startActivity(setting)
        Toast.makeText(this, "You have pressed: ${btn2.text}", Toast.LENGTH_LONG).show()
    }

    private fun btn3Click(view: View) {
        // Tambahkan kode Anda di sini
        // Intent to perform a web search
//        val search = Intent(Intent.ACTION_WEB_SEARCH).apply {
//            putExtra(SearchManager.QUERY, "intent android")
//        }
//        // Start the activity
//        startActivity(search)
        val myIntent = Intent().apply {
            setType("audio/mp3")
            setAction(Intent.ACTION_GET_CONTENT)
        }
        startActivity(myIntent)
    }
}
