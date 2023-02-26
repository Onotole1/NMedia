package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun shareById(id : Long)

    fun getAllAsync(callback: Callback<List<Post>>)
    fun getByIdAsync(id: Long, callback: Callback<Post>)
    fun deleteByIdAsync(id: Long, callback: CallbackUnit<Unit>)
    fun saveAsync(post: Post, callback: Callback<Post>)
    fun unLikeByIdAsync(post: Post, callback: Callback<Post>)
    fun likeByIdAsync(post: Post, callback: Callback<Post>)



    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }

    interface CallbackUnit<T> {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

}