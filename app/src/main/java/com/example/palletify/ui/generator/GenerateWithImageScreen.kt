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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.TextStyle
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
import com.example.palletify.ColorUtils.hexToRgb
import com.example.palletify.ColorUtils.toRgb
import com.example.palletify.data.GenerationMode
import com.example.palletify.data.Palette
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

    val colors = remember { mutableStateListOf<Palette.Color>()}
    val moreColors = remember { mutableStateListOf<Palette.Color>()}

    val colorPalette by generatorViewModel.colorPalette

    var launchedEffectTriggered by remember { mutableStateOf(false) }

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
                    extractColorsFromBitmap(
                        bitmap = bitmap
                    )
                )
                var colorCount = 0
                colorPalette.forEach { (_, value) ->
                    val colorObj = Palette.Color(
                        Palette.Hex(value, value.substring(1)),
                        hexToRgb(value).toRgb(),
                        Palette.Name("")
                    )
                    if (!(colors.contains(colorObj) || moreColors.contains(colorObj))) {
                        if (colorCount < MAX_NUMBER_OF_COLORS - 1) {
                            colors.add(colorObj)
                            colorCount++
                        } else {
                            moreColors.add(colorObj)
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
                                onSelectionChange(color.hex.value)
                            }
                            .background(Color(GraphicsColor.parseColor(color.hex.value)))
                            .border(
                                if (color.hex.value == selectedOption) {
                                    BorderStroke(1.5.dp, Color.DarkGray)
                                } else {
                                    BorderStroke(0.dp, Color.White)
                                }
                            )
                        ,
                    )
                }
                if (colors.size < MAX_NUMBER_OF_COLORS) {
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
                        val numberOfColors = colors.size
                        val mode = generatorViewModel.uiState.value.mode
                        val colorsList = mutableListOf<String>()
                        for (i in 0 until numberOfColors) {
                            val color = colors[i].hex.value
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
                            if (colors.size > MIN_NUMBER_OF_COLORS) {
                                val colorObj = Palette.Color(
                                    Palette.Hex(selectedOption, selectedOption.substring(1)),
                                    hexToRgb(selectedOption).toRgb(),
                                    Palette.Name("")
                                )

                                colors.remove(colorObj)
                                moreColors.add(colorObj)

                            } else {
                                Toast.makeText(context, "Need min of 3 colors", Toast.LENGTH_SHORT).show()
                            }
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
