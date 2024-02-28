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

import androidx.compose.runtime.remember

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun OutlinedRadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 28.dp, end = 28.dp, top = 28.dp)
            .fillMaxWidth()
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected,
                onClick = onClick
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
fun GraphCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
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

            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                drawPath(
                    path = path,
                    color = colorScheme.onSurface,
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
    secondaryText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
//                    .border(2.dp, Color.Gray, CircleShape), // Circular border
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
                        fontSize = 25.sp,
//                        textAlign = TextAlign.Center,
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
fun RadioButtonGroup() {
    val selectedOption = remember { mutableStateOf("Option1") }

    Column {
        OutlinedRadioButtonWithText(
            text = "Radio Button - Option 1",
            selected = selectedOption.value == "Option1",
            onClick = { selectedOption.value = "Option1" }
        )
        OutlinedRadioButtonWithText(
            text = "Radio Button - Option 2",
            selected = selectedOption.value == "Option2",
            onClick = { selectedOption.value = "Option2" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen() {
    val imagePainter = painterResource(id = R.drawable.profile)
    val scrollState = rememberScrollState()
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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp)
                    .heightIn(min = 55.dp),
                onClick = { /* No action is triggered */ }
            ) {
                Text("Button")
            }
            RadioButtonGroup()

            ProfileCard(
                profileImagePainter = imagePainter,
                primaryText = "John Preview Doe",
                secondaryText = "Software Engineer at Palletify Corp"
            )
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 28.dp, end = 28.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text("Some Primary Text", style = MaterialTheme.typography.headlineSmall)
//                    Text("Some secondary text", style = MaterialTheme.typography.bodyMedium)
//
//                }
//            }
            GraphCard()
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
