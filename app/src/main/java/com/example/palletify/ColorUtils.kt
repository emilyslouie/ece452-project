package com.example.palletify

import androidx.compose.ui.graphics.Color
import com.example.palletify.data.Palette
import com.example.palletify.data.fetchColorName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

object ColorUtils {
    fun Color.toPaletteColorString(): String {
        // Convert the floating-point values to 0-255 and then to a hex string
        val redValue = (this.red * 255).toInt()
        val greenValue = (this.green * 255).toInt()
        val blueValue = (this.blue * 255).toInt()

        return String.format("#%02X%02X%02X", redValue, greenValue, blueValue)
    }

    fun Color.toPaletteColor(): Palette.Color {
        // Convert the floating-point values to 0-255 and then to a hex string
        val redValue = (this.red * 255).toInt()
        val greenValue = (this.green * 255).toInt()
        val blueValue = (this.blue * 255).toInt()

        val rgb = Palette.Rgb(redValue, greenValue, blueValue);
        val hexValue =
            String.format("#%02X%02X%02X", redValue, greenValue, blueValue)
        val hex = Palette.Hex(hexValue, hexValue.substring(1));
        val name = fetchColorName(hex);

        return Palette.Color(hex, rgb, name);
    }

    fun hexToComposeColor(hex: String): Color {
        return Color(android.graphics.Color.parseColor(hex));
    }

    fun contrastRatio(foregroundColor: Color, backgroundColor: Color): Double {
        // https://www.w3.org/TR/WCAG21/#dfn-contrast-ratio
        val fgLuminance = calculateLuminance(foregroundColor)
        val bgLuminance = calculateLuminance(backgroundColor)

        val lighter = max(fgLuminance, bgLuminance)
        val darker = min(fgLuminance, bgLuminance)

        return (lighter + 0.05) / (darker + 0.05)
    }

    // https://www.w3.org/TR/WCAG21/#dfn-relative-luminance
    private fun calculateLuminance(color: Color): Double {
        val red = color.red.toDouble()
        val green = color.green.toDouble()
        val blue = color.blue.toDouble()

        val r = if (red <= 0.04045) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
        val g = if (green <= 0.04045) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
        val b = if (blue <= 0.04045) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    // https://stackoverflow.com/questions/5623838/rgb-to-hex-and-hex-to-rgb
    fun hexToRgb(hex: String): Array<Int> {
        var formattedHex = hex;
        if (hex[0] == '#') {
            formattedHex = hex.substring(1);
        }
        val bigint: Int = formattedHex.toLong(16).toInt()
        val r: Int = bigint shr 16 and 255
        val g: Int = bigint shr 8 and 255
        val b: Int = bigint and 255
        return arrayOf(r, g, b);
    }

    private fun componentToHex(c: Int): String {
        var hex = c.toString(16);
        if (hex.length == 1) {
            hex = "0$hex"
        }
        return hex;
    }

    fun rgbToHex(rgb: Array<Int>): String {
        return componentToHex(rgb[0]).uppercase() + componentToHex(rgb[1]).uppercase() + componentToHex(
            rgb[2]
        ).uppercase();
    }

    fun Array<Int>.toRgb(): Palette.Rgb {
        require(size == 3) { "Array must contain exactly three elements for Rgb conversion" }
        return Palette.Rgb(get(0), get(1), get(2))
    }


    // https://gist.github.com/mjackson/5311256
    fun rgbToHsl(rgb: Array<Int>): Array<Double> {
        val r = rgb[0] / 255.0;
        val g = rgb[1] / 255.0;
        val b = rgb[2] / 255.0;

        val max = max(max(r, g), b);
        val min = min(min(r, g), b);
        var h = 0.0;
        var s = 0.0;
        val l = (max + min) / 2.0;

        // if max == min, then it is achromatic
        if (max != min) {
            val d = max - min;
            s = if (l > 0.5) d / (2.0 - max - min) else d / (max + min);

            when (max) {
                r -> h = (g - b) / d + (if (g < b) 6 else 0);
                g -> h = (b - r) / d + 2;
                b -> h = (r - g) / d + 4;
            }

            h /= 6;
        }

        return arrayOf(h, s, l);
    }

    private fun hue2rgb(p: Double, q: Double, t: Double): Double {
        var tResult = t;
        if (tResult < 0.0) tResult += 1.0;
        if (tResult > 1.0) tResult -= 1.0;
        if (tResult < 0.16667)
            return p + (q - p) * 6.0 * tResult;
        if (tResult < 0.5)
            return q;
        if (tResult < 0.6666)
            return p + (q - p) * (2.0 / 3.0 - tResult) * 6.0;
        return p;
    }

    fun hslToRgb(hsl: Array<Double>): Array<Int> {
        var r = 0.0
        var g = 0.0
        var b = 0.0

        val h = hsl[0];
        val s = hsl[1];
        val l = hsl[2];

        // if s == 0, then the color is achromatic
        if (s == 0.0) {
            r = l;
            g = l;
            b = l;
        } else {
            val q = if (l < 0.5) l * (1.0 + s) else l + s - l * s;
            val p = 2.0 * l - q;

            r = hue2rgb(p, q, h + (1.0 / 3.0));
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - (1.0 / 3.0));
        }

        return arrayOf((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt());
    }

    fun interpolate(
        startValue: Double,
        endValue: Double,
        stepNumber: Int,
        lastStepNumber: Int
    ): Double {
        return (endValue - startValue) * (stepNumber.toDouble() / lastStepNumber.toDouble()) + startValue
    }


    // Used to get 2 lightness values that are significantly different from the seed lightness
    fun getDarkerAndLighter(lightness: Double, offset: Double): Array<Double> {
        val result = arrayOf(lightness, lightness);

        // if it is really dark, then we want to choose two lighter values
        if (1.0 - lightness <= 0.2) {
            result[0] = (lightness / 3.0) + offset; // 1/3 of the original lightness
            result[1] = ((lightness / 3.0) * 2.0) + offset; // 2/3 of the original lightness
        } else if (lightness <= 0.2) { // if it is really light, then we want to choose two darker values
            val difference = 1.0 - lightness;
            result[0] = (difference / 3.0) + lightness + offset;
            result[1] = ((difference / 3.0) * 2.0) + lightness + offset;
        } else { // otherwise get one darker and one lighter
            result[0] = if (lightness >= 0.5) lightness - (2.0 * offset) else lightness + offset;
            result[1] = if (lightness <= 0.5) lightness + (2.0 * offset) else lightness - offset;
        }
        return result;
    }

    fun getAnalogousHue(hue: Double, offset: Double): Array<Double> {
        val result = arrayOf(hue, hue);
        if (1.0 - hue <= 0.1) {
            result[0] = hue - offset;
            result[1] = hue - (2.0 * offset);
        } else if (hue <= 0.1) {
            result[0] = hue + offset;
            result[1] = hue + (2.0 * offset);
        } else {
            result[0] = hue + offset;
            result[1] = hue - offset;
        }
        return result;
    }

    fun getRandomValueInGradient(
        firstValue: Double,
        secondValue: Double,
        step: Int,
        numOfResults: Int
    ): Double {
        val randVal = interpolate(firstValue, secondValue, step, numOfResults);
        val lowerBound = if (randVal == 1.0) 0.0 else 0.01;
        val upperBound = if (randVal > 0.9) 1 - randVal else 0.1;
        return randVal + Random.nextDouble(lowerBound, upperBound);
    }
}