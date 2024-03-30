package com.example.palletify.ui.preview


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.ColorUtils.hexToComposeColor
import com.example.palletify.R
import com.example.palletify.ui.theme.PalletifyTheme
import kotlin.random.Random
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.example.palletify.data.Palette
import androidx.compose.ui.window.Dialog
import com.example.palletify.ColorUtils

fun Palette.Color.toComposeColor(): Color {
    // Parse the hex string to a Long and create a Color
    return Color(android.graphics.Color.parseColor(this.hex.value))
}

fun Color.toPaletteColor(): Palette.Color {
    // Convert the floating-point values to 0-255 and then to a hex string
    val redValue = (this.red * 255).toInt()
    val greenValue = (this.green * 255).toInt()
    val blueValue = (this.blue * 255).toInt()
    val alphaValue = (this.alpha * 255).toInt()

    // Format the ARGB components to a hex string, including the alpha channel
    val hexValue = String.format("#%02X%02X%02X%02X", alphaValue, redValue, greenValue, blueValue)
    val cleanHexValue = hexValue.substring(1)

    val namePlaceholder = "Color-$cleanHexValue"
    
    return Palette.Color(
        hex = Palette.Hex(hexValue, cleanHexValue),
        rgb = Palette.Rgb(redValue, greenValue, blueValue),
        name = Palette.Name(namePlaceholder)
    )
}

@Composable
fun OutlinedRadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = color,
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(start = 28.dp, end = 28.dp, top = 28.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = color)
            )
            Text(
                text = text,
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun GraphCard(
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(16.dp)
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = with(LocalDensity.current) { 200.dp.toPx() }
            val colorScheme = MaterialTheme.colorScheme

            val points = remember {
                List(10) {
                    Offset(
                        x = Random.nextFloat() * width,
                        y = Random.nextFloat() * height
                    )
                }.sortedBy { it.x }
            }
            val path = remember {
                Path().apply {

                    moveTo(points.first().x, points.first().y)

                    for (i in 1 until points.size) {
                        val previousPoint = points[i - 1]
                        val currentPoint = points[i]
                        quadraticBezierTo(
                            x1 = (previousPoint.x + currentPoint.x) / 2,
                            y1 = previousPoint.y,
                            x2 = currentPoint.x,
                            y2 = currentPoint.y
                        )
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    profileImagePainter: Painter,
    primaryText: String,
    secondaryText: String,
    color: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(contentColor = color)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = profileImagePainter,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    primaryText,
                    style = TextStyle(
                        fontSize = 25.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(secondaryText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun RadioButtonGroup(
    color: Color
) {
    val selectedOption = remember { mutableStateOf("Option1") }

    Column {
        OutlinedRadioButtonWithText(
            text = "Radio Button - Option 1",
            selected = selectedOption.value == "Option1",
            onClick = { selectedOption.value = "Option1" },
            color = color
        )
        OutlinedRadioButtonWithText(
            text = "Radio Button - Option 2",
            selected = selectedOption.value == "Option2",
            onClick = { selectedOption.value = "Option2" },
            color = color
        )
    }
}

@Composable
fun ColorPickerDialog(
    controller: ColorPickerController,
    onDismissRequest: () -> Unit,
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
    onSave: (Color) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(10.dp),
                    controller = controller,
                    initialColor = initialColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                )
                Spacer(modifier = Modifier.height(16.dp))

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                )

                Spacer(modifier = Modifier.height(16.dp))

                AlphaTile(
                    modifier = Modifier
                        .size(80.dp),
                    controller = controller
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val c = controller.selectedColor
                            onSave(c.value)
                            onDismissRequest()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ContrastColorPickerDialog(
    palette: List<Palette.Color>,
    onDismissRequest: () -> Unit,
    firstColor: Palette.Color
) {
    val contrastResult = remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
                ) {
                    Text("Color One: ", modifier = Modifier.padding(8.dp), style=TextStyle(fontSize =24.sp))
                    ColorSample(color=firstColor, onClick={})
                }

                Text("Select Contrast Color", style = TextStyle(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    palette.forEach { color ->
                        ColorSample(color = color, onClick={
                            val contrastRatio = ColorUtils.contrastRatio(
                                hexToComposeColor(firstColor.hex),
                                hexToComposeColor(color.hex)
                            )
                            contrastResult.value = if (contrastRatio > 4.5) {
                                "Good Contrast (Ratio: %.2f)".format(contrastRatio)
                            } else {
                                "Not Enough Contrast (Ratio: %.2f)".format(contrastRatio)
                            }
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                contrastResult.value?.let {
                    Text(it, style = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    ),)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSample(color: Palette.Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = hexToComposeColor(color.hex))
    ) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(previewViewModel: PreviewViewModel = viewModel()) {
    val previewUiState by previewViewModel.uiState.collectAsStateWithLifecycle()
    val imagePainter = painterResource(id = R.drawable.profile)
    val scrollState = rememberScrollState()

    val showColorPicker = remember { mutableStateOf(false) }
    val showContrastModal = remember { mutableStateOf(false) }
    val controller = rememberColorPickerController()
    val whitePaletteColor = Palette.Color(
        hex = Palette.Hex("#FFFFFF", "FFFFFF"),
        rgb = Palette.Rgb(255, 255, 255),
        name = Palette.Name("White")
    )
    val selectedColor = remember { mutableStateOf(previewUiState.currentColor ?: whitePaletteColor) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = hexToComposeColor(previewUiState.currentColor)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    previewUiState.palette.forEach { color ->
                        Button(
                            onClick = {
                                previewViewModel.setCurrentColor(color)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = hexToComposeColor(
                                    color
                                )
                            )
                        ) {}
                    }
                },
            )
        },
    ) { innerPadding ->
        if (showColorPicker.value) {
            ColorPickerDialog(
                controller = controller,
                onDismissRequest = { showColorPicker.value = false },
                onColorChanged = { color ->
                },
                initialColor = previewUiState.currentColor.toComposeColor(),
                onSave = { color ->
                    Log.d("Color picker", "selected color: ${color}")
                    previewViewModel.setCurrentColor(color.toPaletteColor())
                    showColorPicker.value = false
                }
            )
        }
        if (showContrastModal.value) {
            ContrastColorPickerDialog(
                palette = previewUiState.colors,
                onDismissRequest = { showContrastModal.value = false },
                firstColor = previewUiState.currentColor)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, top = 28.dp, end = 28.dp)
            ) {
                Text(
                    text = "Welcome to Palletify Preview!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.padding(start=8.dp, top=8.dp, bottom=28.dp),
                    onClick = { showColorPicker.value = true }
                ) {
                    Text("Edit Color")
                }
                Button(
                    modifier = Modifier.padding(start=8.dp, top=8.dp, bottom=28.dp),
                    onClick = { showContrastModal.value = true }
                ) {
                    Text("Contrast with Another Color")
                }
            }
            AccessibleComponentWrapper(
                foregroundColor = Color.White,
                backgroundColor = hexToComposeColor(previewUiState.currentColor)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 28.dp, end = 28.dp)
                        .heightIn(min = 55.dp),
                    onClick = { /* No action is triggered */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = hexToComposeColor(
                            previewUiState.currentColor
                        )
                    )
                ) {

                    Text("Button")
                }
            }
            AccessibleComponentWrapper(
                foregroundColor = hexToComposeColor(previewUiState.currentColor),
                backgroundColor = Color.White
            ) { RadioButtonGroup(hexToComposeColor(previewUiState.currentColor)) }

            AccessibleComponentWrapper(
                foregroundColor = hexToComposeColor(previewUiState.currentColor),
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                ProfileCard(
                    profileImagePainter = imagePainter,
                    primaryText = "John Preview Doe",
                    secondaryText = "Software Engineer at Palletify Corp",
                    color = hexToComposeColor(previewUiState.currentColor)
                )
            }
            AccessibleComponentWrapper(
                foregroundColor = hexToComposeColor(previewUiState.currentColor),
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                GraphCard(hexToComposeColor(previewUiState.currentColor))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenPreview() {
    PalletifyTheme {
        PreviewScreen()
    }
}
