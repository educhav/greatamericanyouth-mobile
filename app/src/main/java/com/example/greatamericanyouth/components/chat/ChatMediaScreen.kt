package com.example.greatamericanyouth.components.chat

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.greatamericanyouth.components.common.components.getPlayer

@Composable
fun ChatMediaScreen(mediaUrl: String, isVideo: Boolean, navigateBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        topBar = { TopToolbar(navigateBack) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            if(isVideo) {
                Video(mediaUrl, isLandscape)
            }
            else {
                ZoomableImage(painter = rememberAsyncImagePainter(mediaUrl), isLandscape=isLandscape)
            }
        }
    }
}

@Composable
fun TopToolbar(navigateBack: () -> Unit) {
    val iconSize = 36.dp
    val iconColor = Color.White
    Row(modifier = Modifier.padding(8.dp)) {
        IconButton(onClick = {
            navigateBack()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.AutoGraph,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }

        }
    }
}

@Composable
fun Video(mediaUrl: String, isLandscape: Boolean) {
    val context = LocalContext.current
    val player = remember { getPlayer(mediaUrl, context)}
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    val modifier = if (isLandscape) Modifier.fillMaxHeight().aspectRatio(16 / 9f) else
            Modifier.fillMaxSize().aspectRatio(9 / 16f)
    AndroidView(modifier = modifier,
        factory = { cxt ->
            PlayerView(cxt).apply {
                this.player = player
                useController = true
            }
        }
    )

}

@Composable
fun ZoomableImage(
    minScale: Float = 1.0f,
    maxScale: Float = 100.0f,
    painter: androidx.compose.ui.graphics.painter.Painter,
    isLandscape: Boolean
) {
    var scale by remember { mutableStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val imageContentScale = if (isLandscape) ContentScale.FillHeight else ContentScale.FillWidth

    Box(
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    scale = scale.coerceIn(minScale, maxScale)
                    //offset += pan
                    //offset = if (offset.x + pan.x < 0
                    // && offset.y + pan.y < 0) offset + pan else offset
                }
            }
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = imageContentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}
