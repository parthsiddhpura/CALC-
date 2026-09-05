package com.example.domain

import com.example.model.WorksheetDocument
import com.example.model.WorksheetLine
import com.example.model.WorksheetLineType
import com.example.model.WorksheetTemplate
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.UUID

object WorksheetTapeEngine {

    private val numberFormatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    fun formatNumber(value: Double): String {
        return numberFormatter.format(value)
    }

    /**
     * Reactively recalculates all lines in the worksheet tape from top to bottom.
     * Maintains a variable map, running accumulator, and percentage delta computations.
     */
    fun recalculate(lines: List<WorksheetLine>): List<WorksheetLine> {
        val variables = mutableMapOf<String, Double>()
        var accumulator = 0.0
        var isFirstInBlock = true

        return lines.mapIndexed { index, line ->
            when (line.lineType) {
                WorksheetLineType.COMMENT_HEADER -> {
                    // Headers don't affect accumulator
                    line.copy(runningTotal = accumulator)
                }

                WorksheetLineType.VARIABLE_SET -> {
                    val numVal = parseNumberOrVariable(line.rawValue, variables)
                    line.variableName?.let { varName ->
                        if (varName.isNotBlank()) {
                            variables[varName.trim().lowercase()] = numVal
                        }
                    }
                    line.copy(
                        evaluatedNumber = numVal,
                        runningTotal = accumulator
                    )
                }

                WorksheetLineType.SUB_TOTAL -> {
                    // Subtotal captures the accumulator and optionally assigns it to a variable
                    line.variableName?.let { varName ->
                        if (varName.isNotBlank()) {
                            variables[varName.trim().lowercase()] = accumulator
                        }
                    }
                    line.copy(
                        evaluatedNumber = accumulator,
                        runningTotal = accumulator
                    )
                }

                WorksheetLineType.GRAND_TOTAL -> {
                    line.copy(
                        evaluatedNumber = accumulator,
                        runningTotal = accumulator
                    )
                }

                WorksheetLineType.PERCENTAGE -> {
                    val percentRate = parseNumberOrVariable(line.rawValue, variables)
                    val delta = accumulator * (percentRate / 100.0)
                    accumulator = when (line.operator) {
                        "-" -> accumulator - delta
                        "*" -> accumulator * (percentRate / 100.0)
                        else -> accumulator + delta
                    }
                    val finalDelta = if (line.operator == "-") -delta else delta
                    line.copy(
                        evaluatedNumber = percentRate,
                        percentageDelta = finalDelta,
                        runningTotal = accumulator
                    )
                }

                WorksheetLineType.CALCULATION -> {
                    val numVal = parseNumberOrVariable(line.rawValue, variables)
                    if (isFirstInBlock && (line.operator.isBlank() || line.operator == "+")) {
                        accumulator = numVal
                        isFirstInBlock = false
                    } else {
                        accumulator = when (line.operator) {
                            "-" -> accumulator - numVal
                            "*" -> accumulator * numVal
                            "/" -> if (numVal != 0.0) accumulator / numVal else accumulator
                            else -> accumulator + numVal
                        }
                    }

                    // If line defines an output variable (= Price)
                    line.variableName?.let { varName ->
                        if (varName.isNotBlank()) {
                            variables[varName.trim().lowercase()] = accumulator
                        }
                    }

                    line.copy(
                        evaluatedNumber = numVal,
                        runningTotal = accumulator
                    )
                }
            }
        }
    }

    private fun parseNumberOrVariable(input: String, variables: Map<String, Double>): Double {
        val trimmed = input.trim()
        val varMatch = variables[trimmed.lowercase()]
        if (varMatch != null) return varMatch

        val cleaned = trimmed.replace(",", "").replace("%", "")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    /**
     * Generate exportable plain text formatted receipt tape.
     */
    fun exportToPlainText(doc: WorksheetDocument): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("          ${doc.title.uppercase()}        ")
        sb.appendLine("==========================================")

        doc.lines.forEach { line ->
            when (line.lineType) {
                WorksheetLineType.COMMENT_HEADER -> {
                    sb.appendLine()
                    sb.appendLine("# ${line.note.ifBlank { line.rawValue }}")
                }
                WorksheetLineType.VARIABLE_SET -> {
                    sb.appendLine("${line.variableName ?: "Var"} = ${formatNumber(line.evaluatedNumber)}")
                }
                WorksheetLineType.SUB_TOTAL -> {
                    sb.appendLine("------------------------------------------")
                    val varNote = if (!line.variableName.isNullOrBlank()) " = ${line.variableName}" else ""
                    val comment = if (line.note.isNotBlank()) " ${line.note}" else ""
                    sb.appendLine("+  ${formatNumber(line.evaluatedNumber)}$varNote$comment")
                }
                WorksheetLineType.GRAND_TOTAL -> {
                    sb.appendLine("==========================================")
                    sb.appendLine("TOTAL: ${formatNumber(line.evaluatedNumber)}")
                }
                WorksheetLineType.PERCENTAGE -> {
                    val deltaStr = line.percentageDelta?.let { " | ${if (it >= 0) "+" else ""}${formatNumber(it)}" } ?: ""
                    val comment = if (line.note.isNotBlank()) " ${line.note}" else ""
                    sb.appendLine("${line.operator}  ${formatNumber(line.evaluatedNumber)}%$deltaStr$comment")
                }
                WorksheetLineType.CALCULATION -> {
                    val comment = if (line.note.isNotBlank()) " ${line.note}" else ""
                    val varRef = if (line.rawValue.matches(Regex("[a-zA-Z_]+"))) " ${line.rawValue}" else ""
                    val targetVar = if (!line.variableName.isNullOrBlank()) " = ${line.variableName}" else ""
                    val numDisplay = if (varRef.isNotBlank()) varRef else formatNumber(line.evaluatedNumber)
                    sb.appendLine("${line.operator}  $numDisplay$targetVar$comment")
                }
            }
        }
        sb.appendLine("==========================================")
        sb.appendLine("Grand Total: ${formatNumber(doc.grandTotal)}")
        sb.appendLine("Generated with ChromaTape Worksheet Calculator")
        return sb.toString()
    }

    /**
     * Generate exportable Markdown receipt format.
     */
    fun exportToMarkdown(doc: WorksheetDocument): String {
        val sb = StringBuilder()
        sb.appendLine("## 📋 ${doc.title}")
        sb.appendLine()
        sb.appendLine("| Op | Amount | Notes / Variables | Subtotal |")
        sb.appendLine("|:---:|:---:|:---|:---:|")

        doc.lines.forEach { line ->
            when (line.lineType) {
                WorksheetLineType.COMMENT_HEADER -> {
                    sb.appendLine("| 📝 | **Section** | **${line.note.ifBlank { line.rawValue }}** | - |")
                }
                WorksheetLineType.VARIABLE_SET -> {
                    sb.appendLine("| 🏷️ | `${line.variableName} = ${formatNumber(line.evaluatedNumber)}` | Variable Definition | - |")
                }
                WorksheetLineType.SUB_TOTAL -> {
                    val label = if (!line.variableName.isNullOrBlank()) "Subtotal = ${line.variableName}" else "Subtotal"
                    sb.appendLine("| ➖ | **${formatNumber(line.evaluatedNumber)}** | **$label ${line.note}** | **${formatNumber(line.runningTotal)}** |")
                }
                WorksheetLineType.GRAND_TOTAL -> {
                    sb.appendLine("| 🏁 | **${formatNumber(line.evaluatedNumber)}** | **Grand Total** | **${formatNumber(line.runningTotal)}** |")
                }
                WorksheetLineType.PERCENTAGE -> {
                    val deltaStr = line.percentageDelta?.let { " (${if (it >= 0) "+" else ""}${formatNumber(it)})" } ?: ""
                    sb.appendLine("| `${line.operator}` | `${formatNumber(line.evaluatedNumber)}%` | ${line.note}$deltaStr | `${formatNumber(line.runningTotal)}` |")
                }
                WorksheetLineType.CALCULATION -> {
                    val varRef = if (line.rawValue.matches(Regex("[a-zA-Z_]+"))) line.rawValue else formatNumber(line.evaluatedNumber)
                    val targetVar = if (!line.variableName.isNullOrBlank()) " = ${line.variableName}" else ""
                    sb.appendLine("| `${line.operator}` | `$varRef` | ${line.note}$targetVar | `${formatNumber(line.runningTotal)}` |")
                }
            }
        }
        sb.appendLine()
        sb.appendLine("> **Grand Total**: `${formatNumber(doc.grandTotal)}`")
        return sb.toString()
    }

    /**
     * Pre-packaged professional templates matching user screenshots and everyday business needs.
     */
    fun getDefaultTemplates(): List<WorksheetTemplate> {
        return listOf(
            WorksheetTemplate(
                title = "Design Candle Cost",
                description = "Candle making cost per unit, volume multiplier & pickup discount",
                iconName = "LocalOffer",
                lines = listOf(
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Working with Variables"),
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Design candle cost calculation"),
                    WorksheetLine(lineType = WorksheetLineType.VARIABLE_SET, variableName = "Quantity", rawValue = "25", evaluatedNumber = 25.0),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "12.50", evaluatedNumber = 12.50, note = "wax & wick"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "6.00", evaluatedNumber = 6.00, note = "labor"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "2.50", evaluatedNumber = 2.50, note = "jar & label"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "21.00", evaluatedNumber = 21.00, note = "unit price"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "*", rawValue = "Quantity", note = ""),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "525.00", variableName = "Price", note = "Total Price"),
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Pickup option"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "Price", note = "Base Price"),
                    WorksheetLine(lineType = WorksheetLineType.PERCENTAGE, operator = "-", rawValue = "15.00", evaluatedNumber = 15.0, note = "pickup discount")
                )
            ),
            WorksheetTemplate(
                title = "Shopping & Groceries",
                description = "Weekly household shopping with itemized notes and voucher promo",
                iconName = "ShoppingCart",
                lines = listOf(
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "CalcTape - Weekly Shopping"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "45.80", evaluatedNumber = 45.80, note = "Groceries & drinks"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "12.40", evaluatedNumber = 12.40, note = "Household items"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "7.50", evaluatedNumber = 7.50, note = "Magazines"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "65.70", note = "Subtotal"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "-", rawValue = "5.00", evaluatedNumber = 5.00, note = "Voucher (promo)"),
                    WorksheetLine(lineType = WorksheetLineType.GRAND_TOTAL, operator = "+", rawValue = "60.70", note = "Total Due")
                )
            ),
            WorksheetTemplate(
                title = "Invoice & Tax Billing",
                description = "Hardware order with quantity multiplier, 19% VAT/GST and cash discount",
                iconName = "Receipt",
                lines = listOf(
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Order computers:"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "999.00", evaluatedNumber = 999.00, note = "$ single price"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "*", rawValue = "6.00", evaluatedNumber = 6.00, note = "number of items"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "5994.00", note = "Gross Amount"),
                    WorksheetLine(lineType = WorksheetLineType.PERCENTAGE, operator = "+", rawValue = "19.00", evaluatedNumber = 19.0, note = "VAT / GST"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "7132.86", note = "total"),
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Cash Discount:"),
                    WorksheetLine(lineType = WorksheetLineType.PERCENTAGE, operator = "-", rawValue = "3.00", evaluatedNumber = 3.0, note = "early pay discount"),
                    WorksheetLine(lineType = WorksheetLineType.GRAND_TOTAL, operator = "+", rawValue = "6918.87", note = "Final Payable")
                )
            ),
            WorksheetTemplate(
                title = "Travel Budget & Trip",
                description = "Flight, hotel, car rental, food and currency exchange buffer",
                iconName = "Flight",
                lines = listOf(
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Summer Trip Budget"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "340.00", evaluatedNumber = 340.00, note = "Roundtrip Flights"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "450.00", evaluatedNumber = 450.00, note = "Hotel (4 Nights)"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "120.00", evaluatedNumber = 120.00, note = "Car Rental & Fuel"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "200.00", evaluatedNumber = 200.00, note = "Dining & Food"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "1110.00", note = "Trip Subtotal"),
                    WorksheetLine(lineType = WorksheetLineType.PERCENTAGE, operator = "+", rawValue = "10.00", evaluatedNumber = 10.0, note = "Emergency buffer"),
                    WorksheetLine(lineType = WorksheetLineType.GRAND_TOTAL, operator = "+", rawValue = "1221.00", note = "Target Savings")
                )
            ),
            WorksheetTemplate(
                title = "Project Hours & Timesheet",
                description = "Freelance consulting hours multiplied by hourly rate with deduction",
                iconName = "Schedule",
                lines = listOf(
                    WorksheetLine(lineType = WorksheetLineType.COMMENT_HEADER, note = "Freelance Monthly Timesheet"),
                    WorksheetLine(lineType = WorksheetLineType.VARIABLE_SET, variableName = "HourlyRate", rawValue = "45.00", evaluatedNumber = 45.0),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "35.00", evaluatedNumber = 35.00, note = "Dev Hours Week 1-2"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "+", rawValue = "28.00", evaluatedNumber = 28.00, note = "Dev Hours Week 3-4"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "63.00", note = "Total Hours"),
                    WorksheetLine(lineType = WorksheetLineType.CALCULATION, operator = "*", rawValue = "HourlyRate", note = "rate per hr"),
                    WorksheetLine(lineType = WorksheetLineType.SUB_TOTAL, operator = "+", rawValue = "2835.00", note = "Gross Billing"),
                    WorksheetLine(lineType = WorksheetLineType.PERCENTAGE, operator = "-", rawValue = "10.00", evaluatedNumber = 10.0, note = "TDS / Withholding Tax"),
                    WorksheetLine(lineType = WorksheetLineType.GRAND_TOTAL, operator = "+", rawValue = "2551.50", note = "Net Payout")
                )
            )
        )
    }
}
