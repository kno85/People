package com.aca.people.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.User
import com.aca.people.utils.Constants
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(
    private val remoteDataSource: UserRemoteDataSource,
    private val seed: String = Constants.SEED,
    private val onPageLoaded: (List<User>) -> Unit
) : PagingSource<Int, User>() {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1
            val response = remoteDataSource.getUsers(
                seed = seed,
                pageNumber = currentPage
            )

            if (response.isSuccessful) {
                val users = response.body()?.results ?: emptyList()
                val mappedUsers = mapToDomain(users)
                onPageLoaded(mappedUsers)
                LoadResult.Page(
                    data = mappedUsers,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (users.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
