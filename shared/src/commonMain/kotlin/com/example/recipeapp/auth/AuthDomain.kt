package com.example.recipeapp.auth

import com.russhwolf.settings.Settings

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun setLoggedIn(value: Boolean)
    fun getUsername(): String
    fun setUsername(value: String)
}

class AuthRepositoryImpl(private val settings: Settings) : AuthRepository {
    override fun isLoggedIn(): Boolean = settings.getBoolean(KEY_LOGGED_IN, false)
    override fun setLoggedIn(value: Boolean) = settings.putBoolean(KEY_LOGGED_IN, value)
    override fun getUsername(): String = settings.getString(KEY_USERNAME, "")
    override fun setUsername(value: String) = settings.putString(KEY_USERNAME, value)

    private companion object {
        const val KEY_LOGGED_IN = "is_logged_in"
        const val KEY_USERNAME = "logged_in_username"
    }
}

class IsLoggedInUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.isLoggedIn()
}

class SetLoggedInUseCase(private val repository: AuthRepository) {
    operator fun invoke(isLoggedIn: Boolean, username: String = "") {
        repository.setLoggedIn(isLoggedIn)
        if (isLoggedIn) {
            repository.setUsername(username)
        } else {
            repository.setUsername("")
        }
    }
}

class GetUsernameUseCase(private val repository: AuthRepository) {
    operator fun invoke(): String = repository.getUsername()
}
