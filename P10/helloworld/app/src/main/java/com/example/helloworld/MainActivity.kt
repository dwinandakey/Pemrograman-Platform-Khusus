package com.example.helloworld

import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var txt1: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var listener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi elemen UI
        txt1 = findViewById(R.id.txtView1)
        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)

        // Listener untuk btn2
        listener = View.OnClickListener {
            txt1.text = "Button 2 Clicked"
        }
        btn2.setOnClickListener(listener)

        // Set onClickListener untuk button
        btn1.setOnClickListener {
            txt1.text = "Button Clicked"
        }
    }
}
