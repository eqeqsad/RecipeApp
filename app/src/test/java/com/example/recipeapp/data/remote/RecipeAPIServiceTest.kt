package com.example.recipeapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeAPIServiceTest {

    private fun mockClient(json: String): HttpClient {
        val engine = MockEngine { _ ->
            respond(
                content = json,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun getAllRecipes_parsesResponse() = runBlocking {
        val json = """
            {
              "recipes": [
                {
                  "caloriesPerServing": 300,
                  "cookTimeMinutes": 15,
                  "cuisine": "Italian",
                  "difficulty": "Easy",
                  "id": 1,
                  "image": "https://example.com/pizza.png",
                  "ingredients": ["Flour", "Tomato"],
                  "instructions": ["Mix", "Bake"],
                  "mealType": ["Dinner"],
                  "name": "Classic Margherita Pizza",
                  "prepTimeMinutes": 20,
                  "rating": 4.6,
                  "reviewCount": 3,
                  "servings": 4,
                  "tags": ["Pizza", "Italian"],
                  "userId": 26
                }
              ],
              "total": 1,
              "skip": 0,
              "limit": 1
            }
        """.trimIndent()

        val response = RecipeAPIService(mockClient(json)).getAllRecipes()

        assertEquals(1, response.total)
        assertEquals(1, response.recipes.size)
        assertEquals("Classic Margherita Pizza", response.recipes[0].name)
        assertEquals(listOf("Flour", "Tomato"), response.recipes[0].ingredients)
    }

    @Test
    fun getRecipeById_returnsSingleRecipe() = runBlocking {
        val json = """
            {
              "caloriesPerServing": 100,
              "cookTimeMinutes": 10,
              "cuisine": "Italian",
              "difficulty": "Easy",
              "id": 7,
              "image": "https://example.com/img.png",
              "ingredients": ["A"],
              "instructions": ["B"],
              "mealType": ["Lunch"],
              "name": "Test Recipe",
              "prepTimeMinutes": 5,
              "rating": 4.0,
              "reviewCount": 1,
              "servings": 2,
              "tags": ["T"],
              "userId": 1
            }
        """.trimIndent()

        val recipe = RecipeAPIService(mockClient(json)).getRecipeById(7)

        assertEquals(7, recipe.id)
        assertEquals("Test Recipe", recipe.name)
    }
}
