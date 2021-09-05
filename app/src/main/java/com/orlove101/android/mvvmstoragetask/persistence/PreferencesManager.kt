package com.orlove101.android.mvvmstoragetask.persistence.Room

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME, BY_AGE, BY_BREED }
enum class CurrentDatabase { ROOM, SQLITE }

data class SettingsPreferences(val sortOrder: SortOrder, val currentDatabase: CurrentDatabase)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if ( exception is IOException ) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_NAME.name
            )
            val currentDatabase = CurrentDatabase.valueOf(
                preferences[PreferencesKeys.CURRENT_DATABASE] ?: CurrentDatabase.ROOM.name
            )
            SettingsPreferences(sortOrder, currentDatabase)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateCurrentDatabase(currentDatabase: CurrentDatabase) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_DATABASE] = currentDatabase.name
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val CURRENT_DATABASE = stringPreferencesKey("current_database")
    }
}