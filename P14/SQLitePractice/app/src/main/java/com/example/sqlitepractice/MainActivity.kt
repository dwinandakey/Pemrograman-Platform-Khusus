package com.example.sqlitepractice

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var nim: EditText
    private lateinit var nama: EditText
    private lateinit var kelas: EditText
    private lateinit var nohp: EditText
    private lateinit var addMhs: Button
    private lateinit var lihatMhs: Button
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        nim = findViewById(R.id.nimEditText)
        nama = findViewById(R.id.namaEditText)
        kelas = findViewById(R.id.kelasEditText)
        nohp = findViewById(R.id.nohpEditText)
        addMhs = findViewById(R.id.tambahButton)
        lihatMhs = findViewById(R.id.lihatButton)

        // Initialize database handler
        dbHandler = DBHandler(this)

        // Add button click listener
        addMhs.setOnClickListener {
            val nimmhs = nim.text.toString()
            val namamhs = nama.text.toString()
            val kelasmhs = kelas.text.toString()
            val nohpmhs = nohp.text.toString()

            if (nimmhs.isEmpty() && namamhs.isEmpty() && kelasmhs.isEmpty() && nohpmhs.isEmpty()) {
                Toast.makeText(this, "Lengkapilah semua data..", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add new student data
            dbHandler.addNewMahasiswa(nimmhs, namamhs, kelasmhs, nohpmhs)
            Toast.makeText(this, "Data Mahasiswa Telah Ditambahkan", Toast.LENGTH_SHORT).show()

            // Clear input fields
            nim.setText("")
            nama.setText("")
            kelas.setText("")
            nohp.setText("")
        }

        // View button click listener
        lihatMhs.setOnClickListener {
            // Opening a new activity via intent
            val intent = Intent(this, ViewMahasiswa::class.java)
            startActivity(intent)
        }
    }
}