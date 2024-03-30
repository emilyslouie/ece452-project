package com.example.palletify.ui.library

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.database.Palette
import com.example.palletify.database.PaletteViewModel
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.palletify.Screens
import com.example.palletify.ui.preview.PreviewViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Library(context: Context, navigationController: NavController) {
    val palleteViewModel: PaletteViewModel = viewModel()
    val previewViewModel =
        ViewModelProvider(LocalContext.current as ViewModelStoreOwner).get(PreviewViewModel::class.java)

    val allPalletsState = palleteViewModel.getAllPalettes.collectAsState()

    var selectedPalette: Palette? by remember { mutableStateOf(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
        LazyColumn {
            items(allPalletsState.value.reversed(), key = { palette ->
                palette.id
            }) { palette ->
                val state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            palleteViewModel.deletePalette(palette)
                        }
                        true
                    }
                )
                SwipeToDismissBox(
                    state = state,
                    backgroundContent = {
                        val color = when (state.dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> Color.Red
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(color),
                            contentAlignment = Alignment.CenterEnd

                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.padding(end = 16.dp),
                            )
                        }
                    }

                ) {
                    PaletteItem(palette = palette, navigationController, previewViewModel)
                }
            }
        }

        Button(
            onClick = { showExportDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Export")
        }

        // Export dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = {
                    selectedPalette = null
                    showExportDialog = false
                },
                title = { Text("Select Palette to Export") },
                text = {
                    LazyColumn {
                        items(allPalletsState.value.reversed(), key = { palette ->
                            palette.id
                        }) { palette ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPalette = palette
                                    }
                                    .padding(16.dp)
                                    .background(
                                        color = if (palette == selectedPalette) Color.Gray else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display palette ID
                                Text(text = "Palette ID: ${palette.id}", fontSize = 18.sp)

                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedPalette != null) {
                                val bitmap = generateImageForPalette(selectedPalette!!)
                                val title = "Palette_Image"
                                val description = "Image generated from palette"
                                saveImageToDevice(context, bitmap, title, description)
                                showExportDialog = false
                                selectedPalette = null
                            }
                        }
                    ) {
                        Text("Export")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showExportDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}

fun generateImageForPalette(palette: Palette): Bitmap {
    val imageSize = 400
    val cellSize = imageSize / 5 // TODO: hardcoded right now, change

    val bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint()

    val colors = palette.colors.orEmpty()
    colors.forEachIndexed { index, color ->
        paint.color = android.graphics.Color.parseColor(color)
        canvas.drawRect(
            index * cellSize.toFloat(), 0f,
            (index + 1) * cellSize.toFloat(), imageSize.toFloat(),
            paint
        )
    }

    return bitmap
}
fun saveImageToDevice(context: Context, bitmap: Bitmap, title: String, description: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, title)
        put(MediaStore.Images.Media.DESCRIPTION, description)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        resolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
}

@Composable
fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun PaletteItem(palette: Palette, navigationController: NavController, previewViewModel: PreviewViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Palette ID: ${palette.id}", fontSize = 18.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        previewViewModel.setCurrentPaletteID(palette.id)
                        palette.colors?.let { previewViewModel.setCurrentPalette(it) }
                        previewViewModel.setCurrentColor(palette.colors?.get(0) ?: "#FFFFFF")
                        navigationController.navigate(Screens.PreviewScreen.screen)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.colors?.forEach { color ->
                    ColorBox(color = color)
                }
            }
        }

    }
}


@Composable
fun ColorBox(color: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(color = Color(android.graphics.Color.parseColor(color)))
    )
}


