package com.example.recipeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recipeapp.presentation.screens.add_recipe.AddRecipeScreen
import com.example.recipeapp.presentation.screens.home.HomeScreen
import com.example.recipeapp.presentation.screens.recipe_detail.RecipeDetailScreen

@Composable
fun RecipeNaviHost(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination= HomeRouter
    ){
        composable<HomeRouter> {
            HomeScreen(
                onRecipeClick = { id ->
                    navController.navigate(RecipeDetailRoute(id))
                },
                onAddRecipe = {
                    navController.navigate(AddRecipeRoute)
                }
            )
        }
        composable<AddRecipeRoute> {
            AddRecipeScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<RecipeDetailRoute> { backStackEntry ->

            val detailRoute = backStackEntry.toRoute<RecipeDetailRoute>()

            RecipeDetailScreen(
                recipeId =detailRoute.recipeId,
                onBack = {navController.popBackStack()}
            )
        }
    }
}