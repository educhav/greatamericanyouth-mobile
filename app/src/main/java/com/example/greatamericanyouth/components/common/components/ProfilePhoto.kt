package com.example.greatamericanyouth.components.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProfilePhoto(profilePhoto: String?, size: Int, onClick: () -> Unit) {
    if (profilePhoto?.isBlank() == true) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(size.dp).clip(CircleShape).clickable { onClick() }
        )
    }
    else {
        AsyncImage(
            model = profilePhoto,
            contentDescription = null,
            modifier = Modifier.size(size.dp).clip(CircleShape).clickable { onClick() },
            contentScale = ContentScale.Crop
        )
        //Image(
            //painter = rememberAsyncImagePainter(profilePhoto),
            //contentDescription = null,
            //modifier = Modifier.size(size.dp).border(1.dp, Color.White, RoundedCornerShape(32.dp)).fillMaxSize()
        //)
    }

}