package com.aca.people.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.GetUsersUseCase
import com.aca.people.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Presentation/UserViewModel.kt
@HiltViewModel
class UserViewModel  @Inject constructor(
    private val userUseCase : GetUsersUseCase
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _showExitDialog = MutableLiveData<Boolean>()
    val showExitDialog: LiveData<Boolean> = _showExitDialog


     fun submitUserList() {
        viewModelScope.launch {
            fetchUserList()
                .catch { err ->
                    _errorMessage.value= err.message
                }
                .collect { list ->
                    _users.value = list
                }
        }
    }
    private suspend fun fetchUserList() = flow<List<User>> {
        delay(700)
        userUseCase.invoke(1,10).let {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)


    fun userPressBackButton() {
        _showExitDialog.value = !_showExitDialog.value!!
    }
}
