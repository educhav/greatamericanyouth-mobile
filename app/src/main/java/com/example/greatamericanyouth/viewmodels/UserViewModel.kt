package com.example.greatamericanyouth.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.greatamericanyouth.repositories.UserRepository
import com.example.greatamericanyouth.services.common.AuthService
import com.example.greatamericanyouth.services.common.ProfilePhotoResponse
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _role = mutableStateOf("")
    val role: State<String> = _role

    private val _profilePhoto = mutableStateOf("")
    val profilePhoto: State<String?> = _profilePhoto

    private val userRepository = UserRepository()

    fun loadSavedUserInfo(authService: AuthService) {
        _isLoggedIn.value = authService.isLoggedIn()
        _username.value = authService.getUsername() ?: ""
        _role.value = authService.getRole() ?: ""
        _profilePhoto.value = authService.getProfilePhoto() ?: ""
    }

    fun login(username: String, password: String, context: Context, onResult: (Boolean, String) -> Unit) {
        val authService = AuthService(context)
        viewModelScope.launch {
            val result = userRepository.login(username, password)
            if (result.data != null) {
                val loginInfo = result.data
                _isLoggedIn.value = true
                _username.value = loginInfo.username ?: ""
                _role.value = loginInfo.role ?: ""
                _profilePhoto.value = loginInfo.profilePhoto ?: ""
                authService.saveCredentials(loginInfo)
            }
            else {
                result.error?.let { onResult(false, it) }
            }
        }
    }

    fun logout(context: Context) {
        val authService = AuthService(context)
        authService.logout()
        _isLoggedIn.value = false
        _username.value = ""
        _role.value = ""
        _profilePhoto.value = ""
    }

    fun uploadProfilePhoto(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) : String? {
        val authService = AuthService(context)
        viewModelScope.launch {
            val result = userRepository.uploadProfilePhoto(context, uri)
            if (result.data != null) {
                val profilePhotoUrl = result.data.profilePhoto ?: ""
                _profilePhoto.value = profilePhotoUrl
                authService.saveProfilePhoto(profilePhotoUrl)
            }
            else {
                result.error?.let { onResult(false, it) }
            }
        }
        return null
    }
}

data class LoginInfo(val username: String?, val role: String?, val token: String, val profilePhoto: String?)
