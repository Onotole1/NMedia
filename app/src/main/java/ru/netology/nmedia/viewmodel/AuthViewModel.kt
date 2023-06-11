package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel(
    private val appAuth: AppAuth
) : ViewModel() {

    val state = appAuth.authStateFlow.asLiveData()

    val authorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L
}