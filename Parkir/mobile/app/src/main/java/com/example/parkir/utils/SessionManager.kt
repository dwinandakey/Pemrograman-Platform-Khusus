package com.example.parkir.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "ParkingAppSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_TOKEN = "token"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val IS_PARKED = "is_parked"
        private const val PARKED_VEHICLE_NUMBER = "parked_vehicle_number"
    }

    fun saveAuthToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun saveUserSession(email: String, role: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setParkedStatus(isParked: Boolean, vehicleNumber: String?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_PARKED, isParked)
        editor.putString(PARKED_VEHICLE_NUMBER, vehicleNumber)
        editor.apply()
    }

    fun isCurrentlyParked(): Boolean {
        return sharedPreferences.getBoolean(IS_PARKED, false)
    }

    fun getParkedVehicleNumber(): String? {
        return sharedPreferences.getString(PARKED_VEHICLE_NUMBER, null)
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}