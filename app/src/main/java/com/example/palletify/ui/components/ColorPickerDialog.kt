package com.example.palletify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

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
                        .heightIn(max = 250.dp)
                        .padding(10.dp),
                    controller = controller,
                    initialColor = initialColor
                )
                
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    initialColor = initialColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Initial")
                        AlphaTile(
                            modifier = Modifier
                                .size(80.dp),
                            selectedColor = initialColor
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "New")
                        AlphaTile(
                            modifier = Modifier
                                .size(80.dp),
                            controller = controller
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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