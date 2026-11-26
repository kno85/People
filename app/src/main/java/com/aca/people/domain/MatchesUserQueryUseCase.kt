package com.aca.people.domain

import com.aca.people.domain.model.CategoryFilter
import com.aca.people.domain.model.GenderFilter
import com.aca.people.domain.model.UserFilterCriteria
import javax.inject.Inject

class MatchesUserQueryUseCase @Inject constructor() {

    operator fun invoke(
        user: User,
        criteria: UserFilterCriteria,
        favoriteUuids: Set<String> = emptySet(),
        notedUuids: Set<String> = emptySet()
    ): Boolean {
        return matchesCategory(user, criteria.category, favoriteUuids, notedUuids) &&
            matchesGender(user, criteria.gender) &&
            matchesQuery(user, criteria.query)
    }

    fun matchesQuery(user: User, rawQuery: String): Boolean {
        val query = rawQuery.trim().lowercase()
        return if (query.isEmpty()) {
            true
        } else {
            matchesTextFields(user, query) || matchesPhoneDigits(user, query)
        }
    }

    private fun matchesTextFields(user: User, query: String): Boolean {
        val first = user.name?.first.orEmpty()
        val last = user.name?.last.orEmpty()
        val fullName = "$first $last".trim().lowercase()
        val fullNameNoSpace = "$first$last".lowercase()
        val email = user.email.orEmpty().lowercase()
        val phone = user.phone.orEmpty().lowercase()
        val cell = user.cell.orEmpty().lowercase()
        return fullName.contains(query) ||
            fullNameNoSpace.contains(query) ||
            email.contains(query) ||
            phone.contains(query) ||
            cell.contains(query)
    }

    private fun matchesPhoneDigits(user: User, query: String): Boolean {
        val queryDigits = query.filter(Char::isDigit)
        return queryDigits.isNotEmpty() && (
            user.phone.orEmpty().filter(Char::isDigit).contains(queryDigits) ||
                user.cell.orEmpty().filter(Char::isDigit).contains(queryDigits)
            )
    }

    private fun matchesCategory(
        user: User,
        category: CategoryFilter,
        favoriteUuids: Set<String>,
        notedUuids: Set<String>
    ): Boolean {
        val uuid = user.login?.uuid
        return when (category) {
            CategoryFilter.ALL -> true
            CategoryFilter.FAVORITES -> !uuid.isNullOrBlank() && uuid in favoriteUuids
            CategoryFilter.WITH_NOTES -> !uuid.isNullOrBlank() && uuid in notedUuids
        }
    }

    private fun matchesGender(user: User, gender: GenderFilter): Boolean {
        return when (gender) {
            GenderFilter.ALL -> true
            GenderFilter.FEMALE -> user.gender.equals("female", ignoreCase = true)
            GenderFilter.MALE -> user.gender.equals("male", ignoreCase = true)
        }
    }
}
