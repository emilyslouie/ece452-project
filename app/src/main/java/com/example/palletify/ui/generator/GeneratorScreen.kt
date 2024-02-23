package com.example.palletify.ui.generator

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palletify.R
import com.example.palletify.ui.theme.PalletifyTheme
import kotlin.concurrent.thread

@Composable
fun GeneratorScreen(gameViewModel: GeneratorViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Palette(colors = gameUiState.colors)
        Button(
            onClick = {
                thread { gameViewModel.getRandomPalette() } }
        ) {
            Text(text = "Generate")
        }

//        Text(
//            text = stringResource(R.string.app_name),
//            style = typography.titleLarge,
//        )
//        GameLayout(
//            onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
//            wordCount = gameUiState.currentWordCount,
//            userGuess = gameViewModel.userGuess,
//            onKeyboardDone = { gameViewModel.checkUserGuess() },
//            currentScrambledWord = gameUiState.currentScrambledWord,
//            isGuessWrong = gameUiState.isGuessedWordWrong,
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(mediumPadding)
//        )
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(mediumPadding),
//            verticalArrangement = Arrangement.spacedBy(mediumPadding),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Button(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = { gameViewModel.checkUserGuess() }
//            ) {
//                Text(
//                    text = stringResource(R.string.submit),
//                    fontSize = 16.sp
//                )
//            }
//
//            OutlinedButton(
//                onClick = { gameViewModel.skipWord() },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = stringResource(R.string.skip),
//                    fontSize = 16.sp
//                )
//            }
//        }
//
//        GameStatus(score = gameUiState.score, modifier = Modifier.padding(20.dp))
//
//        if (gameUiState.isGameOver) {
//            FinalScoreDialog(
//                score = gameUiState.score,
//                onPlayAgain = { gameViewModel.resetGame() }
//            )
//        }
    }
}

@Composable
fun Palette(colors: List<com.example.palletify.data.Color>) {
    Column ( Modifier.fillMaxSize()){
        colors.forEach {color ->
            ColorInPalette(color)
        }
    }
}

@Composable
fun ColorInPalette(color: com.example.palletify.data.Color) {
    val backgroundColor = Color(color.rgb.fraction.r, color.rgb.fraction.g, color.rgb.fraction.b)
    // formula from: https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
    val luminosity2 = color.rgb.r * 0.299 + color.rgb.g * 0.587 + color.rgb.b * 0.114 // this is not compliant with W3C, need to modify it in another iteration
    Row(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(top = 16.dp, bottom = 16.dp)) {
            Text(modifier = Modifier.padding(bottom=4.dp), text = color.hex.clean, color = if (luminosity2 >= 186) Color.Black else Color.White, style = typography.bodyLarge)
            Text(text = color.name.value, color = if (luminosity2 >= 186) Color.Black else Color.White, style = typography.labelMedium)
        }

    }
}

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

    }
}

@Composable
fun GameLayout(
    currentScrambledWord: String,
    wordCount: Int,
    isGuessWrong: Boolean,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Text(
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium,
                color = colorScheme.onPrimary
            )
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium
            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = onUserGuessChanged,
                label = {
                    if (isGuessWrong) {
                        Text(stringResource(R.string.wrong_guess))
                    } else {
                        Text(stringResource(R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                )
            )
        }
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PalletifyTheme {
        GeneratorScreen()
    }
}