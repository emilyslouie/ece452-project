package com.example.palletify.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    val bullet = "\u2022"
    val points = listOf(
        "Palletify allows you to create aesthetic colour palettes while on-the-go!",
        "Choose from 7 generation methods to generate palettes of 3 to 6 colours.",
        "Lock colours that you like and use them to generate the next palette!",
        "Feeling inspired? Take a photo and sample colours from it!",
        "Once you're satisfied, save the palette to your Library.",
        "Preview colours and confirm that they pass web accessibility guidelines."
    )

    val disclaimerText = "Remember that colours can have different meanings in different cultures around the world. " +
            "When generating palettes, ensure that you research your audience and identify any associations they " +
            "might have with specific colours."

    Column(modifier=Modifier
        .statusBarsPadding()
        .safeDrawingPadding()
        .fillMaxWidth()
        .padding(top = 64.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)) {
        Text(
            "About Palletify",
            modifier=Modifier.padding(top=16.dp, bottom=8.dp),
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.W800)
        )
        Text(
            buildAnnotatedString {
                points.forEach {
                    append(bullet)
                    append("\t")
                    append(it)
                    append("\n")
                }
            }
        )
        Text(
            "Note:",
            modifier=Modifier.padding(bottom=8.dp),
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.W600)
        )
        Text(
            disclaimerText
        )
    }
}