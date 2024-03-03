package com.example.palletify.ui.preview

import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text

import androidx.compose.ui.Alignment

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import com.example.palletify.ui.theme.PalletifyTheme
import com.example.palletify.ui.preview.ColorUtils.contrastRatio

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.palletify.R

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.getValue

import com.example.palletify.ui.preview.AccessibleComponentWrapper
import androidx.compose.runtime.remember

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.ui.preview.PreviewViewModel
import kotlin.random.Random

@Composable
fun OutlinedRadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(contentColor = color, containerColor = Color.Transparent),
        modifier = Modifier
            .padding(start = 28.dp, end = 28.dp, top = 28.dp)
            .fillMaxWidth()
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
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

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(previewViewModel: PreviewViewModel = viewModel()) {
    val previewUiState by previewViewModel.uiState.collectAsStateWithLifecycle()
    val imagePainter = painterResource(id = R.drawable.profile)
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    previewUiState.colors.forEach { color ->
                        Button(
                            onClick = {previewViewModel.setCurrentColor(color)},
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = previewViewModel.hexToComposeColor(color.hex))
                        ) {}
                    }
                },
            )
        },
    ) { innerPadding ->
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
                    .padding(28.dp)
            ) {
                Text(
                    text = "Welcome to Palletify Preview!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AccessibleComponentWrapper(
                foregroundColor = Color.White,
                backgroundColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 28.dp, end = 28.dp)
                        .heightIn(min = 55.dp),
                    onClick = { /* No action is triggered */ },
                    colors = ButtonDefaults.buttonColors(containerColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex))
                ) {

                    Text("Button")
                }
            }
            AccessibleComponentWrapper(
                foregroundColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex),
                backgroundColor = Color.White
            ) { RadioButtonGroup(previewViewModel.hexToComposeColor(previewUiState.currentColor.hex)) }

            AccessibleComponentWrapper(
                foregroundColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex),
                backgroundColor = Color.White
            ) {
                ProfileCard(
                    profileImagePainter = imagePainter,
                    primaryText = "John Preview Doe",
                    secondaryText = "Software Engineer at Palletify Corp",
                    color = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex)
                )
            }
            AccessibleComponentWrapper(
                foregroundColor = previewViewModel.hexToComposeColor(previewUiState.currentColor.hex),
                backgroundColor = Color.White
            ) {
                GraphCard(previewViewModel.hexToComposeColor(previewUiState.currentColor.hex))
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
