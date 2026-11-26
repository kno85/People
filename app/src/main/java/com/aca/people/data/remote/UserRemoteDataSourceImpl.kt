package com.aca.people.data.remote

import com.aca.people.network.ApiService
import com.aca.people.network.User
import com.aca.people.utils.Constants
import retrofit2.Response
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val api: ApiService
) : UserRemoteDataSource {

    override suspend fun getUsers(
        seed: String,
        pageNumber: Int
    ): Response<ResponseDto<List<User>>> {
        return api.getUsers(seed, pageNumber, Constants.MAX_PAGE_SIZE)
    }
}
