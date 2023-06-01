package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import javax.inject.Singleton
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Qualifier


@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
    }


    @Provides
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideAuthInterceptor(appAuth: AppAuth): Interceptor = Interceptor { chain ->
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


    @Singleton
    @Provides
    fun provideCommonOkhttp() : OkHttpClient = OkHttpClient.Builder()
        .build()

    @MediaOkHttp
    @Singleton
    @Provides
    fun provideMediaOkhttp(): OkHttpClient = provideCommonOkhttp().newBuilder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    @PostOkHttp
    @Singleton
    @Provides
    fun providePostOkhttp(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = provideMediaOkhttp().newBuilder()
        .addInterceptor(logging)
        .build()

    @Singleton
    @Provides
    @PostRetrofit
    fun providePostRetrofit(
        okHttpClient: OkHttpClient
    ) : Retrofit     = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @MediaRetrofit
    fun provideMediaRetrofit(
        okHttpClient: OkHttpClient
    ) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiServicePost(
        @PostRetrofit
        retrofit: Retrofit
    ): PostsApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideApiServiceMedia(
        @MediaRetrofit
        retrofit: Retrofit
    ): SMediaService = retrofit.create()

    @Qualifier
    private annotation class MediaRetrofit

    @Qualifier
    private annotation class PostRetrofit

    @Qualifier
    private annotation class MediaOkHttp

    @Qualifier
    private annotation class PostOkHttp

}