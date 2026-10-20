package com.example.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipeapp.data.remote.dto.RecipeDTO

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val cuisine: String,
    val difficulty: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val caloriesPerServing: Int,
    val rating: Double,
    val reviewCount: Int,
    val userId: Int,
    val image: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val tags: List<String>,
    val mealType: List<String>
)

fun RecipeDTO.toEntity(): RecipeEntity = RecipeEntity(
    id = id,
    name = name,
    cuisine = cuisine,
    difficulty = difficulty,
    prepTimeMinutes = prepTimeMinutes,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    caloriesPerServing = caloriesPerServing,
    rating = rating,
    reviewCount = reviewCount,
    userId = userId,
    image = image,
    ingredients = ingredients,
    instructions = instructions,
    tags = tags,
    mealType = mealType
)

fun RecipeEntity.toDto(): RecipeDTO = RecipeDTO(
    id = id,
    name = name,
    cuisine = cuisine,
    difficulty = difficulty,
    prepTimeMinutes = prepTimeMinutes,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    caloriesPerServing = caloriesPerServing,
    rating = rating,
    reviewCount = reviewCount,
    userId = userId,
    image = image,
    ingredients = ingredients,
    instructions = instructions,
    tags = tags,
    mealType = mealType
)
