package com.dentalgo.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session — stores/retrieves Bearer token and basic user info
 * using SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("DentalGo_Prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN    = "auth_token"
        private const val KEY_USER_ID  = "user_id"
        private const val KEY_NAME     = "user_name"
        private const val KEY_EMAIL    = "user_email"
        private const val KEY_PHONE    = "user_phone"
        private const val KEY_BIO      = "user_bio"
    }

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun isLoggedIn(): Boolean = getToken() != null

    fun saveUserInfo(id: Int, name: String, email: String, phone: String?, bio: String?) {
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PHONE, phone ?: "")
            .putString(KEY_BIO, bio ?: "")
            .apply()
    }

    fun getUserId()    = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName()  = prefs.getString(KEY_NAME, "") ?: ""
    fun getUserEmail() = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getUserPhone() = prefs.getString(KEY_PHONE, "") ?: ""
    fun getUserBio()   = prefs.getString(KEY_BIO, "") ?: ""

    fun clearSession() = prefs.edit().clear().apply()
}
