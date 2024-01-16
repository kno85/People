package com.aca.people.domain

import androidx.paging.PagingData
import com.aca.people.repository.paging.UserRepository
import com.esotericsoftware.kryo.io.Input
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Domain/UserUseCase.kt
//class UserUseCase @Inject constructor(
//    private val userRepository: UserRepository
//): BaseUseCase<Unit, List<User>> {
//    override suspend fun execute(input: Unit): List<User> {
//        return userRepository.getUsers(1, 10)
//    }
//}
class GetUsersUseCase @Inject constructor(private val repository: UserRepository) {

    suspend operator fun invoke(page:Int, pageSize:Int) = repository.getUsers(page, pageSize)
}