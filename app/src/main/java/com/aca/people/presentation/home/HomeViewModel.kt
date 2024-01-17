package com.aca.people.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: UserUseCase
) : ViewModel() {

    private val _usersState: MutableStateFlow<PagingData<User>> = MutableStateFlow(value = PagingData.empty())
    val usersState: StateFlow<PagingData<User>>
        get() = searchText
        .combine(_usersState){text, userstate ->
            if (text.isBlank()){
                userstate
            }else{
                userstate.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
            .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _usersState.value
        )

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        onEvent(HomeEvent.GetHome)
    }
    fun onSearchTextChange(text:String){
        _searchText.value = text
    }



    private fun onEvent(event: HomeEvent) {
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
