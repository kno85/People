package com.aca.people.domain

import javax.inject.Inject

data class UserNoteUseCases @Inject constructor(
    val observeUserNote: ObserveUserNoteUseCase,
    val saveNote: SaveNoteUseCase,
    val deleteNote: DeleteNoteUseCase
)
