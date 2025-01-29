package com.example.parkir.utils

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun getRoleFromToken(token: String): String? {
        try {
            // Split token into 3 parts
            val parts = token.split(".")
            if (parts.size != 3) return null

            // Decode payload (second part)
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)

            // Get authorities from payload
            return jsonObject.optString("authorities")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}