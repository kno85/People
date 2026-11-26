package com.aca.people.domain.model

data class UserFilterCriteria(
    val query: String = "",
    val category: CategoryFilter = CategoryFilter.ALL,
    val gender: GenderFilter = GenderFilter.ALL
) {
    val hasActiveFilters: Boolean
        get() = query.isNotBlank() || category != CategoryFilter.ALL || gender != GenderFilter.ALL
}

enum class CategoryFilter {
    ALL,
    FAVORITES,
    WITH_NOTES
}

enum class GenderFilter {
    ALL,
    FEMALE,
    MALE
}
