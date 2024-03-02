package com.example.greatamericanyouth.services.common

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.greatamericanyouth.viewmodels.LoginInfo

// Class for Global User Information used by multiple screens
class AuthService(context: Context) {
    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "password"
        private const val KEY_TOKEN = "token"
        private const val KEY_PROFILE_PHOTO = "profilePhoto"
    }
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    fun saveCredentials(loginInfo: LoginInfo) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, loginInfo.username)
            putString(KEY_ROLE, loginInfo.role)
            putString(KEY_TOKEN, loginInfo.token)
            putString(KEY_PROFILE_PHOTO, loginInfo.profilePhoto)
            apply()
        }
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, false)
            putString(KEY_USERNAME, null)
            putString(KEY_ROLE, null)
            putString(KEY_TOKEN, null)
            putString(KEY_PROFILE_PHOTO, null)
            apply()
        }
    }
    fun saveProfilePhoto(profilePhoto: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_PROFILE_PHOTO, profilePhoto)
            apply()
        }
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    fun getRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, null)
    }
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    fun getProfilePhoto(): String? {
        return sharedPreferences.getString(KEY_PROFILE_PHOTO, null)
    }
}
