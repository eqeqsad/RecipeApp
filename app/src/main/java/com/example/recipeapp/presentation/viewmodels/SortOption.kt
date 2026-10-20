package com.example.recipeapp.presentation.viewmodels

enum class SortOption(val label: String) {
    DEFAULT("Default"),
    RATING("Rating: high to low"),
    REVIEWS("Reviews: most first"),
    PREP_TIME("Prep time: shortest"),
    CALORIES("Calories: lowest"),
    NAME("Name: A-Z"),
}
