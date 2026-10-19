package com.example.recipeapp


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.yourname.savor.ui.theme.AppTheme
private const val KEY_ONBOARDING_DONE = "onboarding_completed"

@Composable
fun App() {
    val settings = remember { Settings() }
    val log = remember { Logger.withTag("App") }

    AppTheme {
        var onboardingDone by remember {
            mutableStateOf(settings.getBoolean(KEY_ONBOARDING_DONE, false))
        }

        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (!onboardingDone) {
                OnboardingScreen(onFinish = {
                    settings.putBoolean(KEY_ONBOARDING_DONE, true)
                    log.i { "Saved $KEY_ONBOARDING_DONE = true" }
                    onboardingDone = true
                })
            } else {
                HomeScreen()
            }
        }
    }
}