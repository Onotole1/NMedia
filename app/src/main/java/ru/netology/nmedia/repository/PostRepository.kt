package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAll() : List<Post>
    fun getById(id : Long) : Post
    fun likeById(id : Long) :Post
    fun shareById(id : Long)
    fun save(post: Post)
    fun deleteById(id: Long)
}