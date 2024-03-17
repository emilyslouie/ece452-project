package com.example.palletify.data


import com.example.palletify.data.Palette.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random

/**
 * Data that is needed for the generator
 */
fun fetchRandomHex(): String {
    val client = OkHttpClient()
    val request = Request(
        url = "https://x-colors.yurace.pro/api/random".toHttpUrl()
    )
    val response = client.newCall(request).execute()
    val json = response.body.string()
    val responseBody = jsonBuilder.decodeFromString<RandomColourResponseBody>(json)
    return responseBody.hex.substring(1)
}

@Serializable
data class RandomColourResponseBody(
    val hex: String,
    val rgb: String,
    val hsl: String,
)

private val modeOptions = listOf(
    "monochrome",
    "monochrome-dark",
    "monochrome-light",
    "analogic",
    "complement",
    "analogic-complement",
    "triad",
    "quad"
)

fun getRandomGenerationMode(): String {
    return modeOptions[Random.nextInt(modeOptions.size)];
}

private val jsonBuilder = Json { ignoreUnknownKeys = true }

fun fetchPalette(
    seed: String,
    mode: String = getRandomGenerationMode(),
    numOfColours: Int = 5
): PaletteResponseBody {
    val client = OkHttpClient()
    val request = Request(
        url = "https://www.thecolorapi.com/scheme?hex=$seed&mode=$mode&count=$numOfColours".toHttpUrl()
    )
    val response = client.newCall(request).execute()
    val json = response.body.string()
    return jsonBuilder.decodeFromString<PaletteResponseBody>(json)
}

@Serializable
data class PaletteResponseBody(
    val mode: String,
    val count: Int,
    val colors: MutableList<Color>,
    val seed: Color,
)
