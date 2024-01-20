package com.aca.people.repository.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aca.people.data.remote.ResponseDto
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.User
import com.aca.people.utils.mapToDomain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserPagingSourceTest {

    // The class under test
    private lateinit var userPagingSource: UserPagingSource

    // Mock dependencies
    private val remoteDataSource: UserRemoteDataSource = mockk()


    private val state = PagingState<Int, User>(
        listOf(),
        null,
        PagingConfig(10),
        0
    )

        @Before
    fun setUp() {
        userPagingSource = UserPagingSource(remoteDataSource)
    }

    @Test
    fun `load should return LoadResult Page`() = runTest {
        // Arrange


        val key = userPagingSource.getRefreshKey(state)
        val params = PagingSource.LoadParams.Refresh(key, 10, false)
        val user1 = com.aca.people.network.User("1", com.aca.people.network.UserName("Title1","first1","last1"), null, "sample@example1.com", null, null, "654444441", null, null, null)
        val user2 = com.aca.people.network.User("2", com.aca.people.network.UserName("Title2","first2","last2"), null, "sample@example2.com", null, null, "654444442", null, null, null)
        val user3 = com.aca.people.network.User("3", com.aca.people.network.UserName("Title3","first3","last3"), null, "sample@example3.com", null, null, "654444443", null, null, null)

        val listUserMock: List<com.aca.people.network.User> = listOf(user1, user2, user3)
        val responseDto: ResponseDto<List<com.aca.people.network.User>?> = ResponseDto()
        responseDto.results = listUserMock
        coEvery { remoteDataSource.getUsers(any(), any()) } returns responseDto

        // Act
        val result: PagingSource.LoadResult<Int, User> = userPagingSource.load(params)
        // Assert
        assertEquals(
            PagingSource.LoadResult.Page(
                data = mapToDomain(listUserMock),
                prevKey = null,
                nextKey = 2
            ),
            result
        )
    }

    @Test
    fun `load should return LoadResult Page with Empty Data`() = runTest {
        // Arrange
        val key = userPagingSource.getRefreshKey(state)
        val params = PagingSource.LoadParams.Refresh(key, 10, false)
        val emptyList: List<com.aca.people.network.User> = emptyList()
        val responseDto: ResponseDto<List<com.aca.people.network.User>?> = ResponseDto()
        responseDto.results = emptyList
        coEvery { remoteDataSource.getUsers(any(), any()) } returns responseDto

        // Act
        val result: PagingSource.LoadResult<Int, User> = userPagingSource.load(params)

        // Assert
        assertEquals(
            PagingSource.LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            ),
            result
        )
    }
}
