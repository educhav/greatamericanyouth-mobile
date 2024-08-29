package com.example.greatamericanyouth.components.common.components

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

fun getPlayer(mediaUrl: String, context: Context): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(Uri.parse(mediaUrl)))
        prepare()
    }
}