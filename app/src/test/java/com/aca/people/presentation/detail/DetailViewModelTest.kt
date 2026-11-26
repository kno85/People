package com.aca.people.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.DeleteNoteUseCase
import com.aca.people.domain.GetUserUseCase
import com.aca.people.domain.IsFavoriteUseCase
import com.aca.people.domain.ObserveUserNoteUseCase
import com.aca.people.domain.SaveNoteUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.User
import com.aca.people.domain.UserLogin
import com.aca.people.domain.UserNoteUseCases
import com.aca.people.domain.model.UserNote
import com.aca.people.presentation.navigation.ConstantAppScreenName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
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
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getUserUseCase: GetUserUseCase

    @Mock
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase

    @Mock
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @Mock
    private lateinit var observeUserNoteUseCase: ObserveUserNoteUseCase

    @Mock
    private lateinit var saveNoteUseCase: SaveNoteUseCase

    @Mock
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle): DetailViewModel {
        return DetailViewModel(
            savedStateHandle = savedStateHandle,
            getUserUseCase = getUserUseCase,
            isFavoriteUseCase = isFavoriteUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            noteUseCases = UserNoteUseCases(
                observeUserNote = observeUserNoteUseCase,
                saveNote = saveNoteUseCase,
                deleteNote = deleteNoteUseCase
            )
        )
    }

    @Test
    fun `when uuid is missing in savedStateHandle, state is Error`() = runTest(testDispatcher) {
        val viewModel = createViewModel(SavedStateHandle())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DetailUiState.Error)
    }

    @Test
    fun `when uuid is blank in savedStateHandle, state is Error`() = runTest(testDispatcher) {
        val viewModel = createViewModel(SavedStateHandle(mapOf(ConstantAppScreenName.UUID_ARG to "   ")))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DetailUiState.Error)
    }

    @Test
    fun `when user is not found, state is NotFound`() = runTest(testDispatcher) {
        val uuid = "123"
        whenever(getUserUseCase.invoke(uuid)).thenReturn(null)

        val viewModel = createViewModel(SavedStateHandle(mapOf(ConstantAppScreenName.UUID_ARG to uuid)))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `when user is found, state reacts to favorite and note updates`() = runTest(testDispatcher) {
        val uuid = "123"
        val mockUser = User(
            gender = null,
            name = null,
            location = null,
            email = "test@test.com",
            login = UserLogin(uuid = uuid),
            registered = null,
            phone = null,
            cell = null,
            picture = null,
            nat = null
        )
        val favoriteFlow = MutableStateFlow(false)
        val noteFlow = MutableStateFlow<UserNote?>(null)
        whenever(getUserUseCase.invoke(uuid)).thenReturn(mockUser)
        whenever(isFavoriteUseCase.invoke(uuid)).thenReturn(favoriteFlow)
        whenever(observeUserNoteUseCase.invoke(uuid)).thenReturn(noteFlow)

        val viewModel = createViewModel(SavedStateHandle(mapOf(ConstantAppScreenName.UUID_ARG to uuid)))
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.uiState.value as DetailUiState.Success
        assertEquals(mockUser, initialState.user)
        assertFalse(initialState.isFavorite)
        assertNull(initialState.note)

        val newNote = UserNote(userUuid = uuid, content = "Call tomorrow", updatedAtEpochMillis = 1000L)
        favoriteFlow.value = true
        noteFlow.value = newNote
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = viewModel.uiState.value as DetailUiState.Success
        assertTrue(updatedState.isFavorite)
        assertEquals(newNote, updatedState.note)

        viewModel.onToggleFavorite()
        viewModel.onSaveNote("Call tomorrow")
        viewModel.onDeleteNote()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(toggleFavoriteUseCase).invoke(mockUser)
        verify(saveNoteUseCase).invoke(uuid, "Call tomorrow")
        verify(deleteNoteUseCase).invoke(uuid)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `when getUserUseCase throws exception, state is Error`() = runTest(testDispatcher) {
        val uuid = "123"
        whenever(getUserUseCase.invoke(uuid)).thenThrow(IllegalStateException("Storage failure"))

        val viewModel = createViewModel(SavedStateHandle(mapOf(ConstantAppScreenName.UUID_ARG to uuid)))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
        assertEquals("Storage failure", (state as DetailUiState.Error).message)
    }
}
