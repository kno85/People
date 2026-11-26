package com.aca.people.domain

import com.aca.people.data.remote.getMockUserList
import com.aca.people.domain.model.CategoryFilter
import com.aca.people.domain.model.GenderFilter
import com.aca.people.domain.model.UserFilterCriteria
import com.aca.people.utils.mapToDomain
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchesUserQueryUseCaseTest {

    private val useCase = MatchesUserQueryUseCase()
    private val sampleUsers = mapToDomain(getMockUserList())
    private val alice = sampleUsers[0]
    private val bob = sampleUsers[1]

    @Test
    fun `matchesQuery matches first name, last name and full name case-insensitively`() {
        assertTrue(useCase.matchesQuery(alice, "alice"))
        assertTrue(useCase.matchesQuery(alice, "SMITH"))
        assertTrue(useCase.matchesQuery(alice, "  Alice Smith  "))
        assertFalse(useCase.matchesQuery(alice, "Johnson"))
    }

    @Test
    fun `matchesQuery matches email`() {
        assertTrue(useCase.matchesQuery(bob, "bob.johnson@example.ca"))
        assertTrue(useCase.matchesQuery(bob, "EXAMPLE.CA"))
        assertFalse(useCase.matchesQuery(bob, "alice.smith@example.com"))
    }

    @Test
    fun `matchesQuery matches phone and cell with or without formatting`() {
        assertTrue(useCase.matchesQuery(alice, "555-0101"))
        assertTrue(useCase.matchesQuery(alice, "5550101"))
        assertTrue(useCase.matchesQuery(alice, "555-0102"))
        assertTrue(useCase.matchesQuery(alice, "5550102"))
        assertFalse(useCase.matchesQuery(alice, "999-9999"))
    }

    @Test
    fun `matchesQuery returns true when query is blank`() {
        assertTrue(useCase.matchesQuery(alice, ""))
        assertTrue(useCase.matchesQuery(alice, "   "))
    }

    @Test
    fun `invoke combines query with category and gender filters`() {
        val aliceUuid = requireNotNull(alice.login?.uuid)
        val criteria = UserFilterCriteria(
            query = "alice",
            category = CategoryFilter.FAVORITES,
            gender = GenderFilter.FEMALE
        )

        assertTrue(useCase(alice, criteria, favoriteUuids = setOf(aliceUuid)))
        assertFalse(useCase(alice, criteria, favoriteUuids = emptySet()))
        assertFalse(
            useCase(
                alice,
                criteria.copy(gender = GenderFilter.MALE),
                favoriteUuids = setOf(aliceUuid)
            )
        )
    }
}
