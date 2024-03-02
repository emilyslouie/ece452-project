package com.example.palletify.data

import kotlinx.serialization.Serializable

class Palette {
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

}