package com.example.recipeapp.data.local

import kotlinx.coroutines.flow.first

class RecipeLocalDataSource(private val dao: RecipeDao) {

    suspend fun cacheRecipes(recipes: List<RecipeEntity>) {
        dao.insertAll(recipes)
    }

    suspend fun cacheRecipe(recipe: RecipeEntity) {
        dao.insertAll(listOf(recipe))
    }

    suspend fun getCachedRecipes(): List<RecipeEntity> = dao.getAll().first()

    suspend fun getCachedRecipe(id: Int): RecipeEntity? = dao.getById(id)
}
