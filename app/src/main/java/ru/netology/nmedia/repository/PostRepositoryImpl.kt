package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.SMediaService
import ru.netology.nmedia.auth.AuthState
import java.io.IOException
import java.util.concurrent.TimeUnit
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.PhotoModel
import java.lang.Exception
import java.net.ConnectException
import java.util.concurrent.CancellationException
import ru.netology.nmedia.api.PostsApiService
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostsApiService,
    private val mediaApiService: SMediaService) : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override val data = postDao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000L)
                val response = apiService.getNewer(id)

                val posts = response.body().orEmpty()
                postDao.insert(posts.toEntity())
                emit(posts.size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    override suspend fun getAllAsync() {
        val response = apiService.getAll()
        if (!response.isSuccessful) throw RuntimeException("api error")
        response.body() ?: throw RuntimeException("body is null")
        //set isRead to 1
        postDao.insert(response.body()!!.map { it -> PostEntity.fromDto(it) })
        //set isRead to 1
        postDao.readNewPost()
    }


    override fun shareById(id: Long) {
    }

    override suspend fun uploadPhoto(uploadedMedia: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", uploadedMedia.file.name, uploadedMedia.file.asRequestBody()
            )

            val response = mediaApiService.uploadPhoto(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun signIn(login: String, pass: String): AuthState {
        val response = apiService.updateUser(login, pass)

        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }

        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun deleteByIdAsync(id: Long) {
        val response = apiService.deleteById(id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.removeById(id)
    }

    override suspend fun saveAsync(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = uploadPhoto(upload)
            val response = apiService.save(
                post.copy(
                    attachment = Attachment(media.id, AttachmentType.IMAGE)
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(photo: PhotoModel): Media {
        val response = mediaApiService.uploadPhoto(
            MultipartBody.Part.createFormData("file", photo.file!!.name, photo.file.asRequestBody())
        )

        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun unLikeByIdAsync(post: Post) {
        val response = apiService.unlikeById(post.id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun likeByIdAsync(post: Post) {
        val response = apiService.likeById(post.id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun getByIdAsync(id: Long) {
        val response = apiService.getById(id)
        if (!response.isSuccessful) throw RuntimeException("api error")
        val body = response.body() ?: throw RuntimeException("body is null")
        postDao.getById(body.id)
    }
}