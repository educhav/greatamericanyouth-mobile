package com.example.greatamericanyouth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greatamericanyouth.components.chat.ChatMediaGalleryScreen
import com.example.greatamericanyouth.components.chat.ChatMediaScreen
import com.example.greatamericanyouth.components.common.components.BottomNavItem
import com.example.greatamericanyouth.components.common.components.BottomNavigationBar
import com.example.greatamericanyouth.components.chat.ChatScreen
import com.example.greatamericanyouth.components.profile.ProfileScreen
import com.example.greatamericanyouth.services.common.AuthService
import com.example.greatamericanyouth.ui.theme.GreatAmericanYouthTheme
import com.example.greatamericanyouth.viewmodels.ChatViewModel
import com.example.greatamericanyouth.viewmodels.UserViewModel


class MainActivity : ComponentActivity() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreatAmericanYouthTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    userViewModel = UserViewModel()
                    chatViewModel = ChatViewModel()
                    val navHostController = rememberNavController()
                    App(userViewModel, chatViewModel, navHostController)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        chatViewModel.disconnect()
    }
}

sealed class Screen(val route: String) {
    object Profile : Screen("profile")
    object Chat : Screen("chat")
    object ChatMedia : Screen("chat-media")
    object ChatMediaGallery : Screen("chat-media-gallery")
}

@Composable
fun App(userViewModel: UserViewModel, chatViewModel: ChatViewModel, navHostController: NavHostController) {
    val authService = AuthService(LocalContext.current)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Profile) }
    // TODO: Fix logic so it doesn't disconnect when screen is rotated
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        bottomBar = { BottomNavigationBar(navHostController, currentScreen) },
        topBar = {  }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NavHost(navHostController, startDestination = Screen.Profile.route) {
                composable(Screen.Profile.route) {
                    currentScreen = Screen.Profile
                    userViewModel.loadSavedUserInfo(authService)
                    ProfileScreen(userViewModel)
                }
                composable(Screen.Chat.route) {
                    currentScreen = Screen.Chat
                    userViewModel.loadSavedUserInfo(authService)
                    if (authService.getRole() == "party") {
                        //authService.getToken()?.let { token -> chatViewModel.getPartyProfilePhotos("Bearer ${token}") }
                        ChatScreen(chatViewModel, userViewModel,
                            navigateToMediaGalleryScreen = {
                                navHostController.navigate(Screen.ChatMediaGallery.route)
                            }
                        ) { mediaUrl, isVideo ->
                            navHostController.currentBackStackEntry?.savedStateHandle?.set("mediaUrl", mediaUrl)
                            navHostController.currentBackStackEntry?.savedStateHandle?.set("isVideo", isVideo)
                            navHostController.navigate(Screen.ChatMedia.route)
                        }
                    }
                }
                composable(Screen.ChatMedia.route) {
                    currentScreen = Screen.ChatMedia
                    val mediaUrl = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("mediaUrl")
                    val isVideo = navHostController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isVideo")
                    if (mediaUrl != null) {
                        ChatMediaScreen(mediaUrl, isVideo ?: false) {
                            navHostController.popBackStack()
                        }
                    }
                }
                composable(Screen.ChatMediaGallery.route) {
                    currentScreen = Screen.ChatMediaGallery
                    ChatMediaGalleryScreen(chatViewModel)
                }
            }
        }
    }
}

