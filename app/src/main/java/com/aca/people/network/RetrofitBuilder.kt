package com.aca.people.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BASE_URL = "https://randomuser.me/"
val okHttpClient = HttpLoggingInterceptor().run {
    level = HttpLoggingInterceptor.Level.BODY
    OkHttpClient.Builder().addInterceptor(this).build()
}

// Create Retrofit instance
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

