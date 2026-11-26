package com.aca.people.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.GetUserUseCase
import com.aca.people.domain.User
import com.aca.people.presentation.navigation.ConstantAppScreenName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
        val uuid = savedStateHandle.get<String>(ConstantAppScreenName.UUID_ARG)
        if (uuid.isNullOrBlank()) {
            _uiState.value = DetailUiState.Error("Identificador de usuario no proporcionado o inválido")
        } else {
            loadUser(uuid)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadUser(uuid: String) {
        viewModelScope.launch {
            try {
                val user = getUserUseCase(uuid)
                _uiState.value = if (user != null) {
                    DetailUiState.Success(user)
                } else {
                    DetailUiState.NotFound
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
