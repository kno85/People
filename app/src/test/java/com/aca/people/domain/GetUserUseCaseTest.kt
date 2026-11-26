package com.aca.people.domain

import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.model.FavoriteUser
import com.aca.people.domain.repository.FavoriteRepository
import com.aca.people.repository.UserRepository
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class GetUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var favoriteRepository: FavoriteRepository

    private lateinit var getUserUseCase: GetUserUseCase

    @BeforeEach
    fun setUp() {
        getUserUseCase = GetUserUseCase(userRepository, favoriteRepository)
    }

    @Test
    fun `invoke returns user from userRepository when found`() = runTest {
        val expectedUser = mapToDomain(getMockUserList()).first()
        val uuid = requireNotNull(expectedUser.login?.uuid)
        whenever(userRepository.getUserByUuid(uuid)).thenReturn(expectedUser)

        val result = getUserUseCase(uuid)

        assertEquals(expectedUser, result)
    }

    @Test
    fun `invoke falls back to favoriteRepository when not in userRepository`() = runTest {
        val expectedUser = mapToDomain(getMockUserList()).first()
        val uuid = requireNotNull(expectedUser.login?.uuid)
        whenever(userRepository.getUserByUuid(uuid)).thenReturn(null)
        whenever(favoriteRepository.getFavoriteByUuid(uuid)).thenReturn(
            FavoriteUser(uuid = uuid, user = expectedUser, addedAtEpochMillis = 1000L)
        )

        val result = getUserUseCase(uuid)

        assertEquals(expectedUser, result)
    }

    @Test
    fun `invoke returns null when user is not in either repository`() = runTest {
        whenever(userRepository.getUserByUuid("unknown")).thenReturn(null)
        whenever(favoriteRepository.getFavoriteByUuid("unknown")).thenReturn(null)

        val result = getUserUseCase("unknown")

        assertNull(result)
    }
}
