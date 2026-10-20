package com.example.recipeapp.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.UiState
import com.example.recipeapp.core.toUserMessage
import com.example.recipeapp.data.local.FavoritesRepository
import com.example.recipeapp.data.remote.dto.RecipeDTO
import com.example.recipeapp.demain.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val repository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    var uiState by mutableStateOf<UiState<RecipeDTO>>(UiState.Loading)
        private set

    var favoriteIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    init {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collect { favoriteIds = it }
        }
    }

    fun fetchRecipeDetail(id: Int) {
        uiState = UiState.Loading

        viewModelScope.launch {
            try {
                uiState = UiState.Success(repository.getRecipeById(id))
            } catch (e: Exception) {
                uiState = UiState.Error(e.toUserMessage())
            }
        }
    }

    fun isFavorite(id: Int): Boolean = id in favoriteIds

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { favoritesRepository.toggle(id) }
    }
}
