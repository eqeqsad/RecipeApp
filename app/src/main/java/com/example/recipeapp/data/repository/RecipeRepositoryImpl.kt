package com.example.recipeapp.data.repository

import com.example.recipeapp.data.local.RecipeLocalDataSource
import com.example.recipeapp.data.local.toDto
import com.example.recipeapp.data.local.toEntity
import com.example.recipeapp.data.remote.RecipeAPIService
import com.example.recipeapp.data.remote.dto.RecipeDTO
import com.example.recipeapp.demain.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val apiService: RecipeAPIService,
    private val localDataSource: RecipeLocalDataSource
) : RecipeRepository {

    override suspend fun getAllRecipes(): List<RecipeDTO> {
        return try {
            val recipes = apiService.getAllRecipes().recipes
            localDataSource.cacheRecipes(recipes.map { it.toEntity() })
            recipes
        } catch (e: Exception) {
            val cached = localDataSource.getCachedRecipes()
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    override suspend fun getRecipeById(id: Int): RecipeDTO {
        return try {
            val recipe = apiService.getRecipeById(id)
            localDataSource.cacheRecipe(recipe.toEntity())
            recipe
        } catch (e: Exception) {
            localDataSource.getCachedRecipe(id)?.toDto() ?: throw e
        }
    }
}
