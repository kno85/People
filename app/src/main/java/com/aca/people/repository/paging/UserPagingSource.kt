package com.aca.people.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.utils.Constants
import com.aca.people.utils.mapToDomain
import retrofit2.HttpException
import java.io.IOException


class UserPagingSource(
    private val remoteDataSource: UserRemoteDataSource,
    private val onPageLoaded: (List<com.aca.people.domain.User>) -> Unit
) : PagingSource<Int, com.aca.people.domain.User>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, com.aca.people.domain.User> {
        return try {
            val currentPage = params.key ?: 1
            val response = remoteDataSource.getUsers(
                seed = Constants.API_KEY,
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
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, com.aca.people.domain.User>): Int? {
        return state.anchorPosition
    }

}