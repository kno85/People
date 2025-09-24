package com.aca.people.repository.paging

import androidx.paging.PagingSource
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class) // Add this
class UserPagingSourceTest {

    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource

    @Test
    fun `load returns page on success`() = runTest {
        // Arrange
        val responseDto = createMockResponseDto(getMockUserList())

        // Mock the remote call
        whenever(remoteDataSource.getUsers(any(), any())).thenReturn(responseDto)

        val pagingSource = UserPagingSource(remoteDataSource)

        val expected = mapToDomain(getMockUserList())
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Assert
        when (result) {
            is PagingSource.LoadResult.Page -> assertEquals(expected, result.data)
            is PagingSource.LoadResult.Error -> throw result.throwable
            is PagingSource.LoadResult.Invalid<*, *> -> null
        }
    }

}
