package com.aca.people.domain.model

data class UserNote(
    val userUuid: String,
    val content: String,
    val updatedAtEpochMillis: Long
)
