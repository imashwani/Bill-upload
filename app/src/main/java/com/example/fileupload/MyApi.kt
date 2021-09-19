package com.example.fileupload

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface MyApi {

    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part data: MultipartBody.Part,
        @HeaderMap headerMap: Map<String, String>
    ): Call<UploadResponse>

    companion object {
        val BASE_URL = "http://ocrv1.herokuapp.com/"
        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        operator fun invoke(): MyApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}