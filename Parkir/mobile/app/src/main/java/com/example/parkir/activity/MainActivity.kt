package com.example.parkir.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.parkir.R
import com.example.parkir.model.*
import com.example.parkir.network.ApiClient
import com.example.parkir.utils.SessionManager
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private var lokasiParkirList: List<LokasiParkirResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
        setupCardClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_profile -> {
                showProfileDialog()
                true
            }
            R.id.action_change_password -> {
                showChangePasswordDialog()
                true
            }
            R.id.action_search -> {
                showSearchLokasiDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupCardClickListeners() {
        // Parkir Masuk
        findViewById<CardView>(R.id.cardParkirMasuk).setOnClickListener {
            if (sessionManager.isCurrentlyParked()) {
                AlertDialog.Builder(this)
                    .setTitle("Peringatan")
                    .setMessage("Anda sudah memiliki kendaraan yang diparkir. Tidak dapat melakukan parkir masuk lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                showParkirMasukDialog()
            }
        }

        // Parkir Keluar
        findViewById<CardView>(R.id.cardParkirKeluar).setOnClickListener {
            if (!sessionManager.isCurrentlyParked()) {
                AlertDialog.Builder(this)
                    .setTitle("Peringatan")
                    .setMessage("Anda belum memiliki kendaraan yang diparkir. Tidak dapat melakukan parkir keluar.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                processParkirKeluar()
            }
        }

        // Lokasi Parkir
        findViewById<CardView>(R.id.cardLokasiParkir).setOnClickListener {
            fetchLokasiParkir()
        }

        // Profile
        findViewById<CardView>(R.id.cardProfile).setOnClickListener {
            showProfileDialog()
        }

        // Information
        findViewById<CardView>(R.id.cardInfo).setOnClickListener {
            showParkingInfo()
        }
    }

    private fun showParkirMasukDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_parkir_masuk, null)
        val etPlat = dialogView.findViewById<EditText>(R.id.etNomorPlat)
        val spinnerJenisKendaraan = dialogView.findViewById<Spinner>(R.id.spinnerJenisKendaraan)
        val spinnerLokasiParkir = dialogView.findViewById<Spinner>(R.id.spinnerLokasiParkir)

        // Setup jenis kendaraan spinner
        val jenisKendaraanList = listOf("MOTOR", "MOBIL")
        val jenisKendaraanAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            jenisKendaraanList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerJenisKendaraan.adapter = jenisKendaraanAdapter

        // Fetch and setup lokasi parkir spinner
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getLokasiParkir("Bearer ${sessionManager.getAuthToken()}")
                if (response.isSuccessful && response.body() != null) {
                    lokasiParkirList = response.body()!!
                    val lokasiNames = lokasiParkirList.map { "${it.namaLokasi} (${it.terisi}/${it.kapasitas})" }

                    val lokasiAdapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_item,
                        lokasiNames
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinnerLokasiParkir.adapter = lokasiAdapter
                }
            } catch (e: Exception) {
                showMessage("Error loading lokasi: ${e.message}")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Parkir Masuk")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val selectedLokasi = lokasiParkirList[spinnerLokasiParkir.selectedItemPosition]
                if (selectedLokasi.terisi >= selectedLokasi.kapasitas) {
                    showMessage("Lokasi parkir penuh!")
                    return@setPositiveButton
                }

                val request = KendaraanMasukRequest(
                    nomorPlat = etPlat.text.toString(),
                    jenisKendaraan = jenisKendaraanList[spinnerJenisKendaraan.selectedItemPosition],
                    idLokasiParkir = selectedLokasi.id
                )
                processParkirMasuk(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun processParkirMasuk(request: KendaraanMasukRequest) {
        if (request.nomorPlat.isBlank()) {
            showMessage("Nomor plat tidak boleh kosong!")
            return
        }
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.parkirMasuk(
                    "Bearer ${sessionManager.getAuthToken()}",
                    request
                )
                if (response.isSuccessful) {
                    sessionManager.setParkedStatus(true, request.nomorPlat)
                    showMessage("Parkir masuk berhasil")
                } else {
                    showMessage("Gagal memproses parkir masuk")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun processParkirKeluar() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.parkirKeluar("Bearer ${sessionManager.getAuthToken()}")
                if (response.isSuccessful) {
                    val result = response.body()
                    sessionManager.setParkedStatus(false, null)
                    showMessage("Parkir keluar berhasil. Biaya: Rp${result?.biaya ?: 0}"
                    )
                } else {
                    showMessage("Gagal memproses parkir keluar")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun fetchLokasiParkir() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getLokasiParkir("Bearer ${sessionManager.getAuthToken()}")
                if (response.isSuccessful && response.body() != null) {
                    showLokasiParkirDialog(response.body()!!)
                } else {
                    showMessage("Gagal mengambil data lokasi parkir")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun showLokasiParkirDialog(lokasiList: List<LokasiParkirResponse>) {
        val message = buildString {
            lokasiList.forEach { lokasi ->
                appendLine("Lokasi: ${lokasi.namaLokasi}")
                appendLine("Kapasitas: ${lokasi.kapasitas}")
                appendLine("Terisi: ${lokasi.terisi}")
                appendLine("Status: ${if (lokasi.status) "Aktif" else "Nonaktif"}")
                appendLine()
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Lokasi Parkir")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showProfileDialog() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getProfile("Bearer ${sessionManager.getAuthToken()}")
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    showProfileEditDialog(profile)
                } else {
                    showMessage("Gagal mengambil data profil")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun showProfileEditDialog(profile: UserDto) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        etName.setText(profile.name)
        etEmail.setText(profile.email)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val request = ProfileUpdateRequest(
                    name = etName.text.toString(),
                    email = etEmail.text.toString()
                )
                updateProfile(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProfile(request: ProfileUpdateRequest) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateProfile(
                    "Bearer ${sessionManager.getAuthToken()}",
                    request
                )
                if (response.isSuccessful) {
                    showMessage("Profil berhasil diperbarui")
                } else {
                    showMessage("Gagal memperbarui profil")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun showParkingInfo() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getParkingSummary("Bearer ${sessionManager.getAuthToken()}")
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!
                    showParkingInfoDialog(summary)
                } else {
                    showMessage("Gagal mengambil informasi parkir")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun showParkingInfoDialog(summary: ParkingSummaryResponse) {
        val message = """
            Total Lokasi: ${summary.totalLokasi}
            Total Kapasitas: ${summary.totalKapasitas}
            Total Terisi: ${summary.totalTerisi}
            Total Tersedia: ${summary.totalTersedia}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Informasi Parkir")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val etOldPassword = dialogView.findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Ubah Kata Sandi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val oldPassword = etOldPassword.text.toString().trim()
                val newPassword = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (oldPassword.isEmpty()) {
                    etOldPassword.error = "Kata sandi lama tidak boleh kosong"
                    return@setPositiveButton
                }
                if (newPassword.isEmpty()) {
                    etNewPassword.error = "Kata sandi baru tidak boleh kosong"
                    return@setPositiveButton
                }
                if (newPassword != confirmPassword) {
                    etConfirmPassword.error = "Konfirmasi kata sandi tidak cocok"
                    return@setPositiveButton
                }

                try {
                    val request = ChangePasswordRequest(
                        currentPassword = oldPassword,
                        newPassword = newPassword
                    )
                    changePassword(request)
                    dialog.dismiss()
                } catch (e: Exception) {
                    showMessage("Error: ${e.message}")
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // MainActivity.kt - Updated changePassword method
    private fun changePassword(request: ChangePasswordRequest) {
        if (request.currentPassword.isEmpty() || request.newPassword.isEmpty()) {
            AlertDialog.Builder(this)
                .setMessage("Data kata sandi tidak valid")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.changePassword(
                    token = "Bearer ${sessionManager.getAuthToken()}",
                    request = request
                )

                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful -> {
                            try {
                                // Tangani response body dengan aman
                                val responseBody = response.body()
                                AlertDialog.Builder(this@MainActivity)
                                    .setMessage("Kata sandi berhasil diubah")
                                    .setPositiveButton("OK") { _, _ ->
                                        sessionManager.clearSession()
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .setCancelable(false)
                                    .show()
                            } catch (e: Exception) {
                                // Jika parsing response body gagal, tetap anggap sukses
                                AlertDialog.Builder(this@MainActivity)
                                    .setMessage("Kata sandi berhasil diubah")
                                    .setPositiveButton("OK") { _, _ ->
                                        sessionManager.clearSession()
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .setCancelable(false)
                                    .show()
                            }
                        }
                        response.code() == 400 -> {
                            AlertDialog.Builder(this@MainActivity)
                                .setMessage("Kata sandi tidak valid")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                        response.code() == 401 -> {
                            AlertDialog.Builder(this@MainActivity)
                                .setMessage("Sesi telah berakhir. Silakan login kembali")
                                .setPositiveButton("OK") { _, _ ->
                                    sessionManager.clearSession()
                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                    finish()
                                }
                                .setCancelable(false)
                                .show()
                        }
                        else -> {
                            AlertDialog.Builder(this@MainActivity)
                                .setMessage("Gagal mengubah kata sandi")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Error: ${e.message ?: "Terjadi kesalahan"}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }

    private fun showSearchLokasiDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_lokasi, null)
        val etKeyword = dialogView.findViewById<EditText>(R.id.etKeyword)

        AlertDialog.Builder(this)
            .setTitle("Cari Lokasi Parkir")
            .setView(dialogView)
            .setPositiveButton("Cari") { _, _ ->
                val keyword = etKeyword.text.toString()
                if (keyword.isNotBlank()) {
                    searchLokasiParkir(keyword)
                } else {
                    showMessage("Masukkan kata kunci pencarian!")
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun searchLokasiParkir(keyword: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.searchLokasiParkir(
                    "Bearer ${sessionManager.getAuthToken()}",
                    keyword
                )
                if (response.isSuccessful && response.body() != null) {
                    showLokasiParkirDialog(response.body()!!)
                } else {
                    showMessage("Gagal mencari lokasi parkir")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showMessage(message: String) {
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text = message

        Toast(applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}