package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun shareById(id : Long)

    fun deleteByIdAsync(id: Long, callback: DeleteByIdCallback)
    fun saveAsync(post: Post, callback: SaveCallback)
    fun unLikeByIdAsync(post: Post, callback: UnLikeCallback)
    fun likeByIdAsync(post: Post, callback : LikeCallback)
    fun getByIdAsync(id: Long, callback: GetByIdCallback)
    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface SaveCallback {
        fun onError(e : Exception)
        fun onSuccess(post: Post)
    }

    interface LikeCallback {
        fun onError(e : Exception)
        fun onSuccess(post: Post)
    }

    interface UnLikeCallback {
        fun onError(e : Exception)
        fun onSuccess(post: Post)
    }

    interface GetByIdCallback {
        fun onError(e : Exception)
        fun onSuccess(post: Post)
    }

    interface DeleteByIdCallback {
        fun onError(e : Exception)
        fun onSuccess()
    }

}