package com.example.palletify

sealed class Screens (val screen: String) {
    data object Capture:Screens("capture")
    data object Upload:Screens("upload")
    data object Library:Screens("library")
    data object PreviewScreen:Screens("preview")
    data object GenerateScreen:Screens("generate_screens")
}