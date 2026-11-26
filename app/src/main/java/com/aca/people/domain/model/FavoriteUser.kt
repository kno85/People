package com.aca.people.domain.model

import com.aca.people.domain.User

data class FavoriteUser(
    val uuid: String,
    val user: User,
    val addedAtEpochMillis: Long
)
