package com.example.recipeapp.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.example.recipeapp.auth.IsLoggedInUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingState(
    val currentPage: Int = 0,
    val pageCount: Int = 4,
) {
    val isLast: Boolean get() = currentPage == pageCount - 1
}

sealed interface OnboardingIntent {
    data object Next : OnboardingIntent
    data object Skip : OnboardingIntent
    data class PageChanged(val page: Int) : OnboardingIntent
}

sealed interface OnboardingEffect {
    data object NavigateToAuth : OnboardingEffect
    data object NavigateToHome : OnboardingEffect
}

class OnboardingViewModel(
    private val setOnboardingCompleted: SetOnboardingCompletedUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Onboarding")

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged ->
                _state.update { it.copy(currentPage = intent.page) }

            OnboardingIntent.Next ->
                if (_state.value.isLast) finish("finished") else
                    _state.update { it.copy(currentPage = it.currentPage + 1) }

            OnboardingIntent.Skip -> finish("skipped on page ${_state.value.currentPage + 1}")
        }
    }

    private fun finish(reason: String) {
        viewModelScope.launch {
            setOnboardingCompleted(true)
            log.i { "Onboarding $reason" }
            if (isLoggedIn()) {
                _effects.send(OnboardingEffect.NavigateToHome)
            } else {
                _effects.send(OnboardingEffect.NavigateToAuth)
            }
        }
    }
}
