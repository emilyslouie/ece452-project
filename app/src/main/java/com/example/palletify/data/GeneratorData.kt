package com.example.palletify.data


import com.example.palletify.ColorUtils.getAnalogousHue
import com.example.palletify.ColorUtils.getDarkerAndLighter
import com.example.palletify.ColorUtils.getRandomValueInGradient
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
        GenerationMode.GRADIENT -> generateGradientPalette(seeds, count);
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
    var rSum = 0
    var gSum = 0
    var bSum = 0

    for (color in seeds) {
        val rgb = color.rgb;
        rSum += rgb.r
        gSum += rgb.g
        bSum += rgb.b
        rgbColors.add(arrayOf(rgb.r, rgb.g, rgb.b))
    }
    // set seed to averaged rgb values from all seeds
    val averageRgbSeed = arrayOf(rSum / seeds.count(), gSum / seeds.count(), bSum / seeds.count())
    val hslSeed = rgbToHsl(averageRgbSeed);

    val start = rgbColors.count()
    // use random step size to generate new palettes off of the same seeds
    // randomize sign of step so we aren't always increasing/decreasing S and L
    val step = Random.nextDouble(0.2, 1.0)

    for (i in start..<count) {
        // keep hue the same, modify saturation and lightness by even step size based on # of colours
        // S and L are both decimals from 0 to 1 so use modulo to bound
        val newSaturation =
            (hslSeed[1] + step * i.toDouble() / count.toDouble()) % 1
        val newLightness =
            (hslSeed[2] + step * i.toDouble() / count.toDouble()) % 1

        // create new rgb colour
        val newRgbColour = hslToRgb(arrayOf(hslSeed[0], newSaturation, newLightness))

        // add to array
        rgbColors.add(newRgbColour)
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

fun generateGradientPalette(
    seeds: MutableSet<Palette.Color>,
    count: Int
): MutableList<Palette.Color> {
    val result: MutableList<Palette.Color> = mutableListOf();
    val list = seeds.toMutableList();

    if (seeds.size == 1) {
        val seed = list[0];
        val currRgb = seed.rgb;
        val hsl = rgbToHsl(arrayOf(currRgb.r, currRgb.g, currRgb.b));
        // if we are adding a single new colour due to "adding an extra colour in the palette",
        // we want to get an analogous colour to the seed colour instead of generating a random colour
        if (count == 1) {
            val analogousHue = getAnalogousHue(hsl[0], 0.05);
            val analogousRgb = hslToRgb(arrayOf(analogousHue[0], hsl[1], hsl[2]));
            val rgb = Palette.Rgb(analogousRgb[0], analogousRgb[1], analogousRgb[2]);
            val analogousHex = rgbToHex(arrayOf(analogousRgb[0], analogousRgb[1], analogousRgb[2]));
            val hex = Palette.Hex("#$analogousHex", analogousHex);
            val name = fetchColorName(hex);
            result.add(Palette.Color(hex, rgb, name));
        } else {
            // otherwise we want to get a random colour with the same s, l and generate colours in between the two
            // get the hue on the opposite side of the spectrum
            val oppositeHue = 1.0 - hsl[0];
            result.add(seed);
            for (step in 1..<count) {
                val newHue = getRandomValueInGradient(hsl[0], oppositeHue, step, count - 1);
                val newRgb = hslToRgb(arrayOf(newHue, hsl[1], hsl[2]));
                val newHex = rgbToHex(arrayOf(newRgb[0], newRgb[1], newRgb[2]))
                val rgb = Palette.Rgb(newRgb[0], newRgb[1], newRgb[2]);
                val hex = Palette.Hex("#$newHex", newHex);
                val name = fetchColorName(hex);
                result.add(Palette.Color(hex, rgb, name));
            }
        }
    } else {
        // interpolate between the two or more locked colours
        // ideally we would know what order the locked colours are in so that we can generate a gradient in the available slots between each locked colour
        // in this case, if there are two seeds we will just assume that the remaining slots are to be filled with colours in between
        // if there are more than 2 seeds, we will assume there can be 2 colours generated between each one until we reach our specified count
        // this works because we currently have a known max size of 6
        result.add(list[0])
        for (currSeed in 0..<seeds.size - 1) {
            val firstColor = list[currSeed];
            val firstHsl = rgbToHsl(arrayOf(firstColor.rgb.r, firstColor.rgb.g, firstColor.rgb.b));

            val secondColor = list[currSeed + 1];
            val secondHsl =
                rgbToHsl(arrayOf(secondColor.rgb.r, secondColor.rgb.g, secondColor.rgb.b));

            val numOfResults = if (seeds.size == 2) count else 2;

            for (step in 1..numOfResults) {
                // if we reached one before the max size, then stop generating new ones
                if (result.size == count - 1) {
                    break;
                }
                val newHue = getRandomValueInGradient(firstHsl[0], secondHsl[0], step, numOfResults)
                val newSat = getRandomValueInGradient(firstHsl[1], secondHsl[1], step, numOfResults)
                val newLight =
                    getRandomValueInGradient(firstHsl[2], secondHsl[2], step, numOfResults)
                val newRgb = hslToRgb(arrayOf(newHue, newSat, newLight));
                val newHex = rgbToHex(arrayOf(newRgb[0], newRgb[1], newRgb[2]))
                val rgb = Palette.Rgb(newRgb[0], newRgb[1], newRgb[2]);
                val hex = Palette.Hex("#$newHex", newHex);
                val name = fetchColorName(hex);
                result.add(Palette.Color(hex, rgb, name));
            }
            result.add(secondColor);
        }

    }

    return result;
}
