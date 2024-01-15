package com.aca.people.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
val BASE_URL = "https://randomuser.me/"

// Create Retrofit instance
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


// Create ApiService instance
val apiService = retrofit.create(ApiService::class.java)

// Call the API with dynamic values
//val response = apiService.getUsers("your_api_key", 10, 1)
