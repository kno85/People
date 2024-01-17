package com.aca.people.data.remote

import com.aca.people.network.User


interface UserRemoteDataSource {

    suspend fun getUsers(
        apiKey: String,
        pageNumber: Int
    ): ResponseDto<List<User>?>

}
