package com.example.greatamericanyouth.components.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.greatamericanyouth.MainActivity
import java.io.File
import java.io.IOException
import java.util.*

fun getImagePermissionCode(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
fun getVideoPermissionCode(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
fun getAudioPermissionCode(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
fun getAudioRecordingPermissionCode(): String =
    Manifest.permission.RECORD_AUDIO

fun hasImagePermissionsGranted(context: Context) = ContextCompat.checkSelfPermission(context, getImagePermissionCode()) == PackageManager.PERMISSION_GRANTED
fun hasVideoPermissionsGranted(context: Context) = ContextCompat.checkSelfPermission(context, getVideoPermissionCode()) == PackageManager.PERMISSION_GRANTED
fun hasAudioPermissionsGranted(context: Context) = ContextCompat.checkSelfPermission(context, getAudioPermissionCode()) == PackageManager.PERMISSION_GRANTED
fun hasAudioRecordingPermissionsGranted(context: Context) = ContextCompat.checkSelfPermission(context, getAudioRecordingPermissionCode()) == PackageManager.PERMISSION_GRANTED

fun isVideo(context: Context, uri: Uri): Boolean {
    val contentResolver = context.contentResolver
    val mimeType: String? = contentResolver.getType(uri)
    return mimeType?.startsWith("video/") ?: false
}
fun isAudio(context: Context, uri: Uri): Boolean {
    val contentResolver = context.contentResolver
    val mimeType: String? = contentResolver.getType(uri)
    return mimeType?.startsWith("audio/") ?: false
}

fun getFileType(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    return mimeType?.substringAfterLast('/') ?: "Unknown"
}


fun uriToFile(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    val fileType = getFileType(context, uri)
    val filePath = File(context.cacheDir, "picked_media").apply {
        if (!exists()) mkdirs()
    }
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(filePath, "${UUID.randomUUID()}.${fileType}")
        file.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        inputStream?.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        throw IOException("Error creating file", e)
    }
}
@Composable
fun getMediaRequestPermissionLauncher(
                        context: Context,
                        imageOnly: Boolean,
                        pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val requiredPermissions = if (imageOnly) listOf(getImagePermissionCode()) else listOf(getImagePermissionCode(), getVideoPermissionCode())
    val mediaType = if (imageOnly) ActivityResultContracts.PickVisualMedia.ImageOnly else ActivityResultContracts.PickVisualMedia.ImageAndVideo
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions  ->
            var isGranted = true
            requiredPermissions.forEach { requiredPermission ->
                if(permissions[requiredPermission] == false) {
                    isGranted = false
                }
            }
            if (isGranted) {
                pickMedia.launch(PickVisualMediaRequest(mediaType))
            } else {
                var rationaleRequired = true
                requiredPermissions.forEach { requiredPermission ->
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity, requiredPermission)) {
                        rationaleRequired = false
                    }
                }
                val toastMessage = if (rationaleRequired) "Photos permission is required for this feature to work" else "Photos permission is required. Please enable it in the Android settings"
                Toast.makeText(context,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    )
}


@Composable
fun getAudioRequestPermissionLauncher(
    context: Context,
    onGranted: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted  ->
            if (isGranted) {
                onGranted()
            } else {
                val rationaleRequired =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity, Manifest.permission.RECORD_AUDIO
                    )
                if (rationaleRequired) {
                    Toast.makeText(context,
                        "Audio recording permissions are required for this feature to work",
                        Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,
                        "Audio recording permissions are required. Please enable it in the Android settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )
}
