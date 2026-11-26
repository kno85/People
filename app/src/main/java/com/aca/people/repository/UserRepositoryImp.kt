package com.aca.people.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.User
import com.aca.people.repository.paging.UserPagingSource
import com.aca.people.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImp @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    // In-memory cache for loaded users to support master-detail flow prior to local persistence (Phase 3).
    // Cleared on new PagingSource creation (refresh) to avoid unbounded growth and stale entries.
    private val cachedUsers = ConcurrentHashMap<String, User>()

    override fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.MAX_PAGE_SIZE,
                initialLoadSize = Constants.MAX_PAGE_SIZE,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                cachedUsers.clear()
                UserPagingSource(remoteDataSource = remoteDataSource) { users ->
                    users.forEach { user ->
                        val uuid = user.login?.uuid
                        if (!uuid.isNullOrBlank()) {
                            cachedUsers[uuid] = user
                        }
                    }
                }
            }
        ).flow
    }

    override suspend fun getUserByUuid(uuid: String): User? {
        if (uuid.isBlank()) return null
        return cachedUsers[uuid]
    }
}
