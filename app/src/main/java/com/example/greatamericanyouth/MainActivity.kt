package com.example.greatamericanyouth

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greatamericanyouth.components.common.BottomNavItem
import com.example.greatamericanyouth.components.common.BottomNavigationBar
import com.example.greatamericanyouth.components.chat.ChatScreen
import com.example.greatamericanyouth.components.home.HomeScreen
import com.example.greatamericanyouth.components.profile.ProfileScreen
import com.example.greatamericanyouth.components.common.TopBanner
import com.example.greatamericanyouth.ui.theme.GreatAmericanYouthTheme
import com.example.greatamericanyouth.viewmodels.ChatViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreatAmericanYouthTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navHostController = rememberNavController()
                    NavigationHost(navHostController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavigationHost(navHostController: NavHostController) {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        bottomBar = { BottomNavigationBar(navHostController) },
        topBar = { TopBanner() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val chatViewModel: ChatViewModel = viewModel()
            NavHost(navHostController, startDestination = BottomNavItem.Home.route) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen()
                }
                composable(BottomNavItem.Chat.route) {
                    ChatScreen(chatViewModel)
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

