package com.aca.people.presentation

import com.aca.people.domain.GetUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
class FirstFragmentModule {
    @Provides
    fun GetUserViewModelProvider(useCase: GetUsersUseCase) = UserViewModel(useCase)
}