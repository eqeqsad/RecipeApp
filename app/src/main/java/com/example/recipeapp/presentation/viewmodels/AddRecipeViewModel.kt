package com.example.recipeapp.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.toUserMessage
import com.example.recipeapp.data.remote.RecipeAPIService
import com.example.recipeapp.data.remote.dto.AddRecipeRequest
import kotlinx.coroutines.launch

class AddRecipeViewModel(
    private val apiService: RecipeAPIService
) : ViewModel() {

    val mealTypeOptions = listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Beverage", "Side Dish")

    var name by mutableStateOf("")
    var cuisine by mutableStateOf("")
    var difficulty by mutableStateOf("Easy")
    var selectedMealTypes by mutableStateOf<Set<String>>(emptySet())
    var prepTimeMinutes by mutableStateOf("")
    var cookTimeMinutes by mutableStateOf("")
    var servings by mutableStateOf("")
    var caloriesPerServing by mutableStateOf("")

    var ingredientInput by mutableStateOf("")
    var ingredients by mutableStateOf<List<String>>(emptyList())

    var instructionInput by mutableStateOf("")
    var instructions by mutableStateOf<List<String>>(emptyList())

    var tagsInput by mutableStateOf("")

    var isSubmitting by mutableStateOf(false)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun toggleMealType(type: String) {
        selectedMealTypes =
            if (type in selectedMealTypes) selectedMealTypes - type else selectedMealTypes + type
    }

    fun addIngredient() {
        val value = ingredientInput.trim()
        if (value.isNotEmpty()) {
            ingredients = ingredients + value
            ingredientInput = ""
        }
    }

    fun removeIngredient(index: Int) {
        ingredients = ingredients.filterIndexed { i, _ -> i != index }
    }

    fun addInstruction() {
        val value = instructionInput.trim()
        if (value.isNotEmpty()) {
            instructions = instructions + value
            instructionInput = ""
        }
    }

    fun removeInstruction(index: Int) {
        instructions = instructions.filterIndexed { i, _ -> i != index }
    }

    fun submit() {
        if (isSubmitting) return

        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            errorMessage = "Please enter a recipe name"
            return
        }
        if (ingredients.isEmpty()) {
            errorMessage = "Please add at least one ingredient"
            return
        }
        if (instructions.isEmpty()) {
            errorMessage = "Please add at least one instruction"
            return
        }

        val request = AddRecipeRequest(
            caloriesPerServing = caloriesPerServing.trim().toIntOrNull() ?: 0,
            cookTimeMinutes = cookTimeMinutes.trim().toIntOrNull() ?: 0,
            cuisine = cuisine.trim(),
            difficulty = difficulty,
            ingredients = ingredients,
            instructions = instructions,
            mealType = selectedMealTypes.toList(),
            name = trimmedName,
            prepTimeMinutes = prepTimeMinutes.trim().toIntOrNull() ?: 0,
            servings = servings.trim().toIntOrNull() ?: 0,
            tags = tagsInput.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        )

        isSubmitting = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            try {
                apiService.addRecipe(request)
                successMessage = "Recipe \"$trimmedName\" submitted successfully"
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isSubmitting = false
            }
        }
    }
}
