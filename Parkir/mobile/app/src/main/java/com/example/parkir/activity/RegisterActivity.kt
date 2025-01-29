package com.example.parkir.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.parkir.R
import com.example.parkir.databinding.ActivityRegisterBinding
import com.example.parkir.model.Role
import com.example.parkir.model.UserDto
import com.example.parkir.network.ApiClient
import kotlinx.coroutines.launch
import android.util.Patterns
import android.widget.ArrayAdapter

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSpinner()

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString()
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val role = binding.spinnerRole.selectedItem.toString()

                registerUser(name, email, password, role)
            }
        }
    }

    private fun setupRoleSpinner() {
        val roles = Role.values()
            .filter { it != Role.ADMIN }
            .map { it.name }
            .toTypedArray()

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            roles
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter
    }

    private fun validateInputs(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        when {
            name.isEmpty() -> {
                showError("Nama harus diisi")
                return false
            }
            email.isEmpty() -> {
                showError("Email harus diisi")
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Format email tidak valid")
                return false
            }
            password.isEmpty() -> {
                showError("Password harus diisi")
                return false
            }
            confirmPassword.isEmpty() -> {
                showError("Konfirmasi password harus diisi")
                return false
            }
            password != confirmPassword -> {
                showError("Password tidak sesuai")
                return false
            }
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String, roleString: String) {
        lifecycleScope.launch {
            try {
                val role = try {
                    Role.valueOf(roleString)
                } catch (e: IllegalArgumentException) {
                    Role.UMUM
                }

                val userDto = UserDto(null, name, email, password, role)
                val response = ApiClient.apiService.register(userDto)

                if (response.isSuccessful) {
                    showError("Registrasi berhasil!")
                    finish()
                } else {
                    showError("Registrasi gagal. Coba lagi.")
                }
            } catch (e: Exception) {
                showError(e.message ?: "An error occurred")
            }
        }
    }

    private fun showError(message: String) {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.btnRegister.startAnimation(shake)

        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text = message

        Toast(applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}