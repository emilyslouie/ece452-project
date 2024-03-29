package com.example.palletify.ui.generator

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.palletify.R
import com.example.palletify.database.PaletteViewModel
import java.lang.Long.parseLong
import com.example.palletify.ui.generator.GenerateWithImage.convertImageUrlToBitmap
import com.example.palletify.ui.generator.GenerateWithImage.extractColorsFromBitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import android.graphics.Color as GraphicsColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateWithImageScreen(
    uploadedImageUri: Uri?,
    generatorViewModel: GeneratorViewModel = viewModel()
) {
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val paletteViewModel =
        ViewModelProvider(context as ViewModelStoreOwner).get(PaletteViewModel::class.java)

//    val colors = remember { mutableStateListOf(0xFFfff7b2, 0xFFccf865, 0xFF49cc6e, 0xFF9b7367, 0xFF775798)};
    val colors = remember { mutableStateListOf<String>()}
    val moreColors = remember { mutableStateListOf<String>()}

    val colorPalette by generatorViewModel.colorPalette

    var launchedEffectTriggered by remember { mutableStateOf(false) }

//    val imageUrl by generatorViewModel.imageUrl// find what the image url is
    val imageUrl = uploadedImageUri.toString()

    var selectedOption by remember {
        mutableStateOf("")
    }
    val onSelectionChange = { text: String ->
        selectedOption = text
    }

    LaunchedEffect(key1 = true) {
        try {
            val bitmap = convertImageUrlToBitmap(
                imageUrl = imageUrl,
                context = context
            )
            if (bitmap != null) {
                launchedEffectTriggered = true
                generatorViewModel.setColorPaletteFromImage(
                    colors = extractColorsFromBitmap(
                        bitmap = bitmap
                    )
                )
                var colorCount = 0
                colorPalette.forEach { (_, value) ->
                    if (!(colors.contains(value) || moreColors.contains(value))) {
                        if (colorCount < 5) {
                            colors.add(value)
                            colorCount++
                        } else {
                            moreColors.add(value)
                        }
                    }

                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
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
            Image(
                painter = rememberImagePainter(uploadedImageUri),
                contentDescription = null,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                onSelectionChange(color)
                            }
                            .background(Color(GraphicsColor.parseColor(color)))
//                            .padding(
//                                vertical = 1.dp,
//                                horizontal = 1.dp,
//                            )
                            .border(
                                if (color == selectedOption) {
                                    BorderStroke(1.5.dp, Color.DarkGray)
                                } else {
//                                    BorderStroke(1.dp, Color.Gray)
                                    BorderStroke(0.dp, Color.White)
                                }
                            )
                        ,
                    )
                }
                if (colors.size <= 5) {
                    Button(
                        modifier = Modifier
                            .size(60.dp)
                            .heightIn(min = 55.dp),

                        onClick = {
                            if (moreColors.isNotEmpty()) {
                                colors.add(moreColors[0])
                                moreColors.removeAt(0)
                            }
                        }
                    ) {
                        Text(
                            text = "+",
                            style = TextStyle(
                                fontSize = 32.sp, fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            if (selectedOption == ""){
                Button(modifier = Modifier
                    .padding(12.dp)
                    .heightIn(min = 55.dp)
                    .fillMaxWidth(),
                    onClick = {
                        val currentPalette = generatorViewModel.uiState.value.currentPalette
                        val numberOfColors = generatorViewModel.uiState.value.numberOfColours
                        val mode = generatorViewModel.uiState.value.mode
                        val colorsList = mutableListOf<String>()
                        for (i in 0 until numberOfColors) {
                            val color = currentPalette[i].hex.value
                            colorsList.add(color)
                        }
                        val palette = com.example.palletify.database.Palette(
                            0,
                            numberOfColors,
                            colorsList,
                            mode.mode,
                            favourite = false
                        )
                        paletteViewModel.addPalette(palette)
                    }
                ) {
                    Text(
                        text = "Save to Library",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Button(modifier = Modifier
                        .padding(12.dp)
                        .heightIn(min = 55.dp).weight(1f),
                        onClick = {
                            onSelectionChange("")
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 4.dp),
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Button(modifier = Modifier
                        .padding(12.dp)
                        .heightIn(min = 55.dp).weight(1f),
                        onClick = {
                            colors.remove(selectedOption)
                            moreColors.add(selectedOption)
                            onSelectionChange("")
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 4.dp),
                            text = "Delete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

            }

        }
    }
}
