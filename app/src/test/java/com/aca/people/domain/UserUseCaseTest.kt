package com.aca.people.domain

import androidx.paging.PagingData
import com.aca.people.CoroutinesTestExtension
import com.aca.people.data.remote.getMockUserList
import com.aca.people.repository.UserRepository
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, CoroutinesTestExtension::class)
class UserUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userUseCase: UserUseCase

    @BeforeEach
    fun setUp() {
        userUseCase = UserUseCase(userRepository)
    }

    @Test
    fun `execute should return paging data from repository`() = runTest(testDispatcher) {
        // Arrange

        val testData = PagingData.from(mapToDomain(getMockUserList()))
        whenever(userRepository.getUsers()).thenReturn(flowOf(testData))

        // Act
        val result: Flow<PagingData<User>> = userUseCase.execute(Unit)

        // Assert
        assertEquals(testData, result.single())
    }
}
