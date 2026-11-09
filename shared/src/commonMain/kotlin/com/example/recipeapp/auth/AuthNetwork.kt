package com.example.recipeapp.auth

import eu.anifantakis.lib.ksafe.KSafe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(val username: String, val password: String, val expiresInMins: Int = 30)

@Serializable
data class RefreshRequest(val refreshToken: String, val expiresInMins: Int = 30)

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val id: Int = 0,
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
)

@Serializable
private data class ApiError(val message: String = "Authentication failed")

class SecureTokenStore(private val storage: KSafe) {
    suspend fun accessToken(): String? = storage.get(ACCESS_TOKEN, "").ifBlank { null }
    suspend fun refreshToken(): String? = storage.get(REFRESH_TOKEN, "").ifBlank { null }
    suspend fun save(tokens: AuthTokens) {
        storage.put(ACCESS_TOKEN, tokens.accessToken)
        storage.put(REFRESH_TOKEN, tokens.refreshToken)
    }
    suspend fun clear() {
        storage.delete(ACCESS_TOKEN)
        storage.delete(REFRESH_TOKEN)
    }

    private companion object {
        const val ACCESS_TOKEN = "auth_access_token"
        const val REFRESH_TOKEN = "auth_refresh_token"
    }
}

private const val API_URL = "https://dummyjson.com/auth/"

fun createAuthHttpClient(tokens: SecureTokenStore): HttpClient = HttpClient {
    expectSuccess = false
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Auth) {
        bearer {
            loadTokens {
                val access = tokens.accessToken() ?: return@loadTokens null
                val refresh = tokens.refreshToken() ?: return@loadTokens null
                BearerTokens(access, refresh)
            }
            refreshTokens {
                val refresh = tokens.refreshToken() ?: return@refreshTokens null
                val response = client.post("${API_URL}refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshRequest(refresh))
                }
                if (!response.status.isSuccess()) return@refreshTokens null
                val updated = response.body<AuthTokens>()
                tokens.save(updated)
                BearerTokens(updated.accessToken, updated.refreshToken)
            }
        }
    }
}

sealed interface AuthResult {
    data class Success(val tokens: AuthTokens) : AuthResult
    data class Failure(val message: String) : AuthResult
}

class AuthRepositoryNetwork(
    private val client: HttpClient,
    private val tokenStore: SecureTokenStore,
) {
    suspend fun signIn(username: String, password: String): AuthResult = try {
        val response = client.post("${API_URL}login") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }
        if (response.status.isSuccess()) {
            val tokens = response.body<AuthTokens>()
            tokenStore.save(tokens)
            AuthResult.Success(tokens)
        } else {
            val error = runCatching { Json.decodeFromString<ApiError>(response.bodyAsText()) }.getOrNull()
            AuthResult.Failure(error?.message ?: "Sign in failed (${response.status.value})")
        }
    } catch (_: Exception) {
        AuthResult.Failure("Could not connect. Check your internet connection and try again.")
    }
}

class SignInUseCase(private val repository: AuthRepositoryNetwork) {
    suspend operator fun invoke(username: String, password: String): AuthResult =
        repository.signIn(username, password)
}
