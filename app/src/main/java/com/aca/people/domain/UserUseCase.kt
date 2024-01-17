package com.aca.people.domain

import androidx.paging.PagingData
import com.aca.people.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Domain/UserUseCase.kt
class UserUseCase @Inject constructor(
    private val userRepository: UserRepository
): BaseUseCase<Unit, Flow<PagingData<User>>> {
    override suspend fun execute(input: Unit): Flow<PagingData<User>> {
        return userRepository.getUsers()
    }
}
