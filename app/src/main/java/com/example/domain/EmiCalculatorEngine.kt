package com.example.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToLong

data class AmortizationMonth(
    val monthNumber: Int,
    val yearNumber: Int,
    val beginningBalance: Double,
    val emiAmount: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val endingBalance: Double
)

data class AmortizationYear(
    val yearNumber: Int,
    val principalPaid: Double,
    val interestPaid: Double,
    val totalPaid: Double,
    val endingBalance: Double
)

data class EmiResult(
    val principal: Double,
    val annualRate: Double,
    val tenureMonths: Int,
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principalPercent: Float,
    val interestPercent: Float,
    val yearlySchedule: List<AmortizationYear>,
    val monthlySchedule: List<AmortizationMonth>
)

object EmiCalculatorEngine {

    private val US_SYMBOLS = DecimalFormatSymbols(Locale.US)
    private val currencyDf = DecimalFormat("#,##0.00", US_SYMBOLS)
    private val compactDf = DecimalFormat("#,##0", US_SYMBOLS)

    fun calculateEmi(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int
    ): EmiResult {
        if (principal <= 0.0 || tenureMonths <= 0) {
            return EmiResult(
                principal = principal.coerceAtLeast(0.0),
                annualRate = annualRatePercent,
                tenureMonths = tenureMonths,
                monthlyEmi = 0.0,
                totalInterest = 0.0,
                totalPayment = 0.0,
                principalPercent = 100f,
                interestPercent = 0f,
                yearlySchedule = emptyList(),
                monthlySchedule = emptyList()
            )
        }

        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val monthlyEmi: Double
        val totalPayment: Double
        val totalInterest: Double

        if (monthlyRate == 0.0) {
            monthlyEmi = principal / tenureMonths
            totalPayment = principal
            totalInterest = 0.0
        } else {
            val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
            monthlyEmi = principal * monthlyRate * factor / (factor - 1.0)
            totalPayment = monthlyEmi * tenureMonths
            totalInterest = totalPayment - principal
        }

        val principalPercent = if (totalPayment > 0.0) {
            ((principal / totalPayment) * 100.0).toFloat()
        } else 100f

        val interestPercent = if (totalPayment > 0.0) {
            ((totalInterest / totalPayment) * 100.0).toFloat()
        } else 0f

        // Generate Amortization Schedule
        val monthlyList = mutableListOf<AmortizationMonth>()
        val yearlyList = mutableListOf<AmortizationYear>()

        var currentBalance = principal
        var currentYearPrincipal = 0.0
        var currentYearInterest = 0.0

        for (m in 1..tenureMonths) {
            val year = (m - 1) / 12 + 1
            val interestPart = if (monthlyRate > 0.0) currentBalance * monthlyRate else 0.0
            val principalPart = (monthlyEmi - interestPart).coerceAtMost(currentBalance)
            val endingBalance = (currentBalance - principalPart).coerceAtLeast(0.0)

            monthlyList.add(
                AmortizationMonth(
                    monthNumber = m,
                    yearNumber = year,
                    beginningBalance = currentBalance,
                    emiAmount = monthlyEmi,
                    principalPaid = principalPart,
                    interestPaid = interestPart,
                    endingBalance = endingBalance
                )
            )

            currentYearPrincipal += principalPart
            currentYearInterest += interestPart
            currentBalance = endingBalance

            if (m % 12 == 0 || m == tenureMonths) {
                yearlyList.add(
                    AmortizationYear(
                        yearNumber = year,
                        principalPaid = currentYearPrincipal,
                        interestPaid = currentYearInterest,
                        totalPaid = currentYearPrincipal + currentYearInterest,
                        endingBalance = currentBalance
                    )
                )
                currentYearPrincipal = 0.0
                currentYearInterest = 0.0
            }
        }

        return EmiResult(
            principal = principal,
            annualRate = annualRatePercent,
            tenureMonths = tenureMonths,
            monthlyEmi = monthlyEmi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principalPercent = principalPercent,
            interestPercent = interestPercent,
            yearlySchedule = yearlyList,
            monthlySchedule = monthlyList
        )
    }

    fun formatCurrency(value: Double): String {
        return currencyDf.format(value)
    }

    fun formatCompact(value: Double): String {
        return compactDf.format(value)
    }
}
