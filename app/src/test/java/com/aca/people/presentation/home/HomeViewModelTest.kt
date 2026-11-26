package com.aca.people.presentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.User
import com.aca.people.domain.UserLogin
import com.aca.people.domain.UserUseCase
import com.aca.people.repository.UserRepository
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var homeViewModel: HomeViewModel
    private val sampleUsers = mapToDomain(getMockUserList())

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val pagingSourceFactory = sampleUsers.asPagingSourceFactory()
        val pagerFlow = Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = pagingSourceFactory
        ).flow
        whenever(userRepository.getUsers()).thenReturn(pagerFlow)
        homeViewModel = HomeViewModel(UserUseCase(userRepository))
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchTextChange updates searchText and filters usersState`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("Alice")
        assertEquals("Alice", homeViewModel.searchText.value)

        val filteredUsers = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filteredUsers.size)
        assertEquals("Alice", filteredUsers.first().name?.first)
        homeViewModel.viewModelScope.cancel()
    }

    @Test
    fun `onSearchTextChange filters usersState by email`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("bob.johnson@example.ca")

        val filteredUsers = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filteredUsers.size)
        assertEquals("Bob", filteredUsers.first().name?.first)
        homeViewModel.viewModelScope.cancel()
    }

    @Test
    fun `onUserClicked emits NavigateToDetail event with UUID`() = runTest(testDispatcher) {
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
    fun `onUserClicked ignores user with blank UUID`() = runTest(testDispatcher) {
        val user = User(
            gender = null,
            name = null,
            location = null,
            email = null,
            login = UserLogin(uuid = "   "),
            registered = null,
            phone = null,
            cell = null,
            picture = null,
            nat = null
        )

        homeViewModel.uiEvents.test {
            homeViewModel.onUserClicked(user)
            expectNoEvents()
        }
    }

    @Test
    fun `onExitConfirmed hides dialog and emits NavigateToExit event`() = runTest(testDispatcher) {
        homeViewModel.onBackButtonPressed()
        assertTrue(homeViewModel.showExitDialog.value)

        homeViewModel.uiEvents.test {
            homeViewModel.onExitConfirmed()
            assertFalse(homeViewModel.showExitDialog.value)
            assertEquals(HomeUiEvent.NavigateToExit, awaitItem())
        }
    }

    @Test
    fun `onDismissDialog hides exit confirmation dialog`() = runTest(testDispatcher) {
        homeViewModel.onBackButtonPressed()
        assertTrue(homeViewModel.showExitDialog.value)

        homeViewModel.onDismissDialog()
        assertFalse(homeViewModel.showExitDialog.value)
    }
}
