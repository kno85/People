package com.aca.people.repository.paging

import androidx.compose.runtime.key
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.User
import com.aca.people.mapToDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Data/UserRepositoryImpl.kt
class UserRepositoryImpl @Inject constructor(

    private val remoteDataSource: UserRemoteDataSource
):UserRepository {
    override suspend fun getUsers(page: Int, pageSize: Int): List<User> {
//        return Pager(
//            config = PagingConfig( pageSize, prefetchDistance = 2),
//            pagingSourceFactory = {
//                UserPagingSource(remoteDataSource)
//            }
//        ).flow    }
        return mapToDomain(remoteDataSource.getUsers("W6I5-ZEYE-IP3V-APR08", page, pageSize))}
}