package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.netology.nmedia.dto.Media

interface SMediaService {

    @Multipart
    @POST("media")
    suspend fun uploadPhoto(@Part file: MultipartBody.Part): Response<Media>

}