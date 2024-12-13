package com.example.perpustakaan
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val myActivityTag = "lifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(myActivityTag, "onCreate State")
    }

    override fun onStart() {
        super.onStart()
        Log.i(myActivityTag, "onStart State")
    }

    override fun onResume() {
        super.onResume()
        Log.i(myActivityTag, "onResume State")
    }

    override fun onPause() {
        super.onPause()
        Log.i(myActivityTag, "onPause State")
    }

    override fun onStop() {
        super.onStop()
        Log.i(myActivityTag, "onStop State")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(myActivityTag, "onDestroy State")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(myActivityTag, "onRestart State")
    }
}