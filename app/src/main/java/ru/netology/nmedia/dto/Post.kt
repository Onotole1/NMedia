package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.Attachment

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int = 17,
    var shares: Int = 1099999,
    val videoUrl: String? = null,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String,
    val type: String
)