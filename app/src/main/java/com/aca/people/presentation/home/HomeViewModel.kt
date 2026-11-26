package com.aca.people.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed interface HomeUiEvent {
    object NavigateToExit : HomeUiEvent
    data class NavigateToDetail(val uuid: String) : HomeUiEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: UserUseCase
) : ViewModel() {

    private val _uiEvents = Channel<HomeUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val baseUsersFlow: Flow<PagingData<User>> =
        getUsersUseCase().cachedIn(viewModelScope)

    val usersState: Flow<PagingData<User>> = combine(
        searchText,
        baseUsersFlow
    ) { text, pagingData ->
        if (text.isBlank()) {
            pagingData
        } else {
            val searchStr = text.trim().lowercase()
            pagingData.filter { user -> matchesSearch(user, searchStr) }
        }
    }.cachedIn(viewModelScope)

    fun onBackButtonPressed() {
        _showExitDialog.value = true
    }

    fun onDismissDialog() {
        _showExitDialog.value = false
    }

    fun onExitConfirmed() {
        _showExitDialog.value = false
        _uiEvents.trySend(HomeUiEvent.NavigateToExit)
    }

    fun onUserClicked(user: User) {
        val uuid = user.login?.uuid
        if (!uuid.isNullOrBlank()) {
            _uiEvents.trySend(HomeUiEvent.NavigateToDetail(uuid))
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun matchesSearch(user: User, query: String): Boolean {
        val first = user.name?.first.orEmpty()
        val last = user.name?.last.orEmpty()
        val fullName = "$first $last".lowercase()
        val fullNameNoSpace = "$first$last".lowercase()
        val email = user.email?.lowercase().orEmpty()
        return fullName.contains(query) || fullNameNoSpace.contains(query) || email.contains(query)
    }
}

