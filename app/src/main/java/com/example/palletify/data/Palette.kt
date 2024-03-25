package com.example.palletify.data

import kotlinx.serialization.Serializable

class Palette {

    @Serializable
    data class Color(
        val hex: Hex,
        val rgb: Rgb,
    ) {
        // Override default equals operator since it uses referential equality by default (compare addresses)
        // We want structural equality (compare values of all members in struct)
        override fun equals(other: Any?): Boolean {
            return when (other) {
                is Color -> {
                    this.hex == other.hex &&
                            this.rgb == other.rgb
//                            && this.name == other.name
                }

                else -> false
            }
        }

        // When overriding equals, we must also override the default hashCode implementation
        // Build a hash code by hashing the values of each of the members
        override fun hashCode(): Int {
            var result = hex.hashCode()
            result = 31 * result + rgb.hashCode()
//            result = 31 * result + name.hashCode()
            return result
        }
    }

    @Serializable
    data class Rgb(
        val r: Int,
        val g: Int,
        val b: Int,
    )


    @Serializable
    data class Hex(
        val value: String,
        val clean: String,
    )

    @Serializable
    data class Name(
        val value: String,
    )


}