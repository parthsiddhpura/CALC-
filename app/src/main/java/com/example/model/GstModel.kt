package com.example.model

enum class GstCalculationType {
    EXCLUSIVE, // GST Added (+GST)
    INCLUSIVE  // GST Removed (-GST / Extract GST)
}

data class GstSlab(
    val id: Int,
    val name: String,
    val ratePercent: Double,
    val label: String
)

data class GstResult(
    val netAmount: Double,
    val gstRate: Double,
    val gstAmount: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val grossAmount: Double,
    val type: GstCalculationType
)

data class GstHistoryEntry(
    val id: Long = System.currentTimeMillis(),
    val baseAmount: Double,
    val ratePercent: Double,
    val type: GstCalculationType,
    val gstAmount: Double,
    val grossAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
