package com.aca.people.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aca.people.utils.Constants
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.User
import com.aca.people.repository.paging.UserPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Data/UserRepositoryImpl.kt
class UserRepositoryImp @Inject constructor(

    private val remoteDataSource: UserRemoteDataSource
):UserRepository {
    override suspend fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.MAX_PAGE_SIZE, prefetchDistance = 2),
            pagingSourceFactory = {
                UserPagingSource(remoteDataSource)
            }
        ).flow    }
}