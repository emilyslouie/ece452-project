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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
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
                            Toast.makeText(context, "Palette successfully deleted", Toast.LENGTH_SHORT).show()
                        }
                        it != SwipeToDismissBoxValue.StartToEnd
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
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color),
                            contentAlignment = Alignment.CenterEnd

                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.padding(end = 16.dp, top = 16.dp),
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
                                Text(text = "Palette ${palette.id}", fontSize = 18.sp)

                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedPalette != null) {
                                val bitmap = generateImageForPalette(selectedPalette!!)
                                val listofhexcodes = generateListOfHexcodes(selectedPalette!!)
                                val title = "Palette_Image"
                                val description = "Image generated from palette"

                                saveImageToDevice(context, bitmap, title, description, listofhexcodes)
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
    val cellSize = imageSize / palette.numberOfColors!!

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

fun generateListOfHexcodes(palette: Palette): List<String> {
    val colors = palette.colors.orEmpty()
    val hexCodes = mutableListOf<String>()

    colors.forEach { color ->
        val hexCode = String.format("#%06X", (0xFFFFFF and android.graphics.Color.parseColor(color)))
        hexCodes.add(hexCode)
    }

    return hexCodes
}

fun saveImageToDevice(
    context: Context,
    bitmap: Bitmap,
    title: String,
    description: String,
    hexCodes: List<String>
): Boolean {
    val timeStamp = System.currentTimeMillis()
    val imageFileName = "${title}_$timeStamp.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
        put(MediaStore.Images.Media.DESCRIPTION, description)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return if (imageUri != null) {
        try {
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height + 20, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBitmap)

                canvas.drawBitmap(bitmap, 0f, 0f, null)

                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 16f
                    textAlign = Paint.Align.CENTER
                }
                val cellSize = bitmap.width / hexCodes.size
                hexCodes.forEachIndexed { index, hexCode ->
                    paint.color = android.graphics.Color.LTGRAY
                    canvas.drawRect(
                        (index * cellSize).toFloat(),
                        bitmap.height.toFloat(),
                        ((index + 1) * cellSize).toFloat(),
                        bitmap.height + 20f,
                        paint
                    )

                    paint.color = android.graphics.Color.BLACK
                    canvas.drawText(
                        hexCode,
                        (index * cellSize + cellSize / 2).toFloat(),
                        bitmap.height + 20f - 5,
                        paint
                    )
                }

                if (newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    Toast.makeText(context, "Palette exported successfully", Toast.LENGTH_SHORT).show()
                    true // Image saved successfully
                } else {
                    Toast.makeText(context, "Failed to export palette", Toast.LENGTH_SHORT).show()
                    false // Failed to save image
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export palette", Toast.LENGTH_SHORT).show()
            false // Error occurred while saving image
        }
    } else {
        Toast.makeText(context, "Failed to export palette", Toast.LENGTH_SHORT).show()
        false // URI is null
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
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Palette ${palette.id}", fontSize = 18.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        previewViewModel.setBuildMode(false)
                        previewViewModel.setCurrentPaletteID(palette.id)
                        palette.colors?.let { previewViewModel.setCurrentPalette(it) }
                        previewViewModel.setCurrentColor(palette.colors?.get(0) ?: "#FFFFFF")
                        navigationController.navigate(Screens.PreviewScreen.screen)
                    },
            ) {
                palette.colors?.forEach { color ->
                    val hexCode = generateHexCode(color)
                    ColorBoxWithHex(color = color, hexCode = hexCode)
                }
            }
        }

    }
}

fun generateHexCode(color: String): String {
    return String.format("#%06X", (0xFFFFFF and android.graphics.Color.parseColor(color)))
}


@Composable
fun ColorBoxWithHex(color: String, hexCode: String) {
    Column {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color(android.graphics.Color.parseColor(color)))
        )
        Text(
            text = hexCode,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
            color = Color.Black
        )
    }
}



