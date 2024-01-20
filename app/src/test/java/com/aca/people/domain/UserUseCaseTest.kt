package com.aca.people.domain

import androidx.paging.PagingData
import com.aca.people.CoroutinesTestRule
import com.aca.people.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class UserUseCaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()


    // The class under test
    private lateinit var userUseCase: UserUseCase

    // Mock dependencies
    @Mock
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userUseCase = UserUseCase(userRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
     fun `execute should return Flow of PagingData User`() = runTest {
        // Arrange
        val user1 = User("1",UserName("Title1","first1","last1"),null, "sample@example1.com", null, null, "654444441", null, null, null)
        val user2 = User("2",UserName("Title2","first2","last2"),null, "sample@example2.com", null, null, "654444442", null, null, null)
        val user3 = User("3",UserName("Title3","first3","last3"),null, "sample@example3.com", null, null, "654444443", null, null, null)


        val testData = PagingData.from(listOf(user1, user2, user3))
        `when`(userRepository.getUsers()).thenReturn(flowOf(testData))

        // Act
        val result: Flow<PagingData<User>> = userUseCase.execute(Unit)

        // Assert
        assertEquals(testData, result.single())
    }
}
