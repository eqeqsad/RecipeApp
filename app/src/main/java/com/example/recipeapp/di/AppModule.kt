package com.example.recipeapp.di

import androidx.room.Room
import com.example.recipeapp.data.local.FavoritesRepository
import com.example.recipeapp.data.local.RecipeDatabase
import com.example.recipeapp.data.local.RecipeLocalDataSource
import com.example.recipeapp.data.remote.KtorClient
import com.example.recipeapp.data.remote.RecipeAPIService
import com.example.recipeapp.data.repository.RecipeRepositoryImpl
import com.example.recipeapp.demain.repository.RecipeRepository
import com.example.recipeapp.presentation.viewmodels.AddRecipeViewModel
import com.example.recipeapp.presentation.viewmodels.HomeViewModel
import com.example.recipeapp.presentation.viewmodels.RecipeDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { KtorClient.client }
    single { RecipeAPIService(get()) }

    single { Room.databaseBuilder(androidContext(), RecipeDatabase::class.java, "recipe.db").build() }
    single { get<RecipeDatabase>().recipeDao() }
    single { RecipeLocalDataSource(get()) }

    single<RecipeRepository> { RecipeRepositoryImpl(get(), get()) }
    single { FavoritesRepository(androidContext()) }

    viewModelOf(::HomeViewModel)
    viewModelOf(::RecipeDetailViewModel)
    viewModelOf(::AddRecipeViewModel)
}
