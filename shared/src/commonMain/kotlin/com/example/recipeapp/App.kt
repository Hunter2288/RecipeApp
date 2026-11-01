package com.example.recipeapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.recipeapp.auth.AuthContainer
import com.example.recipeapp.di.appModule
import com.example.recipeapp.home.HomeContainer
import com.example.recipeapp.onboarding.OnboardingContainer
import com.yourname.savor.ui.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

sealed interface Route
data object OnboardingRoute : Route
data object AuthRoute : Route
data object HomeRoute : Route

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        AppTheme {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val appViewModel: AppViewModel = koinViewModel()
                val backStack = remember { mutableStateListOf<Route>(appViewModel.startRoute) }

                fun replaceWith(route: Route) {
                    backStack.clear()
                    backStack.add(route)
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<OnboardingRoute> {
                            OnboardingContainer(
                                onNavigateToAuth = { replaceWith(AuthRoute) },
                                onNavigateToHome = { replaceWith(HomeRoute) }
                            )
                        }
                        entry<AuthRoute> {
                            AuthContainer(onNavigateToHome = { replaceWith(HomeRoute) })
                        }
                        entry<HomeRoute> {
                            HomeContainer(
                                onNavigateToOnboarding = { replaceWith(OnboardingRoute) },
                                onNavigateToAuth = { replaceWith(AuthRoute) }
                            )
                        }
                    },
                )
            }
        }
    }
}
