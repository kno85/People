package com.aca.people.di

import com.aca.people.repository.UserRepository
import com.aca.people.repository.UserRepositoryImp
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.network.ApiService
import com.aca.people.data.remote.UserRemoteDataSourceImpl
import com.aca.people.domain.UserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Singleton
    @Provides
    fun providesUserRemoteDataSource(
        api: ApiService
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(api)
    }

    @Singleton
    @Provides
    fun providesUserRepository(
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImp(userRemoteDataSource)
    }

    @Singleton
    @Provides
    fun providesGetUserUseCase(
        userRepository: UserRepository
    ): UserUseCase {
        return UserUseCase(userRepository)
    }
}