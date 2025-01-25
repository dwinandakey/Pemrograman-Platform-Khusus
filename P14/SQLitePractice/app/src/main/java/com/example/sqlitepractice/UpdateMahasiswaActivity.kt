package com.example.sqlitepractice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpdateMahasiswaActivity : AppCompatActivity() {
    private lateinit var nimEdt: EditText
    private lateinit var namaEdt: EditText
    private lateinit var kelasEdt: EditText
    private lateinit var nohpEdt: EditText
    private lateinit var updateMahasiswaBtn: Button
    private lateinit var deleteMahasiswaBtn: Button
    private lateinit var dbHandler: DBHandler
    private lateinit var nim: String
    private lateinit var nama: String
    private lateinit var kelas: String
    private lateinit var nohp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_mahasiswa)

        // Initialize views
        nimEdt = findViewById(R.id.nimEdtEditText)
        namaEdt = findViewById(R.id.namaEdtEditText)
        kelasEdt = findViewById(R.id.kelasEdtEditText)
        nohpEdt = findViewById(R.id.nohpEdtEditText)
        updateMahasiswaBtn = findViewById(R.id.updateButton)
        deleteMahasiswaBtn = findViewById(R.id.deleteButton)

        // Initialize database handler
        dbHandler = DBHandler(this)

        // Get intent extras
        intent.let {
            nim = it.getStringExtra("nim") ?: ""
            nama = it.getStringExtra("nama") ?: ""
            kelas = it.getStringExtra("kelas") ?: ""
            nohp = it.getStringExtra("nohp") ?: ""
        }

        // Set existing values to EditText fields
        nimEdt.setText(nim)
        namaEdt.setText(nama)
        kelasEdt.setText(kelas)
        nohpEdt.setText(nohp)

        // Update button click listener
        updateMahasiswaBtn.setOnClickListener {
            dbHandler.updateMahasiswa(
                nim,
                nimEdt.text.toString(),
                namaEdt.text.toString(),
                kelasEdt.text.toString(),
                nohpEdt.text.toString()
            )
            Toast.makeText(this, "Mahasiswa telah di-Update..", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Delete button click listener
        deleteMahasiswaBtn.setOnClickListener {
            dbHandler.deleteMahasiswa(nim)
            Toast.makeText(this, "Mahasiswa telah di-Delete..", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}