package com.example.portal.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.portal.R

object SessionManager {

    private const val USER_TOKEN = "accessToken"

    /**
     * Function to save auth token
     */
    fun saveAuthToken(context: Context, token: String) {
        saveString(context, USER_TOKEN, token)
    }

    /**
     * Function to fetch auth token
     */
    fun getToken(context: Context): String? {
        return getString(context, USER_TOKEN)
    }

    private fun saveString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()

    }

    private fun getString(context: Context, key: String): String? {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        return prefs.getString(key, null)
    }

    fun clearData(context: Context) {
        val editor =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
                .edit()
        editor.clear()
        editor.apply()
    }
}