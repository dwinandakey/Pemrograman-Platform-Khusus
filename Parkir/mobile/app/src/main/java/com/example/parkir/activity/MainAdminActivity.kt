package com.example.parkir.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.parkir.R
import com.example.parkir.utils.SessionManager

class MainAdminActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Verify admin role
        if (!verifyAdminRole()) {
            redirectToLogin()
            return
        }

        // Setup the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""

        setupCardClickListeners()
    }

    private fun verifyAdminRole(): Boolean {
        val role = sessionManager.getUserRole()
        return role?.contains("ROLE_ADMIN", ignoreCase = true) == true
    }

    private fun redirectToLogin() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_profile -> {
                // Handle profile action
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Clear session data
        sessionManager.clearSession()

        // Redirect to login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCardClickListeners() {
        // Manage Users
        findViewById<CardView>(R.id.cardManageUser).setOnClickListener {
            Toast.makeText(this, "Kelola User clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to user management screen
        }

        // Manage Locations
        findViewById<CardView>(R.id.cardManageLocation).setOnClickListener {
            Toast.makeText(this, "Kelola Lokasi clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to location management screen
        }

        // Manage Vehicles
        findViewById<CardView>(R.id.cardManageVehicle).setOnClickListener {
            Toast.makeText(this, "Kelola Kendaraan clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to vehicle management screen
        }

        // Manage Transactions
        findViewById<CardView>(R.id.cardManageTransaction).setOnClickListener {
            Toast.makeText(this, "Kelola Transaksi clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to transaction management screen
        }

        // Reports
        findViewById<CardView>(R.id.cardReports).setOnClickListener {
            Toast.makeText(this, "Laporan clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to reports screen
        }

        // Settings
        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            Toast.makeText(this, "Pengaturan clicked", Toast.LENGTH_SHORT).show()
            // Add navigation to settings screen
        }
    }
}