package com.example.model

enum class ChainNodeType {
    SOURCE_INPUT,           // Base value, e.g. Salary ₹75,000
    SUBTRACT_AMOUNT,        // Subtract fixed amount, e.g. Expenses -₹35,000
    ADD_AMOUNT,             // Add fixed amount, e.g. Bonus +₹10,000
    MULTIPLY_VALUE,         // Multiply by factor, e.g. x 1.2
    DIVIDE_VALUE,           // Divide by factor, e.g. / 2
    PERCENTAGE_DEDUCT,      // Deduct %, e.g. -18% GST/Tax
    PERCENTAGE_ADD,         // Add %, e.g. +15% Markup
    PERCENTAGE_ALLOCATE,    // Take % of previous, e.g. 62.5% of Savings into SIP
    COMPOUND_GROWTH,        // Compound growth: SIP/Lump sum for N years at R% CAGR
    CUSTOM_FORMULA          // Free-form formula using {prev} or named nodes
}

data class ChainNode(
    val id: String,
    val title: String,
    val type: ChainNodeType,
    val primaryValue: Double,               // e.g. 75000, 35000, 12 (%)
    val secondaryValue: Double = 0.0,       // e.g. years = 15 for compounding
    val unit: String = "₹",
    val description: String = "",
    val formulaString: String = "",
    val minValue: Double = 0.0,
    val maxValue: Double = 1000000.0,
    val step: Double = 1.0,
    val calculatedOutput: Double = 0.0
)

data class CalculationChain(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val nodes: List<ChainNode>,
    val isUserCreated: Boolean = false
)

data class ChainScenario(
    val name: String,
    val multiplier: Double = 1.0,
    val rateOffset: Double = 0.0
)
