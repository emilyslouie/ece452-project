package com.example.palletify.data


import com.example.palletify.ColorUtils.getAnalogousHue
import com.example.palletify.ColorUtils.getDarkerAndLighter
import com.example.palletify.ColorUtils.hslToRgb
import com.example.palletify.ColorUtils.rgbToHex
import com.example.palletify.ColorUtils.rgbToHsl
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random

/**
 * Data that is needed for the generator
 */
fun fetchRandomColors(count: Int = 1): MutableList<Palette.Color> {
    val client = OkHttpClient()
    val request = Request(
        url = "https://x-colors.yurace.pro/api/random?number=$count".toHttpUrl()
    )
    val response = client.newCall(request).execute()
    var json = response.body.string()
    if (count == 1) {
        json = "[$json]";
    }
    val responseBody = jsonBuilder.decodeFromString<List<RandomColor>>(json)
    val result = mutableListOf<Palette.Color>()
    for (color in responseBody) {
        val hex = Palette.Hex(color.hex, color.hex.substring(1));
        val rgbFromResponse = color.rgb.split(',')
        val r = rgbFromResponse[0].substring(4).trim().toInt()
        val g = rgbFromResponse[1].trim().toInt()
        val b = rgbFromResponse[2].substringBefore(')').trim().toInt();
        val rgb = Palette.Rgb(r, g, b);
        result.add(Palette.Color(hex, rgb));
    }
    return result;
}

@Serializable
data class RandomColourResponseBody(
    val colors: List<RandomColor>,
)

@Serializable
data class RandomColor(
    val hex: String,
    val rgb: String,
)


enum class GenerationMode(val mode: String) {
    MONOCHROME("monochrome"),
    ANALOGIC("analogic"),
    COMPLEMENT("complement"),
    TRIAD("triad"),
    QUAD("quad"),
    GRADIENT("gradient"),
    RANDOM("random"),
    ANY("any")
}

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

fun getRandomGenerationMode(): GenerationMode {
    val mode = GenerationMode.entries.random()
    // Ensure that the returned mode is not ANY
    return if (mode == GenerationMode.ANY) {
        getRandomGenerationMode();
    } else {
        return mode;
    }
}

private val jsonBuilder = Json { ignoreUnknownKeys = true }

fun fetchPaletteFromColorApi(
    seed: String,
    mode: String = modeOptions[Random.nextInt(modeOptions.size)],
    numOfColours: Int = 5
): MutableList<Palette.Color> {
    val client = OkHttpClient()
    val request = Request(
        url = "https://www.thecolorapi.com/scheme?hex=$seed&mode=$mode&count=$numOfColours".toHttpUrl()
    )
    val response = client.newCall(request).execute()
    val json = response.body.string()
    val response2 = jsonBuilder.decodeFromString<PaletteResponseBody>(json)
    return response2.colors;
}

@Serializable
data class PaletteResponseBody(
    val colors: MutableList<Palette.Color>,
)


fun fetchPalette(
    seeds: MutableSet<Palette.Color>,
    count: Int,
    generationMode: GenerationMode,
): MutableList<Palette.Color> {
    val result = when (generationMode) {
        GenerationMode.COMPLEMENT -> generateComplementaryPalette(seeds, count);
        GenerationMode.RANDOM -> generateRandomPalette(seeds, count);
        GenerationMode.ANALOGIC -> generateAnalogicPalette(seeds, count);
        // TODO: Implement the remaining color generation algorithms
        else -> {
            // If the generation mode is not specified, for now, get a random palette from the Color API
            fetchPaletteFromColorApi(seed = seeds.first().hex.clean, numOfColours = count);
        }
    }
    return result;
}

fun generateComplementaryPalette(
    seeds: MutableSet<Palette.Color>,
    count: Int
): MutableList<Palette.Color> {
    val rgbColors: MutableList<Array<Int>> = mutableListOf();
    for (color in seeds) {
        val rgb = color.rgb;
        rgbColors.add(arrayOf(rgb.r, rgb.g, rgb.b))
        // TODO: need to create a random offset so that if the same seed is passed in again, we do not create the same palette
        val complementaryRgb = arrayOf(255 - rgb.r, 255 - rgb.g, 255 - rgb.b);
        rgbColors.add(complementaryRgb);
    }

    var hslSeed = 0;
    var currentCount = rgbColors.size;
    while (currentCount < count) {
        val hsl = rgbToHsl(rgbColors[hslSeed]);
        val newLightnessValues = getDarkerAndLighter(hsl[2], Random.nextDouble(0.05, 0.15));
        val firstNewHsl = hslToRgb(arrayOf(hsl[0], hsl[1], newLightnessValues[0]));
        rgbColors.add(firstNewHsl);
        val secondNewHsl = hslToRgb(arrayOf(hsl[0], hsl[1], newLightnessValues[1]));
        rgbColors.add(secondNewHsl);
        hslSeed++;
        currentCount += 2;
    }

    val result: MutableList<Palette.Color> = mutableListOf();
    for (color in 0..<count) {
        val currColor = rgbColors[color];
        val rgb = Palette.Rgb(currColor[0], currColor[1], currColor[2]);
        val hexValue = rgbToHex(currColor);
        val hex = Palette.Hex("#$hexValue", hexValue);
        // TODO: use the Color Api to get the name of the color and add it back to the Color object
        result.add(Palette.Color(hex, rgb));
    }
    return result;
}

fun generateAnalogicPalette(
    seeds: MutableSet<Palette.Color>,
    count: Int
): MutableList<Palette.Color> {
    val rgbColors: MutableList<Array<Int>> = mutableListOf();
    for (color in seeds) {
        val rgb = color.rgb;
        rgbColors.add(arrayOf(rgb.r, rgb.g, rgb.b))
    }

    var hslSeed = 0;
    var currentCount = rgbColors.size;
    while (currentCount < count) {
        val hsl = rgbToHsl(rgbColors[hslSeed]);
        val newHueValues = getAnalogousHue(hsl[0], Random.nextDouble(0.05, 0.15));
        val firstNewHsl = hslToRgb(arrayOf(newHueValues[0], hsl[1], hsl[2]));
        rgbColors.add(firstNewHsl);
        val secondNewHsl = hslToRgb(arrayOf(newHueValues[1], hsl[1], hsl[2]));
        rgbColors.add(secondNewHsl);
        hslSeed++;
        currentCount += 2;
    }

    val result: MutableList<Palette.Color> = mutableListOf();
    for (color in 0..<count) {
        val currColor = rgbColors[color];
        val rgb = Palette.Rgb(currColor[0], currColor[1], currColor[2]);
        val hexValue = rgbToHex(currColor);
        val hex = Palette.Hex("#$hexValue", hexValue);
        // TODO: use the Color Api to get the name of the color and add it back to the Color object
        result.add(Palette.Color(hex, rgb));
    }
    return result;
}

fun generateRandomPalette(
    seeds: MutableSet<Palette.Color>,
    count: Int
): MutableList<Palette.Color> {
    val result: MutableList<Palette.Color> = seeds.toMutableList();
    result.addAll(fetchRandomColors(count - seeds.size));
    return result;
}
