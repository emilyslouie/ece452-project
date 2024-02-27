package com.example.palletify.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import com.example.palletify.ui.theme.PalletifyTheme

@Composable
fun OutlinedRadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Row {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun RadioButtonGroup() {
    val selectedOption = remember { mutableStateOf("Option1") }

    Column {
        OutlinedRadioButtonWithText(
            text = "Option 1",
            selected = selectedOption.value == "Option1",
            onClick = { selectedOption.value = "Option1" }
        )
        OutlinedRadioButtonWithText(
            text = "Option 2",
            selected = selectedOption.value == "Option2",
            onClick = { selectedOption.value = "Option2" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen() {
    // State for RadioButton selection
    val selectedOption = remember { mutableStateOf("Option1") }

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
            Text(
                text = "Welcome to Palettify Preview!!",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = { /* TODO: Handle click */ }) {
                Text("a button")
            }
            OutlinedRadioButtonWithText(
                text = "a radio button",
                selected = selectedOption.value == "Option1",
                onClick = { selectedOption.value = "Option1" }
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Some Primary Text", style = MaterialTheme.typography.headlineSmall)
                    Text("Some secondary text", style = MaterialTheme.typography.bodyMedium)

                }
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
