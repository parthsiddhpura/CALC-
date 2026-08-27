package com.example.domain

import kotlin.math.pow
import kotlin.math.roundToInt

enum class BmiCategory(
    val label: String,
    val description: String,
    val colorHex: Long, // 0xFF...
    val rangeStr: String
) {
    VERY_SEVERELY_UNDERWEIGHT("Severe Thinness", "Significant nutritional deficiency risk", 0xFF3A86FF, "< 16.0"),
    UNDERWEIGHT("Underweight", "Below recommended healthy body weight", 0xFF00B4D8, "16.0 - 18.4"),
    NORMAL("Normal Weight", "Optimal healthy weight range for height", 0xFF2EC4B6, "18.5 - 24.9"),
    OVERWEIGHT("Overweight", "Moderately elevated body mass", 0xFFFFB703, "25.0 - 29.9"),
    OBESE_CLASS_1("Obese (Class I)", "Moderate health risk category", 0xFFFB8500, "30.0 - 34.9"),
    OBESE_CLASS_2("Obese (Class II)", "High health risk category", 0xFFE63946, "35.0 - 39.9"),
    OBESE_CLASS_3("Obese (Class III)", "Severe health risk category", 0xFF9D0208, "≥ 40.0")
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory,
    val primeBmi: Double,
    val ponderalIndex: Double,
    val minHealthyWeightKg: Double,
    val maxHealthyWeightKg: Double,
    val idealWeightKg: Double,
    val weightDifferenceKg: Double, // Negative means need to gain, positive means need to lose
    val bmrKcal: Int,
    val maintenanceCaloriesKcal: Int,
    val gaugePercent: Float // 0.0f to 1.0f for graphical gauge
)

object BmiCalculatorEngine {

    fun calculate(
        weightKg: Double,
        heightCm: Double,
        age: Int = 25,
        isMale: Boolean = true
    ): BmiResult {
        if (heightCm <= 0 || weightKg <= 0) {
            return BmiResult(
                bmi = 0.0,
                category = BmiCategory.NORMAL,
                primeBmi = 0.0,
                ponderalIndex = 0.0,
                minHealthyWeightKg = 0.0,
                maxHealthyWeightKg = 0.0,
                idealWeightKg = 0.0,
                weightDifferenceKg = 0.0,
                bmrKcal = 0,
                maintenanceCaloriesKcal = 0,
                gaugePercent = 0.5f
            )
        }

        val heightMeters = heightCm / 100.0
        val bmi = weightKg / (heightMeters * heightMeters)
        val primeBmi = bmi / 25.0
        val ponderalIndex = weightKg / (heightMeters * heightMeters * heightMeters)

        val category = when {
            bmi < 16.0 -> BmiCategory.VERY_SEVERELY_UNDERWEIGHT
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            bmi < 35.0 -> BmiCategory.OBESE_CLASS_1
            bmi < 40.0 -> BmiCategory.OBESE_CLASS_2
            else -> BmiCategory.OBESE_CLASS_3
        }

        val minHealthyWeight = 18.5 * (heightMeters * heightMeters)
        val maxHealthyWeight = 24.9 * (heightMeters * heightMeters)
        val idealWeight = 21.7 * (heightMeters * heightMeters)
        val weightDiff = weightKg - idealWeight

        // Mifflin-St Jeor Equation
        val bmr = if (isMale) {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5
        } else {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161
        }
        val maintenanceCalories = (bmr * 1.35).roundToInt()

        // Gauge scale: 12.0 to 45.0 mapped to 0.0 to 1.0
        val gaugePercent = ((bmi - 12.0) / (42.0 - 12.0)).toFloat().coerceIn(0.02f, 0.98f)

        return BmiResult(
            bmi = (bmi * 10.0).roundToInt() / 10.0,
            category = category,
            primeBmi = (primeBmi * 100.0).roundToInt() / 100.0,
            ponderalIndex = (ponderalIndex * 10.0).roundToInt() / 10.0,
            minHealthyWeightKg = (minHealthyWeight * 10.0).roundToInt() / 10.0,
            maxHealthyWeightKg = (maxHealthyWeight * 10.0).roundToInt() / 10.0,
            idealWeightKg = (idealWeight * 10.0).roundToInt() / 10.0,
            weightDifferenceKg = (weightDiff * 10.0).roundToInt() / 10.0,
            bmrKcal = bmr.roundToInt().coerceAtLeast(0),
            maintenanceCaloriesKcal = maintenanceCalories.coerceAtLeast(0),
            gaugePercent = gaugePercent
        )
    }
}
