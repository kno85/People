package com.aca.people.di

import com.aca.people.repository.paging.UserRepository
import com.aca.people.repository.paging.UserRepositoryImpl
import com.aca.people.data.remote.UserRemoteDataSource
import com.aca.people.domain.GetUsersUseCase
import com.aca.people.network.ApiService
import com.mmj.movieapp.data.datasource.remote.UserRemoteDataSourceImpl
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
        return UserRepositoryImpl(userRemoteDataSource)
    }

    @Singleton
    @Provides
    fun providesGetUserUseCase(
        userRepository: UserRepository
    ): GetUsersUseCase {
        return GetUsersUseCase(userRepository)
    }
}