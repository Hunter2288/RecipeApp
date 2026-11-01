package com.example.recipeapp.auth

enum class UsernameError { Empty, TooShort, InvalidChars }
enum class PasswordError { Empty, TooShort, NoDigit, NoLetter }

class ValidateUsernameUseCase {
    operator fun invoke(value: String): UsernameError? = when {
        value.isBlank() -> UsernameError.Empty
        value.length < 3 -> UsernameError.TooShort
        !value.all { it.isLetterOrDigit() || it == '_' || it == '.' } -> UsernameError.InvalidChars
        else -> null
    }
}

class ValidatePasswordUseCase {
    operator fun invoke(value: String): PasswordError? = when {
        value.isEmpty() -> PasswordError.Empty
        value.length < 6 -> PasswordError.TooShort
        value.none { it.isLetter() } -> PasswordError.NoLetter
        value.none { it.isDigit() } -> PasswordError.NoDigit
        else -> null
    }
}
