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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface HomeUiEvent {
    object NavigateToExit : HomeUiEvent
    data class NavigateToDetail(val uuid: String) : HomeUiEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: UserUseCase
) : ViewModel() {

    // Usamos Channel.BUFFERED para evitar pérdida de eventos durante recomposiciones (cuando no hay colector)
    private val _uiEvents = Channel<HomeUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

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
        user.login?.uuid?.let { uuid ->
            _uiEvents.trySend(HomeUiEvent.NavigateToDetail(uuid))
        }
    }

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _usersPagingData: MutableStateFlow<PagingData<User>> = MutableStateFlow(PagingData.empty())

    val usersState: StateFlow<PagingData<User>> = searchText
        .combine(_usersPagingData) { text, pagingData ->
            if (text.isBlank()) {
                pagingData
            } else {
                pagingData.filter {
                    val searchStr = text.lowercase()
                    val fullName = "${it.name?.first.orEmpty()} ${it.name?.last.orEmpty()}".lowercase()
                    val fullNameNoSpace = "${it.name?.first.orEmpty()}${it.name?.last.orEmpty()}".lowercase()
                    val email = it.email?.lowercase().orEmpty()
                    fullName.contains(searchStr) || fullNameNoSpace.contains(searchStr) || email.contains(searchStr)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PagingData.empty()
        )

    init {
        getUsers()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun getUsers() {
        viewModelScope.launch {
            getUsersUseCase()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _usersPagingData.value = pagingData
                }
        }
    }
}
