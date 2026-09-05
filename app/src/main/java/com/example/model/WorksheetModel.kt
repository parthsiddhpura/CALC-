package com.example.model

import java.util.UUID

enum class WorksheetLineType {
    CALCULATION,      // + 12.50 wax & wick
    PERCENTAGE,       // - 15.00% | -78.75 pickup discount
    SUB_TOTAL,        // ------------------ 21.00 unit price
    GRAND_TOTAL,      // ================== 541.50 Total
    VARIABLE_SET,     // Quantity = 25 or Price = 525.00
    COMMENT_HEADER    // # Design candle cost calculation
}

data class WorksheetLine(
    val id: String = UUID.randomUUID().toString(),
    val lineType: WorksheetLineType = WorksheetLineType.CALCULATION,
    val operator: String = "+", // "+", "-", "*", "/", "%", "="
    val rawValue: String = "0", // "12.50", "25", "15"
    val evaluatedNumber: Double = 0.0,
    val percentageDelta: Double? = null, // for % lines: the calculated delta
    val runningTotal: Double = 0.0,
    val variableName: String? = null, // e.g. "Quantity", "Price"
    val note: String = "" // e.g. "wax & wick", "pickup discount"
)

data class WorksheetDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Worksheet",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lines: List<WorksheetLine> = emptyList(),
    val grandTotal: Double = 0.0
)

data class WorksheetTemplate(
    val title: String,
    val description: String,
    val iconName: String,
    val lines: List<WorksheetLine>
)
