package com.example.recipeapp.data.remote


import com.example.recipeapp.data.remote.dto.AddRecipeRequest
import com.example.recipeapp.data.remote.dto.AddRecipeResponse
import com.example.recipeapp.data.remote.dto.RecipeDTO
import com.example.recipeapp.data.remote.dto.RecipeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class RecipeAPIService(private val client: HttpClient) {
    suspend fun getAllRecipes(): RecipeResponse{
        return client.get(urlString = "${KtorClient.BASE_URL}recipes?limit=0").body()
    }
    suspend fun getRecipeById(id: Int): RecipeDTO{
        return client.get(urlString = "${KtorClient.BASE_URL}recipes/$id").body()
    }
    suspend fun addRecipe(request: AddRecipeRequest): AddRecipeResponse {
        return client.post("${KtorClient.BASE_URL}recipes/add"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}