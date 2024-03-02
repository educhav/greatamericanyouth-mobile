package com.example.greatamericanyouth.components.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Chat : BottomNavItem("chat", Icons.Default.Message, "Chat")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
    companion object {
        val allItems: List<BottomNavItem>
            get() = listOf(Home, Chat, Profile)
    }

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        BottomNavItem.allItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceTint)
                },
                label = { Text(item.label, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
            )
        }
    }
}
