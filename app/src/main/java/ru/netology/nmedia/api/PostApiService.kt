package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

//private val logging = HttpLoggingInterceptor().apply {
//    if (BuildConfig.DEBUG) {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//}
//
//private val commonOkhttp = OkHttpClient.Builder()
//    .build()
//
//private val mediaOkhttp = commonOkhttp.newBuilder()
//    .addInterceptor(HttpLoggingInterceptor())
//    .build()
//
//private val postOkhttp = mediaOkhttp.newBuilder()
//    .addInterceptor(logging)
//    .addInterceptor { chain ->
//        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
//            chain
//                .request()
//                .newBuilder()
//                .addHeader("Authorization", token)
//                .build()
//                .apply { return@addInterceptor chain.proceed(this) }
//        }
//        return@addInterceptor chain.proceed(chain.request())
//    }
//    .build()
//
//
//
//
//private val postRetrofit = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .client(postOkhttp)
//    .build()
//
//private val mediaRetrofit = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .client(mediaOkhttp)
//    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deleteById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login : String, @Field("pass") pass : String) : Response<AuthState>
}




//object PostsApi {
//    val retrofitService: PostsApiService by lazy {
//        postRetrofit.create()
//    }
//
//    val mediaService: MediaService by lazy {
//       mediaRetrofit.create()
//    }
//}