package com.aca.people.repository.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aca.people.data.remote.ResponseDto
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.User
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExtendWith(MockitoExtension::class)
class UserPagingSourceTest {

    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource

    @Test
    fun `load returns page on success and invokes onPageLoaded`() = runTest {
        val responseDto = Response.success(createMockResponseDto(getMockUserList()))
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

        var callbackUsers: List<User>? = null
        val pagingSource = UserPagingSource(remoteDataSource) { callbackUsers = it }
        val expected = mapToDomain(getMockUserList())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expected, pageResult.data)
        assertEquals(expected, callbackUsers)
        assertEquals(null, pageResult.prevKey)
        assertEquals(2, pageResult.nextKey)
    }

    @Test
    fun `load returns page 2 with prevKey 1 and nextKey 3`() = runTest {
        val responseDto = Response.success(createMockResponseDto(getMockUserList()))
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

        val pagingSource = UserPagingSource(remoteDataSource) { }

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = 2, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(1, pageResult.prevKey)
        assertEquals(3, pageResult.nextKey)
    }

    @Test
    fun `load returns error on http exception`() = runTest {
        val errorResponse = Response.error<ResponseDto<List<com.aca.people.network.User>>>(
            500,
            "{\"error\":\"Internal Server Error\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(errorResponse)

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Error::class.java, result)
        val errorResult = result as PagingSource.LoadResult.Error
        assertInstanceOf(HttpException::class.java, errorResult.throwable)
        assertEquals(500, (errorResult.throwable as HttpException).code())
    }

    @Test
    fun `load returns error on network exception`() = runTest {
        val exception = IOException("Network Error")
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenAnswer { throw exception }

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Error::class.java, result)
        val errorResult = result as PagingSource.LoadResult.Error
        assertInstanceOf(IOException::class.java, errorResult.throwable)
        assertEquals("Network Error", errorResult.throwable.message)
    }

    @Test
    fun `load returns error on unexpected exception`() = runTest {
        val exception = IllegalStateException("Unexpected parse error")
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenAnswer { throw exception }

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Error::class.java, result)
        val errorResult = result as PagingSource.LoadResult.Error
        assertInstanceOf(IllegalStateException::class.java, errorResult.throwable)
        assertEquals("Unexpected parse error", errorResult.throwable.message)
    }

    @Test
    fun `load returns page with null nextKey on empty response`() = runTest {
        val emptyResponseDto = createMockResponseDto(emptyList())
        val responseDto = Response.success(emptyResponseDto)
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(emptyList<User>(), pageResult.data)
        assertEquals(null, pageResult.prevKey)
        assertEquals(null, pageResult.nextKey)
    }

    @Test
    fun `getRefreshKey returns page number computed from closestPageToPosition`() {
        val pagingSource = UserPagingSource(remoteDataSource) { }
        val pageTwo = PagingSource.LoadResult.Page(
            data = mapToDomain(getMockUserList()),
            prevKey = 1,
            nextKey = 3
        )
        val state = PagingState(
            pages = listOf(pageTwo),
            anchorPosition = 1,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        assertEquals(2, pagingSource.getRefreshKey(state))
    }
}
