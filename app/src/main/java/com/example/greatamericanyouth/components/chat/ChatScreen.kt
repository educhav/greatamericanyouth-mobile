package com.example.greatamericanyouth.components.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.greatamericanyouth.viewmodels.ChatViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        bottomBar = { BottomToolbar(viewModel) },
        topBar = { TopToolbar() }
    ) {
        innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun BottomToolbar(viewModel: ChatViewModel) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { println(it) }
    }
    LaunchedEffect(Unit) {
        viewModel.connectWebSocket()
    }

    Row(modifier = Modifier.fillMaxWidth()){
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Enter message here..", fontFamily = FontFamily.Monospace) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Message, contentDescription = null)
            },
            modifier = Modifier.weight(1f).padding(4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    viewModel.sendMessage(message)
                }
            )
        )
        IconButton(onClick = { launcher.launch("image/*") },
            modifier = Modifier.width(64.dp)
                .padding(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.small)
        ) {
            Icon(imageVector = Icons.Default.Image, contentDescription = null)
        }

    }
}

@Composable
private fun TopToolbar() {

}


@Composable
private fun Message() {
}


