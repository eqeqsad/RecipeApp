package com.example.recipeapp.presentation.screens.add_recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.example.recipeapp.presentation.components.RecipeTopBar
import com.example.recipeapp.presentation.viewmodels.AddRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRecipeScreen(
    onBack: () -> Unit,
    viewModel: AddRecipeViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            RecipeTopBar(
                title = "Add Recipe",
                onBackClick = onBack,
                icon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Recipe name *") },
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.cuisine,
                onValueChange = { viewModel.cuisine = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cuisine") },
                singleLine = true
            )

            SectionLabel("Difficulty")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Easy", "Medium", "Hard").forEach { option ->
                    FilterChip(
                        selected = viewModel.difficulty == option,
                        onClick = { viewModel.difficulty = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF6822),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            SectionLabel("Meal type")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.mealTypeOptions.forEach { option ->
                    FilterChip(
                        selected = option in viewModel.selectedMealTypes,
                        onClick = { viewModel.toggleMealType(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF6822),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = viewModel.prepTimeMinutes,
                    onValueChange = { viewModel.prepTimeMinutes = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Prep (min)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.cookTimeMinutes,
                    onValueChange = { viewModel.cookTimeMinutes = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Cook (min)") },
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = viewModel.servings,
                    onValueChange = { viewModel.servings = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Servings") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.caloriesPerServing,
                    onValueChange = { viewModel.caloriesPerServing = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Calories / serving") },
                    singleLine = true
                )
            }

            StringListEditor(
                title = "Ingredients *",
                items = viewModel.ingredients,
                input = viewModel.ingredientInput,
                onInputChange = { viewModel.ingredientInput = it },
                onAdd = { viewModel.addIngredient() },
                onRemove = { viewModel.removeIngredient(it) }
            )

            StringListEditor(
                title = "Instructions *",
                items = viewModel.instructions,
                input = viewModel.instructionInput,
                onInputChange = { viewModel.instructionInput = it },
                onAdd = { viewModel.addInstruction() },
                onRemove = { viewModel.removeInstruction(it) }
            )

            OutlinedTextField(
                value = viewModel.tagsInput,
                onValueChange = { viewModel.tagsInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tags (comma separated)") },
                singleLine = true
            )

            viewModel.errorMessage?.let { msg ->
                Text(msg, color = Color(0xFFE53935), fontSize = 14.sp)
            }
            viewModel.successMessage?.let { msg ->
                Text(msg, color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6822),
                    contentColor = Color.White
                )
            ) {
                if (viewModel.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Recipe", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
}

@Composable
private fun StringListEditor(
    title: String,
    items: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit
) {
    SectionLabel(title)
    items.forEachIndexed { index, item ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray
            )
            IconButton(onClick = { onRemove(index) }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Add an item") },
            singleLine = true
        )
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFFF6822))
        }
    }
}
