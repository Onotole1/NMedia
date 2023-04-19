package ru.netology.nmedia.model

data class AuthModelState (
    val loginAndPassError: Boolean = false,
    val connectionError: Boolean = false,
    val successfulRequest: Boolean = false
)
