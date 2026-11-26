package com.aca.people.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.GetUserUseCase
import com.aca.people.domain.IsFavoriteUseCase
import com.aca.people.domain.ToggleFavoriteUseCase
import com.aca.people.domain.User
import com.aca.people.domain.UserNoteUseCases
import com.aca.people.domain.model.UserNote
import com.aca.people.presentation.navigation.ConstantAppScreenName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val user: User,
        val isFavorite: Boolean = false,
        val note: UserNote? = null
    ) : DetailUiState
    object NotFound : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserUseCase: GetUserUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val noteUseCases: UserNoteUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val currentUuid: String? = savedStateHandle.get<String>(ConstantAppScreenName.UUID_ARG)

    init {
        val uuid = currentUuid
        if (uuid.isNullOrBlank()) {
            _uiState.value = DetailUiState.Error("Identificador de usuario no proporcionado o inválido")
        } else {
            loadUser(uuid)
        }
    }

    fun onToggleFavorite() {
        val currentUser = (_uiState.value as? DetailUiState.Success)?.user ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentUser)
        }
    }

    fun onSaveNote(content: String) {
        val uuid = currentUuid?.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            noteUseCases.saveNote(uuid, content)
        }
    }

    fun onDeleteNote() {
        val uuid = currentUuid?.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            noteUseCases.deleteNote(uuid)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadUser(uuid: String) {
        viewModelScope.launch {
            try {
                val user = getUserUseCase(uuid)
                if (user != null) {
                    combine(
                        isFavoriteUseCase(uuid),
                        noteUseCases.observeUserNote(uuid)
                    ) { isFavorite, note ->
                        DetailUiState.Success(
                            user = user,
                            isFavorite = isFavorite,
                            note = note
                        )
                    }.collect { state ->
                        _uiState.value = state
                    }
                } else {
                    _uiState.value = DetailUiState.NotFound
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = DetailUiState.Error(
                    exception.localizedMessage ?: "Error al cargar el usuario"
                )
            }
        }
    }
}

