package com.aca.people.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.aca.people.domain.MatchesUserQueryUseCase
import com.aca.people.domain.ObserveAllNotesUseCase
import com.aca.people.domain.ObserveFavoriteUuidsUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase
import com.aca.people.domain.model.CategoryFilter
import com.aca.people.domain.model.GenderFilter
import com.aca.people.domain.model.UserFilterCriteria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiEvent {
    object NavigateToExit : HomeUiEvent
    data class NavigateToDetail(val uuid: String) : HomeUiEvent
}

data class HomeUserMarks(
    val favoriteUuids: Set<String> = emptySet(),
    val notedUuids: Set<String> = emptySet()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: UserUseCase,
    observeFavoriteUuidsUseCase: ObserveFavoriteUuidsUseCase,
    observeAllNotesUseCase: ObserveAllNotesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val matchesUserQueryUseCase: MatchesUserQueryUseCase
) : ViewModel() {

    private val _uiEvents = Channel<HomeUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _categoryFilter = MutableStateFlow(CategoryFilter.ALL)
    private val _genderFilter = MutableStateFlow(GenderFilter.ALL)

    val filterCriteria: StateFlow<UserFilterCriteria> = combine(
        _searchText,
        _categoryFilter,
        _genderFilter
    ) { query, category, gender ->
        UserFilterCriteria(query = query, category = category, gender = gender)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UserFilterCriteria()
    )

    private val favoriteUuidsSource: Flow<Set<String>> = observeFavoriteUuidsUseCase()
    private val notedUuidsSource: Flow<Set<String>> = observeAllNotesUseCase().map { it.keys }

    val favoriteUuids: StateFlow<Set<String>> = favoriteUuidsSource
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptySet()
        )

    val notedUuids: StateFlow<Set<String>> = notedUuidsSource
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptySet()
        )

    private val baseUsersFlow: Flow<PagingData<User>> =
        getUsersUseCase().cachedIn(viewModelScope)

    private val debouncedCriteria: Flow<UserFilterCriteria> = combine(
        searchText.map { it.trim() }.distinctUntilChanged().debounce(SEARCH_DEBOUNCE_MILLIS),
        _categoryFilter,
        _genderFilter
    ) { query, category, gender ->
        UserFilterCriteria(query = query, category = category, gender = gender)
    }.distinctUntilChanged()

    val usersState: Flow<PagingData<User>> = combine(
        baseUsersFlow,
        debouncedCriteria,
        favoriteUuidsSource,
        notedUuidsSource
    ) { pagingData, criteria, favUuids, noteUuids ->
        if (!criteria.hasActiveFilters) {
            pagingData
        } else {
            pagingData.filter { user ->
                matchesUserQueryUseCase(
                    user = user,
                    criteria = criteria,
                    favoriteUuids = favUuids,
                    notedUuids = noteUuids
                )
            }
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

    fun onToggleFavorite(user: User) {
        viewModelScope.launch {
            toggleFavoriteUseCase(user)
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onClearSearch() {
        _searchText.value = ""
    }

    fun onCategoryFilterSelected(category: CategoryFilter) {
        _categoryFilter.value = category
    }

    fun onGenderFilterSelected(gender: GenderFilter) {
        _genderFilter.value = gender
    }

    fun onClearFilters() {
        _searchText.value = ""
        _categoryFilter.value = CategoryFilter.ALL
        _genderFilter.value = GenderFilter.ALL
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5000L
        const val SEARCH_DEBOUNCE_MILLIS = 250L
    }
}
