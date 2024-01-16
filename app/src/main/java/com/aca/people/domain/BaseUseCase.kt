package com.aca.people.domain

interface BaseUseCase<In, Out>{
    suspend fun execute(input: In): Out
}