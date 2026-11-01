package com.example.recipeapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.yourname.savor.ui.theme.PillShape
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipeapp.shared.generated.resources.*

@Composable
fun AuthContainer(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    AuthScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun AuthScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    val submit = {
        focusManager.clearFocus()
        onIntent(AuthIntent.SignIn)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.auth_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.auth_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = { onIntent(AuthIntent.UsernameChanged(it)) },
            label = { Text(stringResource(Res.string.auth_username)) },
            singleLine = true,
            enabled = !state.isLoading,
            isError = state.usernameError != null,
            supportingText = state.usernameError?.let { { Text(stringResource(it.message())) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() },
            ),
            shape = PillShape,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
            label = { Text(stringResource(Res.string.auth_password)) },
            singleLine = true,
            enabled = !state.isLoading,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { { Text(stringResource(it.message())) } },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { onIntent(AuthIntent.TogglePasswordVisibility) }) {
                    Text(
                        stringResource(
                            if (state.isPasswordVisible) Res.string.auth_hide else Res.string.auth_show
                        )
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { submit() },
            ),
            shape = PillShape,
            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { submit() },
            enabled = !state.isLoading,
            shape = PillShape,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(Res.string.auth_sign_in), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun UsernameError.message() = when (this) {
    UsernameError.Empty -> Res.string.err_username_empty
    UsernameError.TooShort -> Res.string.err_username_short
    UsernameError.InvalidChars -> Res.string.err_username_chars
}

private fun PasswordError.message() = when (this) {
    PasswordError.Empty -> Res.string.err_password_empty
    PasswordError.TooShort -> Res.string.err_password_short
    PasswordError.NoDigit -> Res.string.err_password_digit
    PasswordError.NoLetter -> Res.string.err_password_letter
}
