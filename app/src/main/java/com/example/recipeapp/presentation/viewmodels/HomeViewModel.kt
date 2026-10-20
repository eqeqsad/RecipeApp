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

class HomeViewModel(
    private val repository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    var uiState by mutableStateOf<UiState<List<RecipeDTO>>>(UiState.Loading)
        private set

    val recipes: List<RecipeDTO>
        get() = (uiState as? UiState.Success)?.data ?: emptyList()

    var categories by mutableStateOf<List<String>>(listOf("All"))
        private set
    var difficultyOptions by mutableStateOf<List<String>>(listOf("All"))
        private set
    var mealTypeOptions by mutableStateOf<List<String>>(listOf("All"))
        private set

    var selectedCategory by mutableStateOf("All")
        private set
    var selectedDifficulty by mutableStateOf("All")
        private set
    var selectedMealType by mutableStateOf("All")
        private set

    var searchQuery by mutableStateOf("")
        private set

    var sortOption by mutableStateOf(SortOption.DEFAULT)
        private set

    var favoriteIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    var showFavoritesOnly by mutableStateOf(false)
        private set

    var visibleCount by mutableStateOf(PAGE_SIZE)
        private set

    private var allRecipes: List<RecipeDTO> = emptyList()

    init {
        fetchRecipes()
        observeFavorites()
    }

    fun loadMore() {
        if (visibleCount < recipes.size) {
            visibleCount = minOf(visibleCount + PAGE_SIZE, recipes.size)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collect { ids ->
                favoriteIds = ids
                if (showFavoritesOnly) {
                    applyFilters()
                }
            }
        }
    }

    fun fetchRecipes() {
        uiState = UiState.Loading

        viewModelScope.launch {
            try {
                val result = repository.getAllRecipes()
                allRecipes = result

                categories = listOf("All") + result.map { it.cuisine }.distinct().sorted()
                difficultyOptions = listOf("All") + result.map { it.difficulty }.distinct().sorted()
                mealTypeOptions = listOf("All") + result.flatMap { it.mealType }.distinct().sorted()

                applyFilters()
            } catch (e: Exception) {
                uiState = UiState.Error(e.toUserMessage())
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
        applyFilters()
    }

    fun onDifficultySelected(difficulty: String) {
        selectedDifficulty = difficulty
        applyFilters()
    }

    fun onMealTypeSelected(mealType: String) {
        selectedMealType = mealType
        applyFilters()
    }

    fun onSortSelected(option: SortOption) {
        sortOption = option
        applyFilters()
    }

    fun isFavorite(id: Int): Boolean = id in favoriteIds

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { favoritesRepository.toggle(id) }
    }

    fun toggleShowFavoritesOnly() {
        showFavoritesOnly = !showFavoritesOnly
        applyFilters()
    }

    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() ||
            selectedCategory != "All" ||
            selectedDifficulty != "All" ||
            selectedMealType != "All" ||
            sortOption != SortOption.DEFAULT ||
            showFavoritesOnly

    fun clearAllFilters() {
        searchQuery = ""
        selectedCategory = "All"
        selectedDifficulty = "All"
        selectedMealType = "All"
        sortOption = SortOption.DEFAULT
        showFavoritesOnly = false
        applyFilters()
    }

    private fun applyFilters() {
        visibleCount = PAGE_SIZE
        val query = searchQuery.trim()
        var result = allRecipes

        if (query.isNotEmpty()) {
            result = result.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                    recipe.tags.any { it.contains(query, ignoreCase = true) } ||
                    recipe.ingredients.any { it.contains(query, ignoreCase = true) }
            }
        }

        if (selectedCategory != "All") {
            result = result.filter { it.cuisine == selectedCategory }
        }

        if (selectedDifficulty != "All") {
            result = result.filter { it.difficulty == selectedDifficulty }
        }

        if (selectedMealType != "All") {
            result = result.filter { it.mealType.contains(selectedMealType) }
        }

        if (showFavoritesOnly) {
            result = result.filter { it.id in favoriteIds }
        }

        result = when (sortOption) {
            SortOption.RATING -> result.sortedByDescending { it.rating }
            SortOption.REVIEWS -> result.sortedByDescending { it.reviewCount }
            SortOption.PREP_TIME -> result.sortedBy { it.prepTimeMinutes }
            SortOption.CALORIES -> result.sortedBy { it.caloriesPerServing }
            SortOption.NAME -> result.sortedBy { it.name.lowercase() }
            SortOption.DEFAULT -> result
        }

        uiState = UiState.Success(result)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
