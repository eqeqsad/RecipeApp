package com.example.recipeapp.presentation.screens.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recipeapp.core.UiState
import com.example.recipeapp.presentation.components.ErrorMessage
import com.example.recipeapp.presentation.components.RecipeTopBar
import com.example.recipeapp.presentation.screens.home.LoadingIndicator
import com.example.recipeapp.presentation.viewmodels.RecipeDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeDetailScreen(
        recipeId: Int,
        onBack: () -> Unit,
        viewModel: RecipeDetailViewModel = koinViewModel()
) {
        LaunchedEffect(recipeId) {
                viewModel.fetchRecipeDetail(recipeId)
        }

        Scaffold(
                topBar = {
                        RecipeTopBar(
                                title = "Recipe Details",
                                onBackClick = onBack,
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                actions = {
                                        IconButton(onClick = { viewModel.toggleFavorite(recipeId) }) {
                                                Icon(
                                                        imageVector = if (viewModel.isFavorite(recipeId)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                        contentDescription = "Favorite",
                                                        tint = if (viewModel.isFavorite(recipeId)) Color(0xFFE53935) else Color.DarkGray
                                                )
                                        }
                                }
                        )
                }
        ) { innerPadding ->
                Box(
                        modifier = Modifier.fillMaxWidth()
                                .padding(innerPadding)
                                .background(color = Color(0xFFFF6822).copy(alpha = 0.05f))
                ) {
                        when (val state = viewModel.uiState) {
                                UiState.Loading -> LoadingIndicator(strokeWidth = 1.dp)

                                is UiState.Error -> ErrorMessage(
                                        errorMessage = state.message,
                                        onRetry = { viewModel.fetchRecipeDetail(recipeId) }
                                )

                                is UiState.Success -> {
                                        RecipeDetailContent(details = state.data)
                                }
                        }
                }

        }
}
