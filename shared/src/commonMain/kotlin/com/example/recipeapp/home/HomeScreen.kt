package com.example.recipeapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.example.recipeapp.auth.GetUsernameUseCase
import com.example.recipeapp.auth.SetLoggedInUseCase
import com.example.recipeapp.onboarding.SetOnboardingCompletedUseCase
import com.yourname.savor.ui.theme.PillShape
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipeapp.shared.generated.resources.*

data class HomeState(
    val username: String = "",
)

sealed interface HomeIntent {
    data object RepeatOnboarding : HomeIntent
    data object SignOut : HomeIntent
}

sealed interface HomeEffect {
    data object NavigateToOnboarding : HomeEffect
    data object NavigateToAuth : HomeEffect
}

class HomeViewModel(
    private val setOnboardingCompleted: SetOnboardingCompletedUseCase,
    private val setLoggedIn: SetLoggedInUseCase,
    getUsername: GetUsernameUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Home")

    private val _state = MutableStateFlow(HomeState(username = getUsername()))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RepeatOnboarding -> repeatOnboarding()
            HomeIntent.SignOut -> signOut()
        }
    }

    private fun repeatOnboarding() {
        viewModelScope.launch {
            setOnboardingCompleted(false)
            log.i { "Reset onboarding for re-taking" }
            _effects.send(HomeEffect.NavigateToOnboarding)
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            setLoggedIn(isLoggedIn = false)
            log.i { "User signed out" }
            _effects.send(HomeEffect.NavigateToAuth)
        }
    }
}

@Composable
fun HomeContainer(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToOnboarding -> onNavigateToOnboarding()
                HomeEffect.NavigateToAuth -> onNavigateToAuth()
            }
        }
    }

    HomeScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (state.username.isNotBlank()) {
                    "${stringResource(Res.string.home_welcome)}, ${state.username}!"
                } else {
                    stringResource(Res.string.home_welcome)
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.home_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "🍳 🥗 🍝",
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = { onIntent(HomeIntent.RepeatOnboarding) },
                shape = PillShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(Res.string.repeat_onboarding),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            OutlinedButton(
                onClick = { onIntent(HomeIntent.SignOut) },
                shape = PillShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(Res.string.sign_out),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
