package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

private val mediaRetrofit = named("MediaRetrofit")

private val postRetrofit = named("PostRetrofit")

private val mediaOkHttp = named("MediaOkHttp")

private val postOkHttp = named("PostOkHttp")

val apiModule = module {

    factory {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    factory {
        val appAuth = get<AppAuth>()
        Interceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                    .apply { return@Interceptor chain.proceed(this) }
            }
            return@Interceptor chain.proceed(chain.request())
        }
    }


    factory {
        OkHttpClient.Builder()
            .build()
    }

    single(qualifier = mediaOkHttp) {
        val commonOkhttp = get<OkHttpClient>()

        commonOkhttp.newBuilder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }

    single(postOkHttp) {
        val logging = get<HttpLoggingInterceptor>()

        val mediaOkhttp = get<OkHttpClient>(mediaOkHttp)

        mediaOkhttp.newBuilder()
            .addInterceptor(logging)
            .build()
    }

    single(postRetrofit) {
        val postOkHttpClient = get<OkHttpClient>(postOkHttp)

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(postOkHttpClient)
            .build()
    }

    single(mediaRetrofit) {
        val mediaOkHttpClient = get<OkHttpClient>(mediaOkHttp)

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(mediaOkHttpClient)
            .build()
    }

    single {
        val postRetrofit = get<Retrofit>(postRetrofit)
        postRetrofit.create<PostsApiService>()
    }

    single {
        val mediaRetrofit = get<Retrofit>(mediaRetrofit)

        mediaRetrofit.create<SMediaService>()
    }
}