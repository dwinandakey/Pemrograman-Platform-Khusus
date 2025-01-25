package com.example.parkir.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.parkir.databinding.ActivityLoginBinding
import com.example.parkir.model.AuthRequest
import com.example.parkir.model.AuthResponse
import com.example.parkir.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginUser(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Response perlu dibungkus dengan Response<T>
                val response: Response<AuthResponse> = ApiClient.apiService.login(AuthRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()
                    // Save token
                    saveAuthToken(authResponse?.accessToken ?: "")
                    // Navigate to main activity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAuthToken(token: String) {
        getSharedPreferences("prefs", MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .apply()
    }
}