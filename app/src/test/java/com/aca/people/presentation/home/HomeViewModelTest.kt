package com.aca.people.presentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.MatchesUserQueryUseCase
import com.aca.people.domain.ObserveAllNotesUseCase
import com.aca.people.domain.ObserveFavoriteUuidsUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.User
import com.aca.people.domain.UserLogin
import com.aca.people.domain.UserUseCase
import com.aca.people.domain.model.CategoryFilter
import com.aca.people.domain.model.GenderFilter
import com.aca.people.domain.model.UserNote
import com.aca.people.domain.repository.FavoriteRepository
import com.aca.people.domain.repository.NoteRepository
import com.aca.people.repository.UserRepository
import com.aca.people.utils.mapToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var favoriteRepository: FavoriteRepository

    @Mock
    private lateinit var noteRepository: NoteRepository

    private val favoriteUuidsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val notesFlow = MutableStateFlow<Map<String, UserNote>>(emptyMap())
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
        whenever(favoriteRepository.observeFavoriteUuids()).thenReturn(favoriteUuidsFlow)
        whenever(noteRepository.observeAllNotes()).thenReturn(notesFlow)
        homeViewModel = HomeViewModel(
            getUsersUseCase = UserUseCase(userRepository),
            observeFavoriteUuidsUseCase = ObserveFavoriteUuidsUseCase(favoriteRepository),
            observeAllNotesUseCase = ObserveAllNotesUseCase(noteRepository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(favoriteRepository),
            matchesUserQueryUseCase = MatchesUserQueryUseCase()
        )
    }

    @AfterEach
    fun tearDown() {
        homeViewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchTextChange updates searchText and filters usersState`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("  Alice  ")
        assertEquals("  Alice  ", homeViewModel.searchText.value)
        testScheduler.advanceTimeBy(300L)

        val filteredUsers = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filteredUsers.size)
        assertEquals("Alice", filteredUsers.first().name?.first)
    }

    @Test
    fun `onSearchTextChange filters usersState by email`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("bob.johnson@example.ca")
        testScheduler.advanceTimeBy(300L)

        val filteredUsers = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filteredUsers.size)
        assertEquals("Bob", filteredUsers.first().name?.first)
    }

    @Test
    fun `onSearchTextChange filters usersState by phone`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("5550301")
        testScheduler.advanceTimeBy(300L)

        val filteredUsers = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filteredUsers.size)
        assertEquals("Carol", filteredUsers.first().name?.first)
    }

    @Test
    fun `onClearSearch resets searchText to empty`() = runTest(testDispatcher) {
        homeViewModel.onSearchTextChange("Alice")
        homeViewModel.onClearSearch()
        assertEquals("", homeViewModel.searchText.value)
    }

    @Test
    fun `category filter FAVORITES returns only favorited users`() = runTest(testDispatcher) {
        val bobUuid = requireNotNull(sampleUsers[1].login?.uuid)
        favoriteUuidsFlow.value = setOf(bobUuid)

        homeViewModel.onCategoryFilterSelected(CategoryFilter.FAVORITES)
        val filtered = homeViewModel.usersState.asSnapshot()

        assertEquals(1, filtered.size)
        assertEquals("Bob", filtered.first().name?.first)
    }

    @Test
    fun `category filter WITH_NOTES combined with search returns matching noted users`() = runTest(testDispatcher) {
        val aliceUuid = requireNotNull(sampleUsers[0].login?.uuid)
        val carolUuid = requireNotNull(sampleUsers[2].login?.uuid)
        notesFlow.value = mapOf(
            aliceUuid to UserNote(aliceUuid, "Note A", 100L),
            carolUuid to UserNote(carolUuid, "Note C", 200L)
        )

        homeViewModel.onCategoryFilterSelected(CategoryFilter.WITH_NOTES)
        homeViewModel.onSearchTextChange("carol")
        testScheduler.advanceTimeBy(300L)

        val filtered = homeViewModel.usersState.asSnapshot()
        assertEquals(1, filtered.size)
        assertEquals("Carol", filtered.first().name?.first)
    }

    @Test
    fun `gender filter and onClearFilters update filtered results`() = runTest(testDispatcher) {
        homeViewModel.onGenderFilterSelected(GenderFilter.MALE)
        val males = homeViewModel.usersState.asSnapshot()
        assertEquals(1, males.size)
        assertEquals("Bob", males.first().name?.first)

        homeViewModel.onClearFilters()
        testScheduler.advanceTimeBy(300L)
        val all = homeViewModel.usersState.asSnapshot()
        assertEquals(3, all.size)
    }

    @Test
    fun `favoriteUuids updates reactively and onToggleFavorite delegates to repository`() = runTest(testDispatcher) {
        val alice = sampleUsers.first()
        val aliceUuid = requireNotNull(alice.login?.uuid)

        homeViewModel.favoriteUuids.test {
            assertEquals(emptySet<String>(), awaitItem())
            favoriteUuidsFlow.value = setOf(aliceUuid)
            assertEquals(setOf(aliceUuid), awaitItem())
        }

        homeViewModel.onToggleFavorite(alice)
        verify(favoriteRepository).toggleFavorite(alice)
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
