package com.aca.people.presentation.home

import app.cash.turbine.test
import com.aca.people.CoroutinesTestExtension
import com.aca.people.domain.User
import com.aca.people.domain.UserLogin
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
        homeViewModel.onSearchTextChange("John")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("John", homeViewModel.searchText.value)
    }

    @Test
    fun `onUserClicked should emit NavigateToDetail event with UUID`() = runTest(testDispatcher) {
        val uuid = "1234-5678"
        val user = User(
            gender = null,
            name = null,
            location = null,
            email = null,
            login = UserLogin(uuid = uuid),
            registered = null,
            phone = null,
            cell = null,
            picture = null,
            nat = null
        )

        homeViewModel.uiEvents.test {
            homeViewModel.onUserClicked(user)
            assertEquals(HomeUiEvent.NavigateToDetail(uuid), awaitItem())
        }
    }

    @Test
    fun `onExitConfirmed should emit NavigateToExit event`() = runTest(testDispatcher) {
        homeViewModel.uiEvents.test {
            homeViewModel.onExitConfirmed()
            assertEquals(HomeUiEvent.NavigateToExit, awaitItem())
        }
    }
}
