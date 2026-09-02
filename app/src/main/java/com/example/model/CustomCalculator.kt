package com.example.model

data class CustomInputField(
    val id: String,
    val label: String,
    val defaultValue: Double,
    val currentValue: Double = defaultValue,
    val minValue: Double = 0.0,
    val maxValue: Double = 100000.0,
    val step: Double = 1.0,
    val unit: String = "",
    val isSlider: Boolean = true,
    val helpText: String = ""
)

data class CustomOutputField(
    val id: String,
    val label: String,
    val formula: String,
    val unit: String = "",
    val isHighlighted: Boolean = false,
    val description: String = ""
)

data class CustomCalculator(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconName: String = "Calculate",
    val inputs: List<CustomInputField>,
    val outputs: List<CustomOutputField>,
    val isUserCreated: Boolean = false,
    val hasInternetData: Boolean = false,
    val dataSourceLabel: String = ""
)
