package com.aca.people.presentation.home

import com.aca.people.CoroutinesTestExtension
import com.aca.people.domain.UserUseCase
import com.aca.people.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, CoroutinesTestExtension::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userUseCase: UserUseCase
    private lateinit var homeViewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        userUseCase = UserUseCase(userRepository)
        homeViewModel = HomeViewModel(userUseCase)
    }

    @Test
    fun `onSearchTextChange should update searchText`() = runTest(testDispatcher) {
        // Act
        homeViewModel.onSearchTextChange("John")

        // advance until coroutines complete when using StandardTestDispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("John", homeViewModel.searchText.value)
    }
}
