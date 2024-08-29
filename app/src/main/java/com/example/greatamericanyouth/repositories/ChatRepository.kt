package com.example.greatamericanyouth.repositories

import com.example.greatamericanyouth.Screen
import com.example.greatamericanyouth.services.common.ApiResponse
import com.example.greatamericanyouth.services.common.ChatMediaGalleryResponse
import com.example.greatamericanyouth.services.common.PartyProfilePhotosResponse
import com.example.greatamericanyouth.services.common.apiService
import com.example.greatamericanyouth.viewmodels.Message
import retrofit2.HttpException
import java.io.IOException

class ChatRepository {
    suspend fun getPartyProfilePhotos(token: String): ApiResponse<PartyProfilePhotosResponse> {
        try {
            val response = apiService.getPartyProfilePhotos(token)
            return ApiResponse(data = response, error = null)
        } catch (e: HttpException) {
            return ApiResponse(data = null, error = e.message())
        } catch (e : IOException) {
            return ApiResponse(data = null, error = e.message)
        }
    }
    suspend fun getChatMediaGallery(token: String, startTimestamp: Long, endTimestamp: Long): ApiResponse<List<Message>> {
        try {
            val response = apiService.getChatMediaGallery(token, startTimestamp, endTimestamp)
            return ApiResponse(data = response, error = null)
        } catch (e: HttpException) {
            return ApiResponse(data = null, error = e.message())
        } catch (e : IOException) {
            return ApiResponse(data = null, error = e.message)
        }
    }
}