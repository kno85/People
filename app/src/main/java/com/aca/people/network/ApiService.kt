package com.aca.people.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getUsers(
        @Query("key") key: String,
        @Query("results") results: Int,
        @Query("page") page: Int
    ): Results

   companion object{
   var key: String = "W6I5-ZEYE-IP3V-APR08"
   }
}