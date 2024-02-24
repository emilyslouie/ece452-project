package com.example.palletify.data


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
    val request: Request = okhttp3.Request(
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

private val modeOptions = listOf("monochrome", "monochrome-dark", "monochrome-light", "analogic", "complement", "analogic-complement", "triad", "quad")

private val jsonBuilder = Json { ignoreUnknownKeys = true }

fun fetchPalette(
    seed: String,
    mode: String = modeOptions[Random.nextInt(modeOptions.size)],
    numOfColours: Int = 5
): PaletteResponseBody {
    val client = OkHttpClient()
    val request: Request = okhttp3.Request(
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
    val colors: List<Color>,
    val image: Image,
    val seed: Color,
)

@Serializable
data class Color(
    val hex: Hex,
    val rgb: Rgb,
    val name: Name,
    val contrast: Contrast,
)

@Serializable
data class Hex(
    val value: String,
    val clean: String,
)

@Serializable
data class Rgb(
    val fraction: RgBFraction,
    val r: Float,
    val g: Float,
    val b: Float,
    val value: String,
)

@Serializable
data class RgBFraction(
    val r: Float,
    val g: Float,
    val b: Float,
)

@Serializable
data class Name(
    val value: String,
)

@Serializable
data class Contrast(
    val value: String
)

@Serializable
data class Image(
    val bare: String,
    val named: String,
)
