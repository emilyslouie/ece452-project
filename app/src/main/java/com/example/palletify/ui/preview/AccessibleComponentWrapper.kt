package com.example.palletify.ui.preview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
//import androidx.compose.material3.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import com.example.palletify.ui.preview.ColorUtils.contrastRatio

@Composable
fun AccessibleComponentWrapper(
    foregroundColor: Color,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    val contrast = contrastRatio(foregroundColor, backgroundColor)
    val showDialog = remember { mutableStateOf(false) }

    Box {
        content()

        if (contrast < 4.5) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Low Contrast Warning",
                tint = Color(0xFFFFA500),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clickable { showDialog.value = true }
            )
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Accessibility Warning") },
                text = { Text("The colors of this element do not meet contrast accessibility requirements.") },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
