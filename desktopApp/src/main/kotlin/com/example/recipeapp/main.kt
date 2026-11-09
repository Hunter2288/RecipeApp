package com.example.recipeapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import eu.anifantakis.lib.ksafe.KSafe

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RecipeApp",
    ) {
        App(KSafe())
    }
}
