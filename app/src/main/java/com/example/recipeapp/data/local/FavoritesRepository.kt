package com.example.recipeapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {

    private val favoriteIdsKey = stringSetPreferencesKey("favorite_ids")

    val favoriteIds: Flow<Set<Int>> = context.favoritesDataStore.data.map { prefs ->
        prefs[favoriteIdsKey]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    suspend fun isFavorite(id: Int): Boolean =
        favoriteIds.first().contains(id)

    suspend fun toggle(id: Int) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[favoriteIdsKey]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
            val updated = if (current.contains(id)) current - id else current + id
            prefs[favoriteIdsKey] = updated.map { it.toString() }.toSet()
        }
    }
}
