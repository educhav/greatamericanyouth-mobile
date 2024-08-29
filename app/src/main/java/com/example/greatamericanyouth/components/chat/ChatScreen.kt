package com.example.greatamericanyouth.components.chat

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.Card
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.greatamericanyouth.R
import com.example.greatamericanyouth.components.common.components.AudioRecorder
import com.example.greatamericanyouth.components.common.components.ProfilePhoto
import com.example.greatamericanyouth.components.common.components.TextWrapper
import com.example.greatamericanyouth.components.common.components.getPlayer
import com.example.greatamericanyouth.components.common.utils.getAudioRecordingPermissionCode
import com.example.greatamericanyouth.components.common.utils.getAudioRequestPermissionLauncher
import com.example.greatamericanyouth.components.common.utils.getImagePermissionCode
import com.example.greatamericanyouth.components.common.utils.getMediaRequestPermissionLauncher
import com.example.greatamericanyouth.components.common.utils.getVideoPermissionCode
import com.example.greatamericanyouth.components.common.utils.hasAudioRecordingPermissionsGranted
import com.example.greatamericanyouth.components.common.utils.hasImagePermissionsGranted
import com.example.greatamericanyouth.components.common.utils.hasVideoPermissionsGranted
import com.example.greatamericanyouth.services.common.AuthService
import com.example.greatamericanyouth.viewmodels.ChatViewModel
import com.example.greatamericanyouth.viewmodels.Message
import com.example.greatamericanyouth.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Action(val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun ChatScreen(chatViewModel: ChatViewModel, userViewModel: UserViewModel,
               navigateToMediaGalleryScreen: () -> Unit,
               navigateToMediaScreen: (String, Boolean) -> Unit) {
    val scrollState = rememberLazyListState()
    val authService = AuthService(LocalContext.current)
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        bottomBar = { BottomToolbar(chatViewModel, userViewModel, scrollState) },
        topBar = { TopToolbar(chatViewModel, authService, navigateToMediaGalleryScreen) }
    ) {
        innerPadding ->
        ChatMessages(chatViewModel.messages.value, scrollState, navigateToMediaScreen, innerPadding)
    }
}

@Composable
private fun ChatMessages(messages: List<Message>,
                         scrollState: LazyListState,
                         navigateToMediaScreen: (String, Boolean) -> Unit,
                         innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(innerPadding).fillMaxSize(),
        state = scrollState
    ) {
        items(messages) { message ->
            ChatMessage(message, navigateToMediaScreen)
        }
    }
}

@Composable
private fun ChatMessage(message: Message, navigateToMediaScreen: (String, Boolean) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    val handler = Handler()
    handler.postDelayed({ isVisible = !isVisible }, 1000)
    Row {
        ChatProfilePhoto()
        Column(modifier = Modifier.padding(8.dp)) {
            ChatMessageInfo(message)
            Spacer(modifier = Modifier.padding(4.dp))
            when (message.type) {
                "user-message" -> MessageBubble(message.content)
                "sql" -> MessageBubble(message.content)
                "mobile-photo" -> ImageMediaWrapper(message.media, navigateToMediaScreen)
                "mobile-video" -> ChatVideoPlayer(message.media, navigateToMediaScreen)
            }
        }
    }
}

@Composable
private fun ChatProfilePhoto() {
    val authService = AuthService(LocalContext.current)
    Column(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        ProfilePhoto(authService.getProfilePhoto(), 48) {}
    }
}

@Composable
private fun ChatMessageInfo(message: Message) {
    TextWrapper("${message.sender} at ${formatTime(message.time)}", 12)
}

@Composable
private fun MessageBubble(text: String?) {
    Box(modifier = Modifier
        .background(colorResource(R.color.blue), shape = RoundedCornerShape(16.dp))
        .padding(12.dp)
    ) {
        TextWrapper(text ?: "", size = 16)
    }
}

@Composable
private fun ImageMediaWrapper(mediaUrl: String?, navigateToMediaScreen: (String, Boolean) -> Unit) {
    if (mediaUrl == null) return
    ImageWrapper(mediaUrl, size = 256) {
        navigateToMediaScreen(mediaUrl, false)
    }
}

@Composable
private fun ImageWrapper(mediaUrl: String, size: Int, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(mediaUrl),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size.dp).clip(RoundedCornerShape(16.dp)).clickable {
            onClick()
        }
    )
}



@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun BottomToolbar(chatViewModel: ChatViewModel, userViewModel: UserViewModel, scrollState: LazyListState) {
    var message by remember { mutableStateOf("") }
    val sender = userViewModel.username
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showAudioRecorder by remember { mutableStateOf(false) }
    val brush = remember {
        Brush.verticalGradient(
            listOf(Color.Red, Color.White)
        )
    }
    val pickMedia = getPickMedia(chatViewModel, sender.value, context)
    val mediaPermissionLauncher = getMediaRequestPermissionLauncher(context, imageOnly = false, pickMedia)
    val audioPermissionLauncher = getAudioRequestPermissionLauncher(context, {})
    val onMediaUploadClick = {
        if (hasImagePermissionsGranted(context) && hasVideoPermissionsGranted(context)) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        else {
            mediaPermissionLauncher.launch(arrayOf(getImagePermissionCode(), getVideoPermissionCode()))
        }
    }
    val onAudioUploadClick = {
        if (hasAudioRecordingPermissionsGranted(context)) {
            showAudioRecorder = true
        }
        else {
            audioPermissionLauncher.launch(getAudioRecordingPermissionCode())
        }
    }
    // Send a callback to scroll down after a message is received
    chatViewModel.connect {
        scope.launch {
            scrollDown(scrollState)
        }
    }
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (showAudioRecorder) {
                AudioRecorder(context, modifier = Modifier.weight(1f), onSend = { uri ->
                    chatViewModel.sendMedia(sender.value, uri, context)
                    showAudioRecorder = false
                }, onDelete = { showAudioRecorder= false })
            }
            else {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Type here..", fontFamily = FontFamily.Monospace) },
                    //textStyle = TextStyle(brush = brush),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = Color.White)
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            chatViewModel.sendMessage(sender.value, message)
                            message = ""
                        }
                    )
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    ExpandableActions(
                        expandIcon = Icons.Default.Add,
                        expandedActions = listOf(
                            Action(Icons.Default.Mic, Color.White, {}),
                            Action(Icons.Default.Audiotrack, Color.White, onAudioUploadClick),
                            Action(Icons.Default.Image, Color.White, onMediaUploadClick)
                        ),
                        size = 36
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableActions(
    expandIcon: ImageVector,
    expandedActions: List<Action>,
    size: Int
) {
    var expanded by remember { mutableStateOf(false) }
    Row {
        AnimatedVisibility(
            visible = expanded,
            enter = expandHorizontally(animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)),
            exit = shrinkHorizontally(animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing))
        ) {
            Row {
                expandedActions.forEach { action ->
                    Icon(
                        imageVector = action.icon,
                        tint = action.color,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                action.onClick()
                                expanded = !expanded
                            }
                            .padding(start = 4.dp, top = 4.dp)
                            .size(size.dp)
                    )
                }
            }
        }
        Icon(
            imageVector = expandIcon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                }
                .size(size.dp)
                .padding(start = 4.dp, top = 4.dp, end = 4.dp)
        )

    }
}
@androidx.annotation.OptIn(UnstableApi::class) @Composable
fun ChatVideoPlayer(mediaUrl: String?, navigateToMediaScreen: (String, Boolean) -> Unit) {
    if(mediaUrl == null) return;
    val context = LocalContext.current
    val player = remember { getPlayer(mediaUrl, context) }
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    AndroidView(modifier = Modifier
        .fillMaxWidth(0.7f)
        .aspectRatio(9 / 16f)
        .clip(RoundedCornerShape(16.dp))
        .clickable { navigateToMediaScreen(mediaUrl, true) },
        factory = { cxt ->
            PlayerView(cxt).apply {
                this.player = player
                useController = false
            }
        }
    )
}

@Composable
private fun TopToolbar(chatViewModel: ChatViewModel, authService: AuthService, navigateToMediaGalleryScreen: () -> Unit) {
    Row {
        TextWrapper("Chat", size = 20, modifier = Modifier.weight(1f).padding(top = 8.dp, start = 16.dp))
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
        IconButton(onClick = {
            chatViewModel.getChatMediaGallery(authService.getToken())
            navigateToMediaGalleryScreen()
        }) {
            Icon(imageVector = Icons.Default.Image, contentDescription = null)
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val userTimeZone = TimeZone.getDefault().id
    val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone(userTimeZone)
    return dateFormat.format(Date(timestamp))
}

private suspend fun scrollDown(scrollState: LazyListState) {
    if (scrollState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
        val recentMessageIndex = scrollState.layoutInfo.totalItemsCount - 1
        val currentScrolledIndex = scrollState.layoutInfo.visibleItemsInfo.last().index
        val isScrolledDown = currentScrolledIndex >= recentMessageIndex
        if (isScrolledDown) {
            scrollState.animateScrollToItem(recentMessageIndex)
        }
    }
}

@Composable
private fun getPickMedia(chatViewModel: ChatViewModel, sender: String, context: Context): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    return rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            chatViewModel.sendMedia(sender, uri, context)
        }
    }
}

