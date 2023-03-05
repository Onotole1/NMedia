package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun data(): LiveData<List<Post>>
    suspend fun getAllAsync()
    suspend fun getByIdAsync(id: Long)
    suspend fun deleteByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun unLikeByIdAsync(post: Post)
    suspend fun likeByIdAsync(post: Post)
    fun shareById(id : Long)


    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }

    interface CallbackUnit {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

}