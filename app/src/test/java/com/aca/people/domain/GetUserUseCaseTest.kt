package com.aca.people.domain

import com.aca.people.data.remote.getMockUserList
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

    private lateinit var getUserUseCase: GetUserUseCase

    @BeforeEach
    fun setUp() {
        getUserUseCase = GetUserUseCase(userRepository)
    }

    @Test
    fun `invoke returns user from repository when found`() = runTest {
        val expectedUser = mapToDomain(getMockUserList()).first()
        val uuid = requireNotNull(expectedUser.login?.uuid)
        whenever(userRepository.getUserByUuid(uuid)).thenReturn(expectedUser)

        val result = getUserUseCase(uuid)

        assertEquals(expectedUser, result)
    }

    @Test
    fun `invoke returns null when user is not found`() = runTest {
        whenever(userRepository.getUserByUuid("unknown")).thenReturn(null)

        val result = getUserUseCase("unknown")

        assertNull(result)
    }
}
