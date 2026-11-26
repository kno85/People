package com.aca.people.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aca.people.data.local.FavoriteRepositoryImpl
import com.aca.people.data.local.NoteRepositoryImpl
import com.aca.people.domain.repository.FavoriteRepository
import com.aca.people.domain.repository.NoteRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {

    private const val PREFERENCES_FILE_NAME = "people_personal_org_prefs"

    @Singleton
    @Provides
    fun providesPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(PREFERENCES_FILE_NAME)
        }
    }

    @Singleton
    @Provides
    fun providesGson(): Gson = Gson()

    @Singleton
    @Provides
    fun providesFavoriteRepository(
        dataStore: DataStore<Preferences>,
        gson: Gson
    ): FavoriteRepository = FavoriteRepositoryImpl(dataStore, gson)

    @Singleton
    @Provides
    fun providesNoteRepository(
        dataStore: DataStore<Preferences>,
        gson: Gson
    ): NoteRepository = NoteRepositoryImpl(dataStore, gson)
}
