package com.aca.people.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.ObserveAllNotesUseCase
import com.aca.people.domain.ObserveFavoritesUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.User
import com.aca.people.domain.model.FavoriteUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    object Empty : FavoritesUiState
    data class Success(
        val favorites: List<FavoriteUser>,
        val notedUuids: Set<String> = emptySet()
    ) : FavoritesUiState
}

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val uuid: String) : FavoritesUiEvent
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    observeAllNotesUseCase: ObserveAllNotesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiEvents = Channel<FavoritesUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    val uiState: StateFlow<FavoritesUiState> = combine(
        observeFavoritesUseCase(),
        observeAllNotesUseCase()
    ) { favorites, notes ->
        if (favorites.isEmpty()) {
            FavoritesUiState.Empty
        } else {
            FavoritesUiState.Success(
                favorites = favorites,
                notedUuids = notes.keys
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = FavoritesUiState.Loading
    )

    fun onUserClicked(user: User) {
        val uuid = user.login?.uuid
        if (!uuid.isNullOrBlank()) {
            _uiEvents.trySend(FavoritesUiEvent.NavigateToDetail(uuid))
        }
    }

    fun onToggleFavorite(user: User) {
        viewModelScope.launch {
            toggleFavoriteUseCase(user)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
