package com.example.palletify.ui.generator

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.palletify.database.PaletteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateWithImageScreen(uploadedImageUri: Uri?, generatorViewModel: GeneratorViewModel = viewModel()) {
    val generatorUiState by generatorViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val palletViewModel1 = ViewModelProvider(context as ViewModelStoreOwner).get(PaletteViewModel::class.java)

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
//                modifier = Modifier.padding(16.dp, 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .heightIn(min = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFfff7b2),),
                    onClick = { /* No action is triggered */ }
                ) {
                    Text("")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .heightIn(min = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFccf865)),
                    onClick = { /* No action is triggered */ }
                ) {
                    Text("")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .heightIn(min = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF49cc6e),),
                    onClick = { /* No action is triggered */ }
                ) {
                    Text("")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .heightIn(min = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9b7367),),
                    onClick = { /* No action is triggered */ }
                ) {
                    Text("")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .heightIn(min = 55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF775798),),
                    onClick = { /* No action is triggered */ }
                ) {
                    Text("")
                }
            }
            Button(modifier = Modifier
                .padding(12.dp)
                .heightIn(min = 10.dp)
                .fillMaxSize(),
                onClick = {
                    val currentPalette = generatorViewModel.uiState.value.currentPalette
                    val numberOfColors = generatorViewModel.uiState.value.numberOfColours
                    val mode = generatorViewModel.uiState.value.mode
                    val colorsList = mutableListOf<String>()
                    for (i in 0 until numberOfColors) {
                        val color = currentPalette[i].hex.value
                        colorsList.add(color)
                    }
                    val pallet = com.example.palletify.database.Palette(
                        0,
                        numberOfColors,
                        colorsList,
                        mode,
                        favourite = false
                    )
                    palletViewModel1.addPalette(pallet)
                }
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "Save",
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    Icons.Filled.AddCircle,
                    contentDescription = "Localized description"
                )

            }
        }
    }
}
