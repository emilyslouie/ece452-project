package com.example.palletify.ui.preview

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object ColorUtils {

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
}