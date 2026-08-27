package com.example.domain

import com.example.model.GstCalculationType
import com.example.model.GstResult
import com.example.model.GstSlab
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object GstEngine {

    val DEFAULT_SLABS = listOf(
        GstSlab(id = 0, name = "GST+0", ratePercent = 0.0, label = "0%"),
        GstSlab(id = 1, name = "GST+1", ratePercent = 5.0, label = "5%"),
        GstSlab(id = 2, name = "GST+2", ratePercent = 12.0, label = "12%"),
        GstSlab(id = 3, name = "GST+3", ratePercent = 18.0, label = "18%"),
        GstSlab(id = 4, name = "GST+4", ratePercent = 28.0, label = "28%")
    )

    private val US_SYMBOLS = DecimalFormatSymbols(Locale.US)
    private val currencyDf = DecimalFormat("#,##0.00", US_SYMBOLS)

    fun calculate(
        amount: Double,
        ratePercent: Double,
        type: GstCalculationType
    ): GstResult {
        val bdAmount = BigDecimal.valueOf(amount)
        val bdRate = BigDecimal.valueOf(ratePercent)
        val hundred = BigDecimal.valueOf(100.0)

        if (type == GstCalculationType.EXCLUSIVE) {
            // GST Added: Amount is Net (Excluding GST)
            // GST Amount = Amount * (Rate / 100)
            // Gross Amount = Amount + GST Amount
            val gstAmount = bdAmount.multiply(bdRate).divide(hundred, 4, RoundingMode.HALF_UP)
            val grossAmount = bdAmount.add(gstAmount)
            val halfGst = gstAmount.divide(BigDecimal.valueOf(2.0), 4, RoundingMode.HALF_UP)

            return GstResult(
                netAmount = bdAmount.toDouble(),
                gstRate = ratePercent,
                gstAmount = gstAmount.setScale(2, RoundingMode.HALF_UP).toDouble(),
                cgstAmount = halfGst.setScale(2, RoundingMode.HALF_UP).toDouble(),
                sgstAmount = halfGst.setScale(2, RoundingMode.HALF_UP).toDouble(),
                grossAmount = grossAmount.setScale(2, RoundingMode.HALF_UP).toDouble(),
                type = type
            )
        } else {
            // GST Removed: Amount is Gross (Including GST)
            // Net Amount = Amount / (1 + Rate / 100) = Amount * 100 / (100 + Rate)
            // GST Amount = Amount - Net Amount
            val divisor = hundred.add(bdRate)
            val netAmount = if (divisor.toDouble() > 0.0) {
                bdAmount.multiply(hundred).divide(divisor, 4, RoundingMode.HALF_UP)
            } else {
                bdAmount
            }
            val gstAmount = bdAmount.subtract(netAmount)
            val halfGst = gstAmount.divide(BigDecimal.valueOf(2.0), 4, RoundingMode.HALF_UP)

            return GstResult(
                netAmount = netAmount.setScale(2, RoundingMode.HALF_UP).toDouble(),
                gstRate = ratePercent,
                gstAmount = gstAmount.setScale(2, RoundingMode.HALF_UP).toDouble(),
                cgstAmount = halfGst.setScale(2, RoundingMode.HALF_UP).toDouble(),
                sgstAmount = halfGst.setScale(2, RoundingMode.HALF_UP).toDouble(),
                grossAmount = bdAmount.setScale(2, RoundingMode.HALF_UP).toDouble(),
                type = type
            )
        }
    }

    fun formatCurrency(value: Double): String {
        return currencyDf.format(value)
    }
}
