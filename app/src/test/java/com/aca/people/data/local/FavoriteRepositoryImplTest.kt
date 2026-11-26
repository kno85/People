package com.aca.people.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.aca.people.data.remote.getMockUserList
import com.aca.people.utils.mapToDomain
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImplTest {

    @TempDir
    lateinit var tempDir: File

    private val sampleUsers = mapToDomain(getMockUserList())

    private fun createRepository(scope: TestScope, fileName: String): FavoriteRepositoryImpl {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope.backgroundScope,
            produceFile = { File(tempDir, "$fileName.preferences_pb") }
        )
        return FavoriteRepositoryImpl(dataStore, Gson())
    }

    @Test
    fun `addFavorite stores user and prevents duplicates`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "add_no_dup")
        val alice = sampleUsers.first()
        val aliceUuid = requireNotNull(alice.login?.uuid)

        repository.addFavorite(alice)
        repository.addFavorite(alice)

        val favorites = repository.observeFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals(aliceUuid, favorites.first().uuid)
        assertEquals(alice, favorites.first().user)
        assertTrue(repository.isFavorite(aliceUuid).first())
    }

    @Test
    fun `removeFavorite deletes user from favorites`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "remove")
        val alice = sampleUsers.first()
        val aliceUuid = requireNotNull(alice.login?.uuid)

        repository.addFavorite(alice)
        assertNotNull(repository.getFavoriteByUuid(aliceUuid))

        repository.removeFavorite(aliceUuid)

        assertTrue(repository.observeFavorites().first().isEmpty())
        assertFalse(repository.isFavorite(aliceUuid).first())
        assertNull(repository.getFavoriteByUuid(aliceUuid))
    }

    @Test
    fun `toggleFavorite adds when absent and removes when present`() = runTest(UnconfinedTestDispatcher()) {
        val repository = createRepository(this, "toggle")
        val bob = sampleUsers[1]
        val bobUuid = requireNotNull(bob.login?.uuid)

        repository.toggleFavorite(bob)
        assertEquals(setOf(bobUuid), repository.observeFavoriteUuids().first())

        repository.toggleFavorite(bob)
        assertTrue(repository.observeFavoriteUuids().first().isEmpty())
    }
}
