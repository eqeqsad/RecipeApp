package com.example.recipeapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorMessage(
    errorMessage: String?,
    onRetry:() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage ?:"",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF6822),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
               onRetry() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6822),
                contentColor = Color.White
            )
        ) {
            Text("Retry",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}