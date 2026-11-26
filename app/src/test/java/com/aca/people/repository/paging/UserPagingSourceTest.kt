package com.aca.people.repository.paging

import androidx.paging.PagingSource
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class UserPagingSourceTest {

    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource

    @Test
    fun `load returns page on success`() = runTest {
        val responseDto = Response.success(createMockResponseDto(getMockUserList()))
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val expected = mapToDomain(getMockUserList())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expected, pageResult.data)
        assertEquals(null, pageResult.prevKey)
        assertEquals(2, pageResult.nextKey)
    }

    @Test
    fun `load returns error on http exception`() = runTest {
        val errorResponse = Response.error<com.aca.people.data.remote.ResponseDto<List<com.aca.people.network.User>>>(
            500, "{\"error\":\"Internal Server Error\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(errorResponse)

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
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
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Error::class.java, result)
        val errorResult = result as PagingSource.LoadResult.Error
        assertInstanceOf(IOException::class.java, errorResult.throwable)
        assertEquals("Network Error", errorResult.throwable.message)
    }

    @Test
    fun `load returns page with null nextKey on empty response`() = runTest {
        val emptyResponseDto = createMockResponseDto(emptyList())
        val responseDto = Response.success(emptyResponseDto)
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

        val pagingSource = UserPagingSource(remoteDataSource) { }
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(emptyList<com.aca.people.domain.User>(), pageResult.data)
        assertEquals(null, pageResult.prevKey)
        assertEquals(null, pageResult.nextKey) // Confirming end of pagination
    }
}