package com.example.greatamericanyouth.components.common.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ImageWrapper() {
}

@Composable
fun TextWrapper(text: String, size: Int, modifier: Modifier = Modifier) {
    Text(
        text,
        fontSize = size.sp,
        color = Color.White,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier
    )
}