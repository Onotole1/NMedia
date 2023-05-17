package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.repository.TokenRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val apiService: PostsApiService,
    private val tokenRepository: TokenRepository
) {

    private val _authStateFlow = MutableStateFlow(AuthState())
    val authStateFlow = _authStateFlow

    init {
        val id = tokenRepository.userId
        val token = tokenRepository.token

        if (id == 0L || token == null) {
            _authStateFlow.value = AuthState()
            tokenRepository.clear()
        } else {
            _authStateFlow.value = AuthState(id = id, token = token)
        }
    }

    @Synchronized
    fun clear() {
        _authStateFlow.value = AuthState()
        tokenRepository.clear()
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id = id, token = token)
        tokenRepository.userId = id
        tokenRepository.token = token
    }
}

data class AuthState(val id: Long = 0L, val token: String? = null)