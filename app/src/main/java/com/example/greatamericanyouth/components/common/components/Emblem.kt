package com.example.greatamericanyouth.components.common.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.greatamericanyouth.R


@Composable
fun Emblem() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val painter: Painter = painterResource(R.drawable.turing)
    val topPadding = if(isLandscape) 16.dp else 256.dp
    val size = if(isLandscape) 256.dp else 2048.dp
    Image(
        painter = painter,
        contentDescription = null,
        alignment = Alignment.Center,
        modifier = Modifier.size(size).padding(top = topPadding).clip(RoundedCornerShape(16.dp)),
    )
}