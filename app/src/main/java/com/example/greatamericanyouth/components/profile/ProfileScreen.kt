package com.example.greatamericanyouth.components.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.greatamericanyouth.services.common.AuthService
import com.example.greatamericanyouth.viewmodels.UserViewModel
import androidx.activity.result.PickVisualMediaRequest
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.greatamericanyouth.MainActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProfileScreen() {
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current
    val authService = AuthService(context)
    userViewModel.loadSavedUserInfo(authService)

    if (!userViewModel.isLoggedIn.value) {
        LoginForm(userViewModel)
    } else {
        Profile(userViewModel, context)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Profile(userViewModel: UserViewModel, context: Context) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(userViewModel, context) },
        bottomBar = { BottomBar(userViewModel, context) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
        }
    }
}

@Composable
private fun BottomBar(userViewModel: UserViewModel, context: Context) {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            userViewModel.logout(context)
        }){
            Text("LOG OUT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun TopBar(userViewModel: UserViewModel, context: Context) {
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            userViewModel.uploadProfilePhoto(context, uri, onResult = { success, message ->
                if (!success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                if (rationaleRequired) {
                    Toast.makeText(context,
                        "Photos permission is required for this feature to work",
                        Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,
                        "Photos permission is required. Please enable it in the Android settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.small)
            ) {
                if (userViewModel.profilePhoto.value?.isBlank() == true) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                }
                else {
                    Image(
                        painter = rememberAsyncImagePainter(userViewModel.profilePhoto.value),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).fillMaxSize()
                    )
                }
            }
            Column {
                Text(
                    userViewModel.username.value,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    userViewModel.role.value,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
