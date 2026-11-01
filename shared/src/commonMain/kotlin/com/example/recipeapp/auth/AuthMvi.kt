package com.example.recipeapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val username: String = "",
    val password: String = "",
    val usernameError: UsernameError? = null,
    val passwordError: PasswordError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface AuthIntent {
    data class UsernameChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object SignIn : AuthIntent
}

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
}

class AuthViewModel(
    private val validateUsername: ValidateUsernameUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val setLoggedIn: SetLoggedInUseCase,
) : ViewModel() {

    private val log = Logger.withTag("Auth")

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UsernameChanged ->
                _state.update { it.copy(username = intent.value, usernameError = null) }

            is AuthIntent.PasswordChanged ->
                _state.update { it.copy(password = intent.value, passwordError = null) }

            AuthIntent.TogglePasswordVisibility ->
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

            AuthIntent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        val s = _state.value
        if (s.isLoading) return

        val usernameError = validateUsername(s.username)
        val passwordError = validatePassword(s.password)
        if (usernameError != null || passwordError != null) {
            _state.update { it.copy(usernameError = usernameError, passwordError = passwordError) }
            log.d { "Validation failed: $usernameError / $passwordError" }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(800) // імітація запиту до сервера
            log.i { "Signed in as ${s.username}" }
            setLoggedIn(isLoggedIn = true, username = s.username)
            _state.update { it.copy(isLoading = false) }
            _effects.send(AuthEffect.NavigateToHome)
        }
    }
}
