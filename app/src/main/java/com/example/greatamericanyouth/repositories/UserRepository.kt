package com.example.greatamericanyouth.repositories

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.greatamericanyouth.services.common.ApiResponse
import com.example.greatamericanyouth.services.common.AuthService
import com.example.greatamericanyouth.viewmodels.LoginInfo
import com.example.greatamericanyouth.services.common.LoginRequest
import com.example.greatamericanyouth.services.common.LoginResponse
import com.example.greatamericanyouth.services.common.ProfilePhotoResponse
import com.example.greatamericanyouth.services.common.apiService
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.ByteString.Companion.decodeBase64
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UserRepository {
    suspend fun login(username: String, password: String) : ApiResponse<LoginInfo> {
        val loginRequest = LoginRequest(username, password)
        try {
            val response = apiService.login(loginRequest)
            if (response.status === "error") {
                return ApiResponse(data = null, error = "Wrong user credentials")
            }
            if (response.token == null) {
                return ApiResponse(data = null, error = "No JWT Token returned from server")
            }
            val loginInfo = decodeJwt(response.token)
            if (loginInfo?.role == null || loginInfo.username == null) {
                return ApiResponse(data = null, error = "Bad JWT Token returned from server")
            }
            return ApiResponse(data = loginInfo, error = null)
        } catch (e: HttpException) {
            return ApiResponse(data = null, error = e.message())
        } catch (e : IOException) {
            return ApiResponse(data = null, error = e.message)
        }
    }
    suspend fun uploadProfilePhoto(context: Context, uri: Uri) : ApiResponse<ProfilePhotoResponse> {
        try {
            val file = uriToFile(context, uri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val authService = AuthService(context)
            val token = "Bearer ${authService.getToken()}"
            val response = apiService.uploadProfilePhoto(token, body)
            if (response.status == "error") {
                return ApiResponse(data = null, error = "Server error")
            }
            return ApiResponse(data = response, error = null)
        } catch(e: HttpException) {
            return ApiResponse(data = null, error = e.message())
        } catch(e: IOException) {
            return ApiResponse(data = null, error = e.message)
        } catch(e: Exception) {
            return ApiResponse(data = null, error = e.message)
        }

    }
    private fun decodeJwt(token: String) : LoginInfo? {
        val parts = token.split(".")
        if (parts.size != 3) return null
        val payload = parts[1].decodeBase64() ?: return null
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val claims: Map<String, LinkedTreeMap<String, String>> = Gson().fromJson(payload.utf8(), type)
        val sub: LinkedTreeMap<String, String>? = claims["sub"]
        return LoginInfo(sub?.get("username"), sub?.get("role"), token, sub?.get("profilePhoto"))
    }
    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val filePath = File(context.cacheDir, "picked_media").apply {
            if (!exists()) mkdirs()
        }
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filePath, "profile_photo.jpg")
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Error creating file", e)
        }
    }
}