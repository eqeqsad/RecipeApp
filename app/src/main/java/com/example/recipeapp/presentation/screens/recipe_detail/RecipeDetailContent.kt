package com.example.recipeapp.presentation.screens.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recipeapp.data.remote.dto.RecipeDTO

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailContent(
   details: RecipeDTO) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .background(color = Color(0xFFFF6822).copy(alpha = 0.05f)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = details.image,
            contentDescription = details.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )

        DetailSection(title = "Recipe Details", icon = Icons.Default.RemoveRedEye) {
            Text(
                text = details.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${details.rating}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${details.reviewCount} reviews",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(label = details.cuisine, icon = Icons.Default.Public)

                InfoChip(label = details.difficulty, icon = Icons.Default.Star)

                InfoChip(
                    label = details.mealType.firstOrNull() ?: "",
                    icon = Icons.Default.Bolt
                )
            }

            if (details.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    details.tags.forEach { tag ->
                        InfoChip(label = tag, icon = Icons.AutoMirrored.Filled.Label)
                    }
                }
            }
        }

        DetailSection(title = "At a Glance", icon = Icons.Default.Timer) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Default.Schedule,
                    label = "Prep",
                    value = "${details.prepTimeMinutes}m"
                )

                VerticalDivider(
                     modifier = Modifier.height(36.dp),
                     thickness = 1.dp,
                     color = Color.Gray.copy(alpha = 0.2f)
                 )

                StatItem(
                    icon = Icons.Default.Whatshot,
                    label = "Cook",
                    value = "${details.cookTimeMinutes}m"
                )

                VerticalDivider(
                    modifier = Modifier.height(36.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                StatItem(
                    icon = Icons.Default.Egg,
                    label = "Serves",
                    value = "${details.servings}"
                )

                VerticalDivider(
                    modifier = Modifier.height(36.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Cal",
                    value = "${details.caloriesPerServing}"
                )
            }
        }

        DetailSection(title = "Ingredients", icon = Icons.Default.Restaurant) {
            details.ingredients.forEach { ingredient ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier.padding(top = 7.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color = Color(0xFFFF6822))
                    ){

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = ingredient,
                            color = Color.DarkGray.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        DetailSection(title = "Instructions", icon = Icons.Default.Info) {
            details.instructions.forEachIndexed { index, instruction ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFFF6822)),
                        contentAlignment = Alignment.Center
                        ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = instruction,
                        color = Color.DarkGray.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
