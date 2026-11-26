package com.aca.people.data.remote

import com.aca.people.network.User


interface UserRemoteDataSource {

    suspend fun getUsers(
        seed: String,
        pageNumber: Int
    ): retrofit2.Response<ResponseDto<List<User>>>

}
