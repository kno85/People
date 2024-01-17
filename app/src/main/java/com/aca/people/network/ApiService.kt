package com.aca.people.network

import com.aca.people.data.remote.ResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getUsers(
        @Query("key") key: String,
        @Query("page") page: Int,
        @Query("results") results: Int
    ): ResponseDto<List<User>?>
}

