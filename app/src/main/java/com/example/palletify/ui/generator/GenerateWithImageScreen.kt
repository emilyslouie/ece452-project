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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    val colors = remember { mutableStateListOf(0xFFfff7b2, 0xFFccf865, 0xFF49cc6e, 0xFF9b7367, 0xFF775798)};
    val otherColors = remember { mutableStateListOf(0xFFfff7b2, 0xFFccf865)};

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
                colors.forEach { color ->
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .heightIn(min = 55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(color)),
                        onClick = { /* No action is triggered */ }
                    ) {
                        Text("")
//                        Icon(
//                            Icons.Filled.Delete,
//                            contentDescription = "Localized description"
//                        )
                    }
                }
                if (colors.size <= 5) {
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .heightIn(min = 55.dp),
                        onClick = { colors.add(0xFFfff7b2) }
                    ) {
                        Text(
                            text = "+",
                            style = TextStyle(
                                fontSize = 32.sp, fontWeight = FontWeight.Bold
                            )
                        )
                    }
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
