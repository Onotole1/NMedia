package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>,
                response: Response<List<Post>>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }

        })
    }

    override fun shareById(id: Long) {
    }

    override fun deleteByIdAsync(id: Long, callback: PostRepository.CallbackUnit<Unit>) {
        PostsApi.retrofitService.deleteById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(ConnectException("Connection is lost"))
            }

        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()} \n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("Connection is lost"))
            }

        })
    }

    override fun unLikeByIdAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.unlikeById(post.id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("Connection is lost"))
            }
        })
    }

    override fun likeByIdAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.likeById(post.id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("Connection is lost"))
            }

        })
    }

    override fun getByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.getById(id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException("${response.message()}\n${response.code()}"))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(ConnectException("Connection is lost"))
            }
        })
    }
}