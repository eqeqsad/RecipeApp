package com.example.recipeapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import org.koin.androidx.compose.koinViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.recipeapp.core.UiState
import com.example.recipeapp.data.remote.dto.RecipeDTO
import com.example.recipeapp.presentation.components.ErrorMessage
import com.example.recipeapp.presentation.viewmodels.HomeViewModel
import com.example.recipeapp.presentation.viewmodels.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick:(Int) -> Unit,
    onAddRecipe: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =  "Recipes",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    ) },
                actions = {
                    IconButton(onClick = { viewModel.toggleShowFavoritesOnly() }) {
                        Icon(
                            imageVector = if (viewModel.showFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Show favorites",
                            tint = if (viewModel.showFavoritesOnly) Color(0xFFE53935) else Color.DarkGray
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipe,
                containerColor = Color(0xFFFF6822),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add recipe")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFF6822).copy(alpha = 0.05f))
            .padding(innerPadding))
        {
            when (val state = viewModel.uiState) {
                UiState.Loading -> LoadingIndicator(strokeWidth = 1.dp)

                is UiState.Error -> {
                    ErrorMessage(
                        errorMessage = state.message,
                        onRetry = { viewModel.fetchRecipes() }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}) { HomeHeader() }

                        item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                            SearchBar(
                                query = viewModel.searchQuery,
                                onQueryChange = viewModel::onSearchQueryChanged
                            )
                        }

                        item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                            SortBar(
                                selected = viewModel.sortOption,
                                onSelected = viewModel::onSortSelected
                            )
                        }

                        if(viewModel.categories.size > 1){
                            item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                                FilterChipRow(
                                    title = "Categories",
                                    icon = Icons.Default.Restaurant,
                                    options = viewModel.categories,
                                    selected = viewModel.selectedCategory,
                                    onSelected = viewModel::onCategorySelected
                                )
                            }
                        }

                        if(viewModel.difficultyOptions.size > 1){
                            item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                                FilterChipRow(
                                    title = "Difficulty",
                                    icon = Icons.Default.Star,
                                    options = viewModel.difficultyOptions,
                                    selected = viewModel.selectedDifficulty,
                                    onSelected = viewModel::onDifficultySelected
                                )
                            }
                        }

                        if(viewModel.mealTypeOptions.size > 1){
                            item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                                FilterChipRow(
                                    title = "Meal Type",
                                    icon = Icons.Default.Bolt,
                                    options = viewModel.mealTypeOptions,
                                    selected = viewModel.selectedMealType,
                                    onSelected = viewModel::onMealTypeSelected
                                )
                            }
                        }
                        item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionHeader(
                                    title = "Recipes",
                                    icon = Icons.AutoMirrored.Filled.MenuOpen,
                                    count = viewModel.recipes.size
                                )
                                if (viewModel.hasActiveFilters) {
                                    TextButton(onClick = { viewModel.clearAllFilters() }) {
                                        Text(
                                            text = "Clear all",
                                            color = Color(0xFFFF6822),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                        if(viewModel.recipes.isEmpty()){
                            item(span ={GridItemSpan(currentLineSpan = maxLineSpan)}){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = Color(0xFFFF6822).copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val noFavoritesYet = viewModel.showFavoritesOnly && viewModel.favoriteIds.isEmpty()
                                    Text(
                                        text = when {
                                            noFavoritesYet -> "No favorites yet"
                                            viewModel.hasActiveFilters -> "No recipes match your filters"
                                            else -> "No recipes found"
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when {
                                            noFavoritesYet -> "Tap the heart on any recipe to save it"
                                            viewModel.hasActiveFilters -> "Try adjusting your search or filters"
                                            else -> "Check back later"
                                        },
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    if (viewModel.hasActiveFilters) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.clearAllFilters() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF6822),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Clear filters", fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }else{
                             items(viewModel.recipes.take(viewModel.visibleCount),key={it.id}){ recipe ->
                                RecipeCard(
                                    recipe,
                                    isFavorite = viewModel.isFavorite(recipe.id),
                                    onClick = {onRecipeClick(recipe.id)},
                                    onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
                                )

                             }
                             if (viewModel.visibleCount < viewModel.recipes.size) {
                                 item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                                     LaunchedEffect(Unit) { viewModel.loadMore() }
                                     Box(
                                         modifier = Modifier
                                             .fillMaxWidth()
                                             .padding(vertical = 16.dp),
                                         contentAlignment = Alignment.Center
                                     ) {
                                         CircularProgressIndicator(
                                             color = Color(0xFFFF6822),
                                             strokeWidth = 2.dp,
                                             modifier = Modifier.size(24.dp)
                                         )
                                     }
                                 }
                             }
                        }
                    }
                }
            }
        }

    }
}
@Composable
fun LoadingIndicator(strokeWidth: Dp){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
        ) {
        CircularProgressIndicator(color = Color(0xFFFF6822), strokeWidth = strokeWidth)
    }
}

@Composable
fun HomeHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xFFFF6822).copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFFF6822),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center

        )
        {
            Icon(
                Icons.Default.RestaurantMenu,
                contentDescription = "Restaurant Menu Icon",
                tint  = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Hello,Chef!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Find something delicious to cook",
                color = Color.Gray
                )

        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search name, tags, ingredients...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFF6822),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLeadingIconColor = Color(0xFFFF6822),
            unfocusedLeadingIconColor = Color.Gray,
            cursorColor = Color(0xFFFF6822)
        )
    )
}

@Composable
fun SortBar(
    selected: SortOption,
    onSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = selected != SortOption.DEFAULT,
            onClick = { expanded = true },
            label = { Text("Sort: ${selected.label}") },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedLabelColor = Color.White,
                selectedContainerColor = Color(0xFFFF6822),
                labelColor = Color.DarkGray,
                containerColor = Color.White
            ),
            border = null
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChipRow(
    title: String,
    icon: ImageVector,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column {
        SectionHeader(title = title, icon = icon)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                val isSelected = selected == option

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(option) },
                    label = {
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = Color.White,
                        selectedContainerColor = Color(0xFFFF6822),
                        labelColor = Color.DarkGray,
                        containerColor = Color.White
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFF6822),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = if (count != null) "$title ($count)" else title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun RecipeCard(
    recipeDTO: RecipeDTO,
    isFavorite: Boolean,
    onClick:() -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = recipeDTO.image,
                    contentDescription = recipeDTO.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(shape = RoundedCornerShape(topStart = (16.dp), topEnd = (16.dp))),
                    contentScale = ContentScale.Crop,
                    loading ={
                        LoadingIndicator(strokeWidth =2.dp)
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "Error",
                                fontSize = 32.sp
                            )
                        }
                    }
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.Gray
                    )
                }
            }
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = recipeDTO.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = recipeDTO.rating.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${recipeDTO.reviewCount})",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text =  recipeDTO.cuisine,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFF6822).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ){
                        Text(
                            text = recipeDTO.difficulty,
                            fontSize = 12.sp,
                            color = Color(0xFFFF6822),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
