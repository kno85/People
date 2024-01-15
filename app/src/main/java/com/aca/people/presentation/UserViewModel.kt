package com.aca.people.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

// Presentation/UserViewModel.kt
class UserViewModel(private val userUseCase: UserUseCase) : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _showExitDialog = MutableLiveData<Boolean>()
    val showExitDialog: LiveData<Boolean> = _showExitDialog


    private val currentPage: Int = 1
    private val pageSize: Int = 10

    init {
        _showExitDialog.value = false
        submitUserList()
    }

    private fun submitUserList() {
        viewModelScope.launch {
            getUsers(page = currentPage, pageSize = pageSize)
                .catch { err ->
                    _errorMessage.value = err.message
                }
                .collect { list ->
                    if (list.userList != null) {
                        _users.value = list.userList!!
                    } else {
                        _errorMessage.value = list.errorMessage!!
                    }
                }
        }
    }

    fun getUsers(page: Int, pageSize: Int) = flow<UiResult> {
        delay(700)
        userUseCase.getUsers(page, pageSize).let {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    fun userPressBackButton() {
        _showExitDialog.value = !_showExitDialog.value!!
    }


}
