package com.example.recipeapp.data.local

import com.example.recipeapp.data.remote.dto.RecipeDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeEntityMappersTest {

    private val dto = RecipeDTO(
        id = 1,
        name = "Classic Margherita Pizza",
        cuisine = "Italian",
        difficulty = "Easy",
        prepTimeMinutes = 20,
        cookTimeMinutes = 15,
        servings = 4,
        caloriesPerServing = 300,
        rating = 4.6,
        reviewCount = 3,
        userId = 26,
        image = "https://example.com/pizza.png",
        ingredients = listOf("Flour", "Tomato", "Mozzarella"),
        instructions = listOf("Mix dough", "Bake"),
        tags = listOf("Pizza", "Italian"),
        mealType = listOf("Dinner")
    )

    @Test
    fun dtoToEntityRoundtrip_preservesAllFields() {
        val entity = dto.toEntity()
        val back = entity.toDto()
        assertEquals(dto, back)
    }
}
