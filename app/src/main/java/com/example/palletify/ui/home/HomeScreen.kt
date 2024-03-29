package com.example.palletify.ui.home

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color.parseColor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.palletify.ui.generator.GenerateWithImage.extractColorsFromBitmap
import com.example.palletify.ui.generator.GenerateWithImage.convertImageUrlToBitmap
import java.util.Objects
import com.example.palletify.BuildConfig
import com.example.palletify.R
import com.example.palletify.Screens
import com.example.palletify.ui.generator.GenerateWithImageScreen
import com.example.palletify.ui.image.createImageFile

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun HomeScreen(navigationController: NavController) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    var uploadedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    val selectImageLauncher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {
                Text(
                    text = "Welcome to Palletify!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp)
                    .heightIn(min = 55.dp),
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        // Request a permission
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }) {
                Text("Take a photo to generate colours")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp)
                    .heightIn(min = 55.dp),
                onClick = { selectImageLauncher.launch("image/*") }
            ) {
                Text("Upload a photo to generate colours")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp)
                    .heightIn(min = 55.dp),
                onClick = { navigationController.navigate(Screens.GenerateScreen.screen) }
            ) {
                Text("Randomly generate colours")
            }
//            Button(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 28.dp, end = 28.dp)
//                    .heightIn(min = 55.dp),
//                onClick = { /* No action is triggered */ }
//            ) {
//                Text("Customize your own palette")
//            }
        }
    }

    if (capturedImageUri.path?.isNotEmpty() == true) {
        GenerateWithImageScreen(capturedImageUri)
    }
    if (uploadedImageUri?.path?.isNotEmpty() == true) {
        GenerateWithImageScreen(uploadedImageUri)
    }
}