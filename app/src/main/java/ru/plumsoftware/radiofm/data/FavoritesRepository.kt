package ru.plumsoftware.radiofm.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

/**
 * Хранит id избранных станций между запусками приложения (DataStore).
 * Используется и на главном экране (чип "Избранное"), и на экране плеера (кнопка "сердце").
 */
class FavoritesRepository(private val context: Context) {

    private val favoriteIdsKey = stringSetPreferencesKey("favorite_station_ids")

    val favoriteIds: Flow<Set<String>> = context.favoritesDataStore.data.map { prefs ->
        prefs[favoriteIdsKey] ?: emptySet()
    }

    suspend fun toggleFavorite(stationId: String) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[favoriteIdsKey] ?: emptySet()
            prefs[favoriteIdsKey] = if (stationId in current) current - stationId else current + stationId
        }
    }
}
