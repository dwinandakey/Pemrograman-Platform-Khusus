package com.example.sqlitepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewMahasiswa : AppCompatActivity() {
    private lateinit var mahasiswaModalArrayList: ArrayList<MahasiswaModal>
    private lateinit var dbHandler: DBHandler
    private lateinit var mahasiswaRVAdapter: MahasiswaRVAdapter
    private lateinit var mahasiswaRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_mahasiswa)

        mahasiswaModalArrayList = ArrayList()
        dbHandler = DBHandler(this)

        // Read data from database
        mahasiswaModalArrayList = dbHandler.readMahasiswa()

        // Initialize adapter
        mahasiswaRVAdapter = MahasiswaRVAdapter(
            mahasiswaModalArrayList,
            this
        )

        // Set up RecyclerView
        mahasiswaRV = findViewById(R.id.idRVMahasiswa)
        val linearLayoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )

        mahasiswaRV.apply {
            layoutManager = linearLayoutManager
            adapter = mahasiswaRVAdapter
        }
    }
}