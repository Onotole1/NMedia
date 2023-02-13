package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 17,
    var likedByMe: Boolean = false,
    var shares: Int = 1099999,
    val videoUrl: String? = null
)