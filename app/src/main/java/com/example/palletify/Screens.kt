package com.example.palletify

sealed class Screens (val screen: String) {
    data object Home:Screens("home")
    data object Library:Screens("library")
    data object PreviewScreen:Screens("preview")
}