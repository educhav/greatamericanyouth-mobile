package com.example.greatamericanyouth.viewmodels

import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

data class Message(val sender: String, val time: Long, val timestamp: String, val content: String, val media: String, val type: String)

class ChatViewModel: ViewModel() {
    private var webSocket: WebSocket? = null

    fun connectWebSocket() {
        val request = Request.Builder().url("ws://your_websocket_url").build()
        val client = OkHttpClient()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@ChatViewModel.webSocket = webSocket
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Handle received text message
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Handle received binary message
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Handle failure
            }
        }

        client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}