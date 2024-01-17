package com.aca.people.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: UserUseCase
) : ViewModel() {

    private val _usersState: MutableStateFlow<PagingData<User>> = MutableStateFlow(value = PagingData.empty())
    val usersState: MutableStateFlow<PagingData<User>> get() = _usersState

    init {
        onEvent(HomeEvent.GetHome)
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.GetHome -> {
                    getUsers()
                }
            }
        }
    }

    private suspend fun getUsers() {
        getUsersUseCase.execute(Unit)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _usersState.value = it
            }
    }
}

sealed class HomeEvent {
    object GetHome : HomeEvent()
}

data class HomeState(
    val users: List<User> = listOf()
)