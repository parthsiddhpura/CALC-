package com.example.model

enum class UnitCategory(val displayName: String, val iconName: String) {
    LENGTH("Length", "Straighten"),
    WEIGHT("Mass & Weight", "FitnessCenter"),
    TEMPERATURE("Temperature", "Thermostat"),
    AREA("Area", "SquareFoot"),
    SPEED("Speed", "Speed"),
    STORAGE("Digital Storage", "Storage"),
    VOLUME("Volume", "LocalDrink"),
    TIME("Time", "Schedule")
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    val factorToBase: Double, // Multiplier to convert this unit into the base unit of category
    val offset: Double = 0.0  // For temp (e.g. Kelvin/Celsius/Fahrenheit)
)

object UnitConverterData {
    val categories = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("Meters", "m", 1.0),
            ConversionUnit("Kilometers", "km", 1000.0),
            ConversionUnit("Centimeters", "cm", 0.01),
            ConversionUnit("Millimeters", "mm", 0.001),
            ConversionUnit("Miles", "mi", 1609.344),
            ConversionUnit("Yards", "yd", 0.9144),
            ConversionUnit("Feet", "ft", 0.3048),
            ConversionUnit("Inches", "in", 0.0254),
            ConversionUnit("Nautical Miles", "NM", 1852.0)
        ),
        UnitCategory.WEIGHT to listOf(
            ConversionUnit("Kilograms", "kg", 1.0),
            ConversionUnit("Grams", "g", 0.001),
            ConversionUnit("Milligrams", "mg", 0.000001),
            ConversionUnit("Metric Tons", "t", 1000.0),
            ConversionUnit("Pounds", "lbs", 0.45359237),
            ConversionUnit("Ounces", "oz", 0.028349523125),
            ConversionUnit("Stones", "st", 6.35029)
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("Celsius", "°C", 1.0, 0.0),
            ConversionUnit("Fahrenheit", "°F", 1.0, 0.0), // Handled specially in logic
            ConversionUnit("Kelvin", "K", 1.0, 0.0)
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("Square Meters", "m²", 1.0),
            ConversionUnit("Square Kilometers", "km²", 1000000.0),
            ConversionUnit("Square Feet", "ft²", 0.092903),
            ConversionUnit("Square Miles", "mi²", 2589988.11),
            ConversionUnit("Acres", "ac", 4046.86),
            ConversionUnit("Hectares", "ha", 10000.0)
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("Meters/second", "m/s", 1.0),
            ConversionUnit("Kilometers/hour", "km/h", 0.277778),
            ConversionUnit("Miles/hour", "mph", 0.44704),
            ConversionUnit("Knots", "kn", 0.514444),
            ConversionUnit("Mach", "M", 340.29)
        ),
        UnitCategory.STORAGE to listOf(
            ConversionUnit("Bytes", "B", 1.0),
            ConversionUnit("Kilobytes", "KB", 1024.0),
            ConversionUnit("Megabytes", "MB", 1048576.0),
            ConversionUnit("Gigabytes", "GB", 1073741824.0),
            ConversionUnit("Terabytes", "TB", 1099511627776.0),
            ConversionUnit("Petabytes", "PB", 1125899906842624.0)
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("Liters", "L", 1.0),
            ConversionUnit("Milliliters", "mL", 0.001),
            ConversionUnit("Cubic Meters", "m³", 1000.0),
            ConversionUnit("US Gallons", "gal", 3.78541),
            ConversionUnit("US Quarts", "qt", 0.946353),
            ConversionUnit("US Pints", "pt", 0.473176),
            ConversionUnit("US Cups", "cup", 0.24),
            ConversionUnit("US Fluid Ounces", "fl oz", 0.0295735)
        ),
        UnitCategory.TIME to listOf(
            ConversionUnit("Seconds", "s", 1.0),
            ConversionUnit("Milliseconds", "ms", 0.001),
            ConversionUnit("Minutes", "min", 60.0),
            ConversionUnit("Hours", "hr", 3600.0),
            ConversionUnit("Days", "days", 86400.0),
            ConversionUnit("Weeks", "wks", 604800.0),
            ConversionUnit("Years (365d)", "yrs", 31536000.0)
        )
    )

    fun convert(category: UnitCategory, value: Double, fromUnit: ConversionUnit, toUnit: ConversionUnit): Double {
        if (category == UnitCategory.TEMPERATURE) {
            val celsius = when (fromUnit.name) {
                "Celsius" -> value
                "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
                "Kelvin" -> value - 273.15
                else -> value
            }
            return when (toUnit.name) {
                "Celsius" -> celsius
                "Fahrenheit" -> (celsius * 9.0 / 5.0) + 32.0
                "Kelvin" -> celsius + 273.15
                else -> celsius
            }
        }
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }
}
