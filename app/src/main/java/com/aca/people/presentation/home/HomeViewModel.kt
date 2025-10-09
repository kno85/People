package com.aca.people.presentation.home

import androidx.lifecycle.ViewModel // CAMBIO: Hereda del ViewModel estándar de AndroidX
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase
// import com.aca.people.presentation.util.ScopedViewModel // CAMBIO: Se elimina la dependencia del ViewModel personalizado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: UserUseCase
) : ViewModel() { // CAMBIO: Hereda de ViewModel directamente

    // Variable para almacenar la acción de cierre que será "inyectada" desde la UI.
    // Esto es parte de la solución de "forzar la salida" y es muy robusto.
    var onExitAction: (() -> Unit)? = null

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
        // CORRECCIÓN PRINCIPAL:
        // En lugar de emitir un evento de Flow que no llegaba,
        // ahora invocamos directamente la acción de salida que nos pasó la UI.
        onExitAction?.invoke()
    }

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // El StateFlow que contiene los datos de paginación.
    private val _usersPagingData: MutableStateFlow<PagingData<User>> = MutableStateFlow(PagingData.empty())

    // El StateFlow público que la UI observará. Combina los datos de paginación con el texto de búsqueda.
    val usersState: StateFlow<PagingData<User>> = searchText
        .combine(_usersPagingData) { text, pagingData ->
            if (text.isBlank()) {
                pagingData // Si no hay búsqueda, muestra todos los usuarios.
            } else {
                pagingData.filter {
                    // Filtra los datos según la consulta de búsqueda.
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // El Flow se mantiene activo 5 seg después de que el último subscriptor desaparezca.
            initialValue = PagingData.empty()
        )

    init {
        // CAMBIO: Ya no se necesita initScope()
        getUsers() // Se llama directamente a la función que obtiene los usuarios.
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun getUsers() {
        viewModelScope.launch {
            getUsersUseCase.execute(Unit)
                .distinctUntilChanged()
                .cachedIn(viewModelScope) // Guarda en caché los datos de paginación en el scope del ViewModel.
                .collect { pagingData ->
                    _usersPagingData.value = pagingData
                }
        }
    }

    // CAMBIO: onCleared() ya no es necesario para destruir el scope.
    // El 'viewModelScope' estándar se cancela automáticamente.
    // Puedes eliminar este método si no tienes otra limpieza que hacer.
    override fun onCleared() {
        super.onCleared()
    }
}

// He movido HomeEvent a este archivo ya que solo se usa aquí. Si lo usas en otro lado, puedes dejarlo fuera.
sealed class HomeEvent {
    object GetHome : HomeEvent()
}
