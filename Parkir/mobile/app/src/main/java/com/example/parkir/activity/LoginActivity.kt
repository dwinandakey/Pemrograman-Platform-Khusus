package com.example.parkir.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.parkir.databinding.ActivityLoginBinding
import com.example.parkir.model.AuthRequest
import com.example.parkir.network.ApiClient
import com.example.parkir.utils.JwtUtils
import com.example.parkir.utils.SessionManager
import kotlinx.coroutines.launch
import com.example.parkir.R
import kotlinx.coroutines.delay

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            redirectBasedOnRole(sessionManager.getUserRole())
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()

        setupClickListeners()
    }

    private fun setupAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Animasi untuk logo
        binding.ivLogo.apply {
            visibility = View.VISIBLE
            startAnimation(fadeIn)
        }

        // Animasi untuk title
        binding.tvTitle.apply {
            alpha = 0f
            postDelayed({
                alpha = 1f
                startAnimation(fadeIn)
            }, 200) // Delay sedikit setelah logo
        }

        binding.etEmail.apply {
            alpha = 0f
            postDelayed({
                alpha = 1f
                startAnimation(slideUp)
            }, 300)
        }

        binding.etPassword.apply {
            alpha = 0f
            postDelayed({
                alpha = 1f
                startAnimation(slideUp)
            }, 500)
        }

        binding.btnLogin.apply {
            alpha = 0f
            postDelayed({
                alpha = 1f
                startAnimation(slideUp)
            }, 700)
        }

        binding.tvRegister.apply {
            alpha = 0f
            postDelayed({
                alpha = 1f
                startAnimation(fadeIn)
            }, 900)
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            // Add fade out animation before starting new activity
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            binding.root.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            // Shake animation for error
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            if (email.isEmpty()) binding.etEmail.startAnimation(shake)
            if (password.isEmpty()) binding.etPassword.startAnimation(shake)

            showError("Email dan password harus diisi")
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        // Show loading state
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(AuthRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val role = JwtUtils.getRoleFromToken(authResponse.accessToken)

                    // Save session data
                    sessionManager.saveAuthToken(authResponse.accessToken)
                    sessionManager.saveUserSession(email, role ?: "ROLE_UMUM")

                    // Add slight delay for loading indicator
                    delay(500)

                    // Success animation before redirect
                    val fadeOut = AnimationUtils.loadAnimation(this@LoginActivity, R.anim.fade_out)
                    binding.root.startAnimation(fadeOut)
                    fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            redirectBasedOnRole(role)
                            finish()
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }
                        override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                    })
                } else {
                    showError("Email atau password salah")
                }
            } catch (e: Exception) {
                showError("Terjadi kesalahan: ${e.message}")
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            progressBar.isVisible = isLoading
            btnLogin.isEnabled = !isLoading
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            tvRegister.isEnabled = !isLoading
        }
    }

    private fun showError(message: String) {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.btnLogin.startAnimation(shake)

        // Create custom toast
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text = message

        Toast(applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    private fun redirectBasedOnRole(role: String?) {
        val isAdmin = role?.contains("ROLE_ADMIN", ignoreCase = true) == true
        val intent = if (isAdmin) {
            Intent(this@LoginActivity, MainAdminActivity::class.java)
        } else {
            Intent(this@LoginActivity, MainActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}