package com.aca.people.presentation.home

import com.aca.people.CoroutinesTestRule
import com.aca.people.domain.UserUseCase
import com.aca.people.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val rule = CoroutinesTestRule()
    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userUseCase: UserUseCase

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userUseCase = UserUseCase(userRepository)
        homeViewModel = HomeViewModel(userUseCase)
    }

    @Test
    fun `onSearchTextChange should update searchText`() = run {
        homeViewModel.onSearchTextChange("John")
        // Assert
        assertEquals("John", homeViewModel.searchText.value)
    }

}
