package com.example.model

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val country: String,
    val priority: Int = 100 // 1 for INR, 2 for USD, etc.
)

data class CurrencyRateResponse(
    val result: String? = null,
    val base_code: String = "INR",
    val time_last_update_utc: String? = null,
    val time_next_update_utc: String? = null,
    val rates: Map<String, Double> = emptyMap()
)
