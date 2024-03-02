package com.example.greatamericanyouth.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greatamericanyouth.R

@Composable
fun TopBanner() {
    Row {
        val painter: Painter = painterResource(R.drawable.banner_eagle)
        Image(painter = painter,
            contentDescription = null,
            modifier = Modifier.height(55.dp).clip(RoundedCornerShape(16.dp))
        )
        val coloredTitle = getColoredTitle()
        Text(coloredTitle,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
@Composable
private fun getColoredTitle(): AnnotatedString {
    return buildAnnotatedString {
        "The Great American Youth".forEachIndexed { _, char ->
            val color = when (char) {
                'G' -> Color.Red
                'A' -> Color.Blue
                'Y' -> Color.White
                else -> MaterialTheme.colorScheme.onBackground
            }

            withStyle(
                style = SpanStyle(color = color)
            ) {
                append(char.toString())
            }
        }
    }
}