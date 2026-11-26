package com.aca.people.domain.model

data class SortCriteria(
    val field: SortField = SortField.DEFAULT,
    val direction: SortDirection = SortDirection.ASCENDING
)

enum class SortField {
    DEFAULT,
    FIRST_NAME,
    LAST_NAME,
    FULL_NAME
}

enum class SortDirection {
    ASCENDING,
    DESCENDING
}
