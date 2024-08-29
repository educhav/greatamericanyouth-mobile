package com.example.greatamericanyouth.components.chat

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.greatamericanyouth.viewmodels.ChatViewModel

@Composable
fun ChatMediaGalleryScreen(chatViewModel: ChatViewModel) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(chatViewModel.mediaGalleryMessages.value) {message ->
            rememberAsyncImagePainter(message.media)
        }
    }
}