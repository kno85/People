package com.aca.people.data.remote

import com.aca.people.network.ApiService
import com.aca.people.network.User
import javax.inject.Inject

var numberOfItems: Int = 10
class UserRemoteDataSourceImpl @Inject constructor(
    private val api: ApiService
) : UserRemoteDataSource {

    override suspend fun getUsers(
        apiKey: String,
        pageNumber: Int
    ): ResponseDto<List<User>?> {
        return api.getUsers( apiKey,pageNumber, numberOfItems)
    }

}