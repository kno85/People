package com.aca.people.repository.paging

import androidx.paging.PagingSource
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class) // Add this
class UserPagingSourceTest {

    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource

    @Test
    fun `load returns page on success`() = runTest {
        // Arrange
        val responseDto = Response.success(createMockResponseDto(getMockUserList()))

        // Mock the remote call
        whenever(remoteDataSource.getUsers(anyString(), anyInt())).thenReturn(responseDto)

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
        // Asegura que el resultado es de tipo Page
        assertInstanceOf(PagingSource.LoadResult.Page::class.java, result, "El resultado debería ser PagingSource.LoadResult.Page")

        // Ahora puedes hacer un cast seguro porque ya verificaste el tipo
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expected, pageResult.data, "Los datos cargados no coinciden con los esperados")

    }

}
