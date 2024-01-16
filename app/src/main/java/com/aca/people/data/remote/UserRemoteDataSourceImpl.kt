package com.mmj.movieapp.data.datasource.remote

import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.network.ApiService
import com.aca.people.network.User
import javax.inject.Inject

var numberOfItems: Int = 10
class UserRemoteDataSourceImpl @Inject constructor(
    private val api: ApiService
) : UserRemoteDataSource {


    override suspend fun getUsers(apiKey: String, pageNumber: Int, pageSize: Int): List<User>? {
        return api.getUsers( apiKey,pageNumber,numberOfItems).results
    }

}