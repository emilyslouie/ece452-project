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
 * Get random colors for a specified number
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
        val name = fetchColorName(hex);

        result.add(Palette.Color(hex, rgb, name));
    }
    return result;
}

@Serializable
data class RandomColor(
    val hex: String,
    val rgb: String,
)

fun fetchColorName(color: Palette.Hex): Palette.Name {
    val client = OkHttpClient()
    val request = Request(
        url = "https://www.thecolorapi.com/id?hex=${color.clean}".toHttpUrl()
    )
    val response = client.newCall(request).execute()
    val json = response.body.string()
    val responseBody = jsonBuilder.decodeFromString<ColorNameResponse>(json)
    return Palette.Name(responseBody.name.value);
}

@Serializable
data class ColorNameResponse(
    val name: NameResponse,
)

@Serializable
data class NameResponse(
    val value: String,
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
        GenerationMode.MONOCHROME -> generateMonochromePalette(seeds, count);
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
        val name = fetchColorName(hex);
        result.add(Palette.Color(hex, rgb, name));
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
        val name = fetchColorName(hex);
        result.add(Palette.Color(hex, rgb, name));
    }
    return result;
}

fun generateMonochromePalette(
    seeds: MutableSet<Palette.Color>,
    count: Int
): MutableList<Palette.Color> {
    val rgbColors: MutableList<Array<Int>> = mutableListOf();

    // if there are multiple seeds, average their rgb values and use that as a single seed
    // (can change this later, not sure if it looks amazing)
    var rSum = 0
    var gSum = 0
    var bSum = 0

    for (color in seeds) {
        val rgb = color.rgb;
        rSum += rgb.r
        gSum += rgb.g
        bSum += rgb.b
    }
    // set seed to averaged rgb values from all seeds
    val averageRgbSeed = arrayOf(rSum / seeds.count(), gSum / seeds.count(), bSum / seeds.count())
    rgbColors.add(averageRgbSeed)
    val hslSeed = rgbToHsl(averageRgbSeed);

    val countAsDouble = count.toDouble()
    var currentCount: Double = 1.0
    val randomOffset = Random.nextDouble(0.05, 0.15)
    val step = 0.2

    while (currentCount < countAsDouble) {
        // keep hue the same, modify saturation and lightness by even step size based on # of colours
        // add random offset to generate new palettes off of the same seeds
        // S and L are both decimals from 0 to 1 so use modulo to bound
        val newSaturation = (hslSeed[1] + step * currentCount / countAsDouble + randomOffset) % 1
        val newLightness = (hslSeed[2] + step * currentCount / countAsDouble + randomOffset) % 1

        // create new rgb colour
        val newRgbColour = hslToRgb(arrayOf(hslSeed[0], newSaturation, newLightness))

        // add to array
        rgbColors.add(newRgbColour)
        currentCount++
    }

    // get colour names and convert from rgb colours into Palette.Color objects
    val result: MutableList<Palette.Color> = mutableListOf();
    for (color in 0..<count) {
        val currColor = rgbColors[color];
        val rgb = Palette.Rgb(currColor[0], currColor[1], currColor[2]);
        val hexValue = rgbToHex(currColor);
        val hex = Palette.Hex("#$hexValue", hexValue);
        val name = fetchColorName(hex);
        result.add(Palette.Color(hex, rgb, name));
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
