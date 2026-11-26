package com.aca.people.presentation.favorites

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.ObserveAllNotesUseCase
import com.aca.people.domain.ObserveFavoritesUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.model.FavoriteUser
import com.aca.people.domain.model.UserNote
import com.aca.people.domain.repository.FavoriteRepository
import com.aca.people.domain.repository.NoteRepository
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
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var favoriteRepository: FavoriteRepository

    @Mock
    private lateinit var noteRepository: NoteRepository

    private val favoritesFlow = MutableStateFlow<List<FavoriteUser>>(emptyList())
    private val notesFlow = MutableStateFlow<Map<String, UserNote>>(emptyMap())
    private lateinit var viewModel: FavoritesViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(favoriteRepository.observeFavorites()).thenReturn(favoritesFlow)
        whenever(noteRepository.observeAllNotes()).thenReturn(notesFlow)
        viewModel = FavoritesViewModel(
            observeFavoritesUseCase = ObserveFavoritesUseCase(favoriteRepository),
            observeAllNotesUseCase = ObserveAllNotesUseCase(noteRepository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(favoriteRepository)
        )
    }

    @AfterEach
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState transitions reactively between Empty and Success`() = runTest(testDispatcher) {
        val alice = mapToDomain(getMockUserList()).first()
        val favoriteAlice = FavoriteUser(
            uuid = requireNotNull(alice.login?.uuid),
            user = alice,
            addedAtEpochMillis = 1000L
        )

        viewModel.uiState.test {
            assertEquals(FavoritesUiState.Empty, awaitItem())

            favoritesFlow.value = listOf(favoriteAlice)
            val successState = awaitItem()
            assertTrue(successState is FavoritesUiState.Success)
            assertEquals(listOf(favoriteAlice), (successState as FavoritesUiState.Success).favorites)
        }
    }

    @Test
    fun `onUserClicked emits NavigateToDetail event`() = runTest(testDispatcher) {
        val alice = mapToDomain(getMockUserList()).first()
        val expectedUuid = requireNotNull(alice.login?.uuid)

        viewModel.uiEvents.test {
            viewModel.onUserClicked(alice)
            assertEquals(FavoritesUiEvent.NavigateToDetail(expectedUuid), awaitItem())
        }
    }

    @Test
    fun `onToggleFavorite delegates to repository`() = runTest(testDispatcher) {
        val alice = mapToDomain(getMockUserList()).first()

        viewModel.onToggleFavorite(alice)

        verify(favoriteRepository).toggleFavorite(alice)
    }
}
