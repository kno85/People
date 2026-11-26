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
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
class UserRepositoryImp @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {
    
    // In-memory cache for loaded users to support master-detail flow without Room yet
    // Uses ConcurrentHashMap to prevent ConcurrentModificationException when PagingSource writes from IO threads
    private val cachedUsers = ConcurrentHashMap<String, User>()

    override suspend fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.MAX_PAGE_SIZE, prefetchDistance = 2),
            pagingSourceFactory = {
                UserPagingSource(remoteDataSource) { users -> 
                    // Populate cache as pages load using UUID
                    users.forEach { user -> 
                        user.login?.uuid?.let { cachedUsers[it] = user } 
                    }
                }
            }
        ).flow    
    }

    override suspend fun getUserByUuid(uuid: String): User? {
        return cachedUsers[uuid]
    }
}