package com.example.greatamericanyouth.components.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.greatamericanyouth.viewmodels.UserViewModel
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import com.example.greatamericanyouth.components.common.components.Emblem
import com.example.greatamericanyouth.components.common.components.ProfilePhoto
import com.example.greatamericanyouth.components.common.components.TextWrapper
import com.example.greatamericanyouth.components.common.utils.getImagePermissionCode
import com.example.greatamericanyouth.components.common.utils.getMediaRequestPermissionLauncher
import com.example.greatamericanyouth.components.common.utils.hasImagePermissionsGranted

@Composable
fun ProfileScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current

    if (!userViewModel.isLoggedIn.value) {
        LoginForm(userViewModel)
    } else {
        Profile(userViewModel, context)
    }
}

@Composable
fun Profile(userViewModel: UserViewModel, context: Context) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(userViewModel, context) },
        bottomBar = { BottomBar(userViewModel, context) }
    ) { innerPadding ->
        Emblem()
        Column(modifier = Modifier.padding(innerPadding)) {
        }
    }
}

@Composable
private fun BottomBar(userViewModel: UserViewModel, context: Context) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            userViewModel.logout(context)
        }){
            Text("LOG OUT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold)
        }
    }

}


@Composable
private fun TopBar(userViewModel: UserViewModel, context: Context) {
    val pickMedia = getPickMedia(userViewModel, context)
    val requestPermissionLauncher = getMediaRequestPermissionLauncher(context, imageOnly = true, pickMedia)

    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp)) {
            Column(modifier = Modifier.padding(end = 16.dp)) {
                ProfilePhoto(userViewModel.profilePhoto.value, 48) {
                    if (hasImagePermissionsGranted(context)) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    else {
                        requestPermissionLauncher.launch(arrayOf(getImagePermissionCode()))
                    }
                }
            }
            Column {
                TextWrapper(userViewModel.username.value, 20)
                TextWrapper(userViewModel.role.value, 12)
            }
        }
    }
}

@Composable
private fun getPickMedia(userViewModel: UserViewModel, context: Context): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    return rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            userViewModel.uploadProfilePhoto(context, uri, onResult = { success, message ->
                if (!success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

