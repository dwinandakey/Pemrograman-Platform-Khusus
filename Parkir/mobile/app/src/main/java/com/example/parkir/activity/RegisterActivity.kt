package com.example.parkir.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.parkir.databinding.ActivityRegisterBinding
import com.example.parkir.model.UserDto
import com.example.parkir.network.ApiClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSpinner()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val role = binding.spinnerRole.selectedItem.toString()

            registerUser(name, email, password, role)
        }
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("UMUM", "DOSEN", "MAHASISWA", "KARYAWAN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter
    }

    private fun registerUser(name: String, email: String, password: String, role: String) {
        lifecycleScope.launch {
            try {
                val userDto = UserDto(null, name, email, password, role)
                val response = ApiClient.apiService.register(userDto)

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish() // Return to login
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}