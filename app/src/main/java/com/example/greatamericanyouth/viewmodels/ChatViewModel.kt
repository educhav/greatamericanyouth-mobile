package com.example.greatamericanyouth.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greatamericanyouth.components.common.utils.getFileType
import com.example.greatamericanyouth.components.common.utils.isAudio
import com.example.greatamericanyouth.components.common.utils.isVideo
import com.example.greatamericanyouth.components.common.utils.uriToFile
import com.example.greatamericanyouth.repositories.ChatRepository
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch

data class Message(
    val sender: String,
    val time: Long,
    val content: String?,
    val type: String,
    val media: String?
)

data class MessageRequest(
    val sender: String,
    val time: Long,
    val content: String?,
    val type: String,
    val media: ByteArray?,
    val fileType: String?
)

class ChatViewModel: ViewModel() {
    private val chatRepository = ChatRepository()
    private val options = IO.Options.builder()
        .setMultiplex(true)
        .setUpgrade(true)
        .build()
    private val socket: Socket = IO.socket("https://greatamericanyouth.com/", options)
    private var isConnecting: Boolean = false
    private val _messages: MutableState<List<Message>> = mutableStateOf(emptyList())
    val messages: MutableState<List<Message>> = _messages
    private val _partyProfilePhotoMap: MutableState<Map<String, String>> = mutableStateOf(emptyMap())
    val partyProfilePhotoMap: MutableState<Map<String, String>> = _partyProfilePhotoMap

    private val _mediaGalleryMessages: MutableState<List<Message>> = mutableStateOf(emptyList())
    val mediaGalleryMessages: MutableState<List<Message>> = _mediaGalleryMessages

    // TODO: Fetch most recent 50 messages
    init {
        println("Test")
    }

    fun connect(onMessage: () -> Unit) {
        if (this.isConnecting) return
        this.isConnecting = true;
        socket.connect()
        socket.on("message") { args ->
            val gson = Gson()
            val message = gson.fromJson(args[0].toString(), Message::class.java)
            _messages.value += message
            onMessage()
        }
    }
    fun disconnect() {
        this.socket.disconnect()
    }

    fun sendMessage(sender: String, content: String) {
        if (content.isEmpty()) return
        val isSql = content.startsWith("/")
        val gson = Gson()
        if (isSql) {
            val message = MessageRequest(sender,
                time = System.currentTimeMillis(),
                content.substring(1), type = "sql", media = null, fileType = null)
            socket.emit("message", gson.toJson(message))
            return
        }
        val message =
            MessageRequest(
                sender,
                time = System.currentTimeMillis(),
                content,
                type = "user-message",
                media = null,
                fileType = null
            )
        socket.emit("message", gson.toJson(message))
    }
    fun sendMedia(sender: String, media: Uri, context: Context) {
        val gson = Gson()
        val file = uriToFile(context, media)
        val fileType = getFileType(context, media)
        val isVideo = isVideo(context, media)
        val isAudio = isAudio(context, media)
        val type = when {
            isVideo -> "mobile-video"
            isAudio -> "mobile-audio"
            else -> "mobile-photo"
        }
        val message =
            MessageRequest(
                sender,
                time = System.currentTimeMillis(),
                content = null,
                type = type,
                media = file.readBytes(),
                fileType
            )
        socket.emit("message", gson.toJson(message))
    }

    fun getPartyProfilePhotos(token: String) {
        viewModelScope.launch {
            val result = chatRepository.getPartyProfilePhotos(token)
            if (result.data?.partyProfilePhotoMap != null) {
                _partyProfilePhotoMap.value = result.data.partyProfilePhotoMap
            }
        }
    }

    fun getChatMediaGallery(token: String?, startTimestamp: Long = 0L, endTimestamp: Long = 9000000000000L) {
        if (token == null) return
        val bearerToken = "Bearer $token"
        viewModelScope.launch {
            val response = chatRepository.getChatMediaGallery(bearerToken, startTimestamp, endTimestamp).data
            _mediaGalleryMessages.value = response ?: emptyList()
        }
    }
}