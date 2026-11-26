package com.aca.people.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.GetUserUseCase
import com.aca.people.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val user: User) : DetailUiState
    object NotFound : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val uuid = savedStateHandle.get<String>("uuid")
        if (uuid.isNullOrBlank()) {
            _uiState.value = DetailUiState.Error("Identificador de usuario no proporcionado o inválido")
        } else {
            loadUser(uuid)
        }
    }

    private fun loadUser(uuid: String) {
        viewModelScope.launch {
            val user = getUserUseCase(uuid)
            if (user != null) {
                _uiState.value = DetailUiState.Success(user)
            } else {
                _uiState.value = DetailUiState.NotFound
            }
        }
    }
}