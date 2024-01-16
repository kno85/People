package com.aca.people.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getUsers(
        @Query("key") key: String,
        @Query("page") page: Int,
        @Query("results") results: Int
    ): Results
}


