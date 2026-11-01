package com.example.recipeapp

import androidx.lifecycle.ViewModel
import com.example.recipeapp.auth.IsLoggedInUseCase
import com.example.recipeapp.onboarding.IsOnboardingCompletedUseCase

class AppViewModel(
    isOnboardingCompleted: IsOnboardingCompletedUseCase,
    isLoggedIn: IsLoggedInUseCase,
) : ViewModel() {
    val startRoute: Route = when {
        !isOnboardingCompleted() -> OnboardingRoute
        !isLoggedIn() -> AuthRoute
        else -> HomeRoute
    }
}
