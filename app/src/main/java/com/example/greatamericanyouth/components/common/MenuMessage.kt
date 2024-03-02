package com.example.greatamericanyouth.components.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MenuMessage {
}
@Composable
fun MenuMessage(text: String) {
    var isScalingUp by remember { mutableStateOf(true) }

    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (isScalingUp) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        finishedListener = {
            isScalingUp = !isScalingUp
        }, label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .scale(scale)
                .padding(16.dp)
                .background(
                    color = Color.Gray
                ),
            color = Color.Black
        )
    }
}