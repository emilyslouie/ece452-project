package com.example.palletify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.palletify.data.Palette

@Composable
fun TrademarkComponentWrapper(
    color: Palette.Color,
    list: Set<String>,
    map: Map<String, Palette.Color>,
    content: @Composable () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    Box {
        content()

        if (list.contains(color.hex.value)) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Trademarked color",
                tint = Color(0xFFFFA500),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clickable { showDialog.value = true }
            )
        }

        if (showDialog.value) {
            val colorName = map[color.hex.value]?.name?.value;
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Trademarked Color Warning") },
                text = { Text("This color is trademarked as $colorName and you should be careful about using it as the primary colour in your designs.") },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
