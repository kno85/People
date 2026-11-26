package com.aca.people.repository

import androidx.paging.PagingSource
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.getMockUserList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import retrofit2.Response
import com.aca.people.utils.mapToDomain

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun `getUserByUuid returns user after cache is populated`() = runTest {
        // Arrange
        val mockUserList = mapToDomain(getMockUserList())
        val expectedUser = mockUserList.first()
        val targetUuid = expectedUser.login?.uuid!!
        
        // Use reflection to access the private cache map just for the purpose of simulating the PagingSource callback,
        // because collecting a Pager flow in a pure JUnit test without Compose/Android lifecycle is complex and flaky.
        // We will simulate the callback that the repository provides to UserPagingSource.
        val cacheField = repository.javaClass.getDeclaredField("cachedUsers")
        cacheField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val cache = cacheField.get(repository) as java.util.concurrent.ConcurrentHashMap<String, com.aca.people.domain.User>
        
        // Simulate what the lambda in repository.getUsers() does when PagingSource loads a page
        mockUserList.forEach { user ->
            user.login?.uuid?.let { cache[it] = user }
        }

        // Act
        val result = repository.getUserByUuid(targetUuid)

        // Assert
        assertEquals(expectedUser, result)
    }
}