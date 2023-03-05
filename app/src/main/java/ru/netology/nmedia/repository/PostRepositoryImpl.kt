package ru.netology.nmedia.repository

import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import java.lang.Exception
import java.net.ConnectException

class PostRepositoryImpl (private val postDao: PostDao): PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun data() = postDao.getAll().map { it.map(PostEntity::toDto) }


    override suspend fun getAllAsync() {
        val response = PostsApi.retrofitService.getAll()
        if (!response.isSuccessful) throw RuntimeException("api error")
        response.body() ?: throw RuntimeException("body is null")
        postDao.insert(response.body()!!.map { it -> PostEntity.fromDto(it) })
    }


    override fun shareById(id: Long) {
    }

    override suspend fun deleteByIdAsync(id: Long) {
        val response = PostsApi.retrofitService.deleteById(id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.removeById(id)
    }

    override suspend fun saveAsync(post: Post) {
        val response = PostsApi.retrofitService.save(post)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun unLikeByIdAsync(post: Post) {
        val response = PostsApi.retrofitService.unlikeById(post.id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun likeByIdAsync(post: Post) {
        val response = PostsApi.retrofitService.likeById(post.id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun getByIdAsync(id: Long) {
        val response = PostsApi.retrofitService.getById(id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.getById(body.id)
    }
}