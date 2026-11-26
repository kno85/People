package com.aca.people.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.aca.people.domain.GetUserUseCase
import com.aca.people.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
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
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getUserUseCase: GetUserUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when uuid is missing in savedStateHandle, state is Error`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle()
        val viewModel = DetailViewModel(savedStateHandle, getUserUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
    }

    @Test
    fun `when user is not found, state is NotFound`() = runTest(testDispatcher) {
        val uuid = "123"
        val savedStateHandle = SavedStateHandle(mapOf("uuid" to uuid))
        whenever(getUserUseCase.invoke(any())).thenReturn(null)

        val viewModel = DetailViewModel(savedStateHandle, getUserUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `when user is found, state is Success`() = runTest(testDispatcher) {
        val uuid = "123"
        val mockUser = User(
            gender = null,
            name = null,
            location = null,
            email = "test@test.com",
            login = null,
            registered = null,
            phone = null,
            cell = null,
            picture = null,
            nat = null
        )
        val savedStateHandle = SavedStateHandle(mapOf("uuid" to uuid))
        whenever(getUserUseCase.invoke(any())).thenReturn(mockUser)

        val viewModel = DetailViewModel(savedStateHandle, getUserUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        assertEquals(mockUser, (state as DetailUiState.Success).user)
    }
}
