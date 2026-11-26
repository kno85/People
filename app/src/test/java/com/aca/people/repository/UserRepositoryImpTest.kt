package com.aca.people.repository

import androidx.paging.testing.asSnapshot
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExtendWith(MockitoExtension::class)
class UserRepositoryImpTest {

    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource

    private lateinit var repository: UserRepositoryImp

    @BeforeEach
    fun setUp() {
        repository = UserRepositoryImp(remoteDataSource)
    }

    @Test
    fun `getUserByUuid returns null when user is not in cache`() = runTest {
        val result = repository.getUserByUuid("non-existent-uuid")
        assertNull(result)
    }

    @Test
    fun `getUserByUuid returns null when uuid is blank`() = runTest {
        val result = repository.getUserByUuid("   ")
        assertNull(result)
    }

    @Test
    fun `getUserByUuid returns user after getUsers populates cache via PagingSource`() = runTest {
        val networkUsers = getMockUserList()
        val expectedUsers = mapToDomain(networkUsers)
        val expectedUser = expectedUsers.first()
        val targetUuid = requireNotNull(expectedUser.login?.uuid)

        whenever(remoteDataSource.getUsers(any(), eq(1)))
            .thenReturn(Response.success(createMockResponseDto(networkUsers)))
        whenever(remoteDataSource.getUsers(any(), eq(2)))
            .thenReturn(Response.success(createMockResponseDto(emptyList())))

        val loadedItems = repository.getUsers().asSnapshot()
        val cachedUser = repository.getUserByUuid(targetUuid)

        assertEquals(expectedUsers, loadedItems)
        assertEquals(expectedUser, cachedUser)
    }
}
