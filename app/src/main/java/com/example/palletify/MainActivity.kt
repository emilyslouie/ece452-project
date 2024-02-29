package com.example.palletify

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.palletify.ui.CameraView
import com.example.palletify.ui.generator.GeneratorScreen
import com.example.palletify.ui.theme.PalletifyTheme
import com.example.palletify.ui.home.HomeScreen
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.palletify.ui.generator.Palette
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
    private var imageUriState = mutableStateOf<Uri?>(null)

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            PalletifyTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    HomeScreen(selectImageLauncher = ::selectImageLauncher);
////                    GeneratorScreen();
//                }
//            }
            if (shouldShowCamera.value) {
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("kilo", "View error:", it) }
                )
            }

            if(shouldShowPhoto.value){
                Scaffold(
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(onClick = {}) {
                                    Text(
                                        modifier = Modifier.padding(end = 4.dp),
                                        text = stringResource(R.string.retake_photo),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                Button(onClick = {}) {
                                    Text(
                                        modifier = Modifier.padding(end = 4.dp),
                                        text = stringResource(R.string.use_photo),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .safeDrawingPadding()
                            .padding(innerPadding)
                            .fillMaxHeight()
                            ,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberImagePainter(photoUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

            }
        }
        requestCameraPermission()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        imageUriState.value = uri
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
