package com.example.recipeapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRouter {
}

@Serializable
object AddRecipeRoute {
}

@Serializable
data class RecipeDetailRoute(val recipeId: Int){

}