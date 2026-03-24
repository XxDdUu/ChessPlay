package com.sky.chessplay.data.remote

import com.sky.chessplay.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val authInterceptor = AuthInterceptor {
        null
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
