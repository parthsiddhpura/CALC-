package com.example.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToLong

data class DecimalConversionItem(
    val title: String,
    val value: String,
    val description: String
)

object DecimalConverterEngine {

    private val US_SYMBOLS = DecimalFormatSymbols(Locale.US)

    fun convertNumber(valueStr: String): List<DecimalConversionItem> {
        val cleanStr = valueStr.replace(",", "").trim()
        val num = cleanStr.toDoubleOrNull() ?: return emptyList()

        val results = mutableListOf<DecimalConversionItem>()

        // 1. Standard Decimal (High Precision)
        val standardDf = DecimalFormat("#,##0.##########", US_SYMBOLS)
        results.add(
            DecimalConversionItem(
                title = "Decimal",
                value = standardDf.format(num),
                description = "Standard base-10 numerical representation"
            )
        )

        // 2. Exact Fraction
        val fractionStr = toFraction(num)
        results.add(
            DecimalConversionItem(
                title = "Fraction",
                value = fractionStr,
                description = "Exact irreducible fraction / ratio"
            )
        )

        // 3. Fixed Currency / 2 Decimal Places
        val currencyDf = DecimalFormat("#,##0.00", US_SYMBOLS)
        results.add(
            DecimalConversionItem(
                title = "Currency (2 Decimals)",
                value = currencyDf.format(num),
                description = "Rounded to 2 decimal places"
            )
        )

        // 4. Percentage
        val percentVal = num * 100.0
        val percentDf = DecimalFormat("#,##0.####", US_SYMBOLS)
        results.add(
            DecimalConversionItem(
                title = "Percentage",
                value = "${percentDf.format(percentVal)}%",
                description = "Value multiplied by 100"
            )
        )

        // 5. Scientific Notation
        val sciDf = DecimalFormat("0.######E0", US_SYMBOLS)
        results.add(
            DecimalConversionItem(
                title = "Scientific Notation",
                value = sciDf.format(num),
                description = "Standard power of 10 exponential notation"
            )
        )

        // 6. Integer Base Representations if applicable
        if (num >= Long.MIN_VALUE.toDouble() && num <= Long.MAX_VALUE.toDouble() && num == floor(num)) {
            val longVal = num.toLong()
            results.add(
                DecimalConversionItem(
                    title = "Hexadecimal (HEX)",
                    value = "0x" + java.lang.Long.toHexString(longVal).uppercase(Locale.ROOT),
                    description = "Base-16 positional numeral"
                )
            )
            results.add(
                DecimalConversionItem(
                    title = "Binary (BIN)",
                    value = java.lang.Long.toBinaryString(longVal),
                    description = "Base-2 numeral"
                )
            )
            results.add(
                DecimalConversionItem(
                    title = "Octal (OCT)",
                    value = "0o" + java.lang.Long.toOctalString(longVal),
                    description = "Base-8 numeral"
                )
            )
        }

        return results
    }

    /**
     * Converts a floating point number to a simplified fraction using continued fractions algorithm.
     */
    fun toFraction(x: Double, maxDenominator: Long = 100000L): String {
        if (x.isNaN() || x.isInfinite()) return "N/A"
        val isNegative = x < 0
        val absX = abs(x)

        val wholePart = floor(absX).toLong()
        val fractionalPart = absX - wholePart

        if (fractionalPart < 1e-9) {
            return if (isNegative) "-$wholePart" else "$wholePart"
        }

        var h1: Long = 1
        var h0: Long = 0
        var k1: Long = 0
        var k0: Long = 1

        var b = fractionalPart
        do {
            val a = floor(b).toLong()
            var aux = h1
            h1 = a * h1 + h0
            h0 = aux
            aux = k1
            k1 = a * k1 + k0
            k0 = aux
            if (abs(b - a) < 1e-9) break
            b = 1.0 / (b - a)
        } while (abs(fractionalPart - h1.toDouble() / k1.toDouble()) > absX * 1e-7 && k1 <= maxDenominator)

        val gcd = gcd(h1, k1)
        val num = h1 / gcd
        val den = k1 / gcd

        val sign = if (isNegative) "-" else ""
        return if (wholePart > 0) {
            val improperNum = wholePart * den + num
            "$sign$wholePart ${num}/$den ($sign$improperNum/$den)"
        } else {
            "$sign$num/$den"
        }
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = a
        var y = b
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return if (x < 0) -x else x
    }
}
