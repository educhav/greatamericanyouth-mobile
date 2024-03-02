package com.example.greatamericanyouth.services.common

import android.os.Parcelable
import com.example.greatamericanyouth.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part

@Parcelize
data class LoginResponse(
    val status: String,
    val token: String?
) : Parcelable

@Parcelize
data class ProfilePhotoResponse(
    val status: String,
    val profilePhoto: String?
) : Parcelable

data class ApiResponse<T>(val data: T?, val error: String?)

data class LoginRequest(
    val username: String,
    val password: String
)

const val API_URL = "https://greatamericanyouth.com/api/"

private val retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


val apiService: ApiService = retrofit.create(ApiService::class.java)

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @Multipart
    @POST("profile-photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part,
    ): ProfilePhotoResponse
}