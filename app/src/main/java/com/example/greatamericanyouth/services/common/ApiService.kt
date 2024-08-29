package com.example.greatamericanyouth.services.common

import android.os.Parcelable
import com.example.greatamericanyouth.BuildConfig
import com.example.greatamericanyouth.Screen
import com.example.greatamericanyouth.viewmodels.Message
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

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

@Parcelize
data class PartyProfilePhotosResponse(
    val status: String,
    val partyProfilePhotoMap: Map<String, String>?
) : Parcelable

@Parcelize
data class ChatMediaGalleryResponse(
    val status: String,
    val mediaGalleryMessages: @RawValue List<Message>?
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

    @GET("party-profile-photos")
    suspend fun getPartyProfilePhotos(
        @Header("Authorization") authorization: String
    ): PartyProfilePhotosResponse

    @GET("chat-media-gallery")
    suspend fun getChatMediaGallery(
        @Header("Authorization") authorization: String,
        @Query("startTimestamp") startTimestamp: Long,
        @Query("endTimestamp") endTimestamp: Long
    ): List<Message>
}