package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
    }


    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideCommonOkhttp() : OkHttpClient = OkHttpClient.Builder()
        .build()

    @Singleton
    @Provides
    fun provideMediaOkhttp(): OkHttpClient = provideCommonOkhttp().newBuilder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    @Singleton
    @Provides
    fun providePostOkhttp(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = provideMediaOkhttp().newBuilder()
        .addInterceptor(logging)
        .build()

    @Singleton
    @Provides
    fun providePostRetrofit(
        okHttpClient: OkHttpClient
    ) : Retrofit     = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
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
        retrofit: Retrofit
    ) :PostApiService =retrofit.create()

    @Singleton
    @Provides
    fun provideApiServiceMedia(
        retrofit: Retrofit
    ) :PostApiService =retrofit.create()


}