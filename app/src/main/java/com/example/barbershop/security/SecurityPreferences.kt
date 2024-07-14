package com.example.barbershop.security

import android.content.Context

class SecurityPreferences(private val context: Context) {

    private fun getSharedPreferences() = context.getSharedPreferences("barbershop", Context.MODE_PRIVATE)

    fun storeString(key: String, value: String) {
        val editor = getSharedPreferences().edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return getSharedPreferences().getString(key, null)
    }
}