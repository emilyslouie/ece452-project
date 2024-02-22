package com.example.palletify.data


import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request


const val MAX_NO_OF_WORDS = 10
const val SCORE_INCREASE = 20

// Set with all the words for the Game
val allWords: Set<String> =
    setOf(
        "animal",
        "auto",
        "anecdote",
        "alphabet",
        "all",
        "awesome",
        "arise",
        "balloon",
        "basket",
        "bench",
        "best",
        "birthday",
        "book",
        "briefcase",
        "camera",
        "camping",
        "candle",
        "cat",
        "cauliflower",
        "chat",
        "children",
        "class",
        "classic",
        "classroom",
        "coffee",
        "colorful",
        "cookie",
        "creative",
        "cruise",
        "dance",
        "daytime",
        "dinosaur",
        "doorknob",
        "dine",
        "dream",
        "dusk",
        "eating",
        "elephant",
        "emerald",
        "eerie",
        "electric",
        "finish",
        "flowers",
        "follow",
        "fox",
        "frame",
        "free",
        "frequent",
        "funnel",
        "green",
        "guitar",
        "grocery",
        "glass",
        "great",
        "giggle",
        "haircut",
        "half",
        "homemade",
        "happen",
        "honey",
        "hurry",
        "hundred",
        "ice",
        "igloo",
        "invest",
        "invite",
        "icon",
        "introduce",
        "joke",
        "jovial",
        "journal",
        "jump",
        "join",
        "kangaroo",
        "keyboard",
        "kitchen",
        "koala",
        "kind",
        "kaleidoscope",
        "landscape",
        "late",
        "laugh",
        "learning",
        "lemon",
        "letter",
        "lily",
        "magazine",
        "marine",
        "marshmallow",
        "maze",
        "meditate",
        "melody",
        "minute",
        "monument",
        "moon",
        "motorcycle",
        "mountain",
        "music",
        "north",
        "nose",
        "night",
        "name",
        "never",
        "negotiate",
        "number",
        "opposite",
        "octopus",
        "oak",
        "order",
        "open",
        "polar",
        "pack",
        "painting",
        "person",
        "picnic",
        "pillow",
        "pizza",
        "podcast",
        "presentation",
        "puppy",
        "puzzle",
        "recipe",
        "release",
        "restaurant",
        "revolve",
        "rewind",
        "room",
        "run",
        "secret",
        "seed",
        "ship",
        "shirt",
        "should",
        "small",
        "spaceship",
        "stargazing",
        "skill",
        "street",
        "style",
        "sunrise",
        "taxi",
        "tidy",
        "timer",
        "together",
        "tooth",
        "tourist",
        "travel",
        "truck",
        "under",
        "useful",
        "unicorn",
        "unique",
        "uplift",
        "uniform",
        "vase",
        "violin",
        "visitor",
        "vision",
        "volume",
        "view",
        "walrus",
        "wander",
        "world",
        "winter",
        "well",
        "whirlwind",
        "x-ray",
        "xylophone",
        "yoga",
        "yogurt",
        "yoyo",
        "you",
        "year",
        "yummy",
        "zebra",
        "zigzag",
        "zoology",
        "zone",
        "zeal"
    )

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

private val jsonBuilder = Json { ignoreUnknownKeys = true }

fun fetchPalette(
    seed: String,
    mode: String = "monochrome",
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
//    val hsl: Hsl,
//    val hsv: Hsv,
    val name: Name,
//    val cmyk: Cmyk,
//    val xyz: XYZ,
//    val image: Images,
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
    val r: Int,
    val g: Int,
    val b: Int,
    val value: String,
)

//@Serializable
//data class Hsl(
//    val fraction: Fraction3,
//    val h: Int,
//    val s: Int,
//    val l: Int,
//    val value: String,
//)

//@Serializable
//data class Hsv(
//    val fraction: Fraction3,
//    val value: String,
//    val h: Int,
//    val s: Int,
//    val v: Int,
//)

//@Serializable
//data class Cmyk(
//    val fraction: Fraction4,
//    val value: String,
//    val c: Int,
//    val m: Int,
//    val y: Int,
//    val k: Int
//)

//@Serializable
//data class XYZ(
//    val fraction: Fraction3,
//    val value: String,
//    val x: Int,
//    val y: Int,
//    val z: Int
//)

@Serializable
data class RgBFraction(
    val r: Float,
    val g: Float,
    val b: Float,
)

//@Serializable
//data class Fraction4(
//    val a: Float,
//    val b: Float,
//    val c: Float,
//    val d: Float
//)

@Serializable
data class Name(
    val value: String,
//    val closestNamedHex: String,
//    val exactNameMatch: Boolean,
//    val distance: Int,
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
