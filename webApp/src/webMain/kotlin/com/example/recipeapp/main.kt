package com.example.recipeapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import eu.anifantakis.lib.ksafe.KSafe

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(KSafe())
    }
}
