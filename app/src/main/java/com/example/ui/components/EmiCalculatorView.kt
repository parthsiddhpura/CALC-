package com.example.ui.components

import androidx.compose.ui.graphics.luminance
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AmortizationYear
import com.example.domain.EmiCalculatorEngine
import com.example.domain.EmiResult
import com.example.model.ThemePalette

@Composable
fun EmiCalculatorView(
    theme: ThemePalette,
    principalInput: String,
    interestRateInput: String,
    tenureYearsInput: String,
    isTenureInYears: Boolean,
    onPrincipalChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onTenureChange: (String) -> Unit,
    onToggleTenureUnit: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showAmortization by remember { mutableStateOf(false) }

    val principal = principalInput.replace(",", "").replace(" ", "").toDoubleOrNull() ?: 1000000.0
    val annualRate = interestRateInput.replace(",", "").replace(" ", "").toDoubleOrNull() ?: 8.5
    val tenureVal = tenureYearsInput.replace(",", "").replace(" ", "").toIntOrNull() ?: 20
    val tenureMonths = if (isTenureInYears) tenureVal * 12 else tenureVal

    val emiResult: EmiResult = remember(principal, annualRate, tenureMonths) {
        EmiCalculatorEngine.calculateEmi(principal, annualRate, tenureMonths)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Top Hero EMI Summary Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(theme.borderWidthDp, theme.screenBorderColor, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = theme.screenBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTHLY EMI AMOUNT",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Surface(
                        color = theme.secondaryAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${emiResult.tenureMonths} Months / ${emiResult.tenureMonths / 12} Yrs",
                            color = theme.secondaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Big Monthly EMI Number
                Text(
                    text = EmiCalculatorEngine.formatCurrency(emiResult.monthlyEmi),
                    color = theme.screenTextColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.testTag("emi_monthly_amount")
                )

                // Visual Ratio Bar: Principal vs Total Interest
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.surfaceColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight((emiResult.principalPercent.coerceAtLeast(1f)))
                                .height(8.dp)
                                .background(theme.accentColor)
                        )
                        Box(
                            modifier = Modifier
                                .weight((emiResult.interestPercent.coerceAtLeast(1f)))
                                .height(8.dp)
                                .background(theme.secondaryAccent)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(theme.accentColor))
                            Text(
                                text = "Principal: ${String.format("%.1f", emiResult.principalPercent)}%",
                                color = theme.screenExpressionColor,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(theme.secondaryAccent))
                            Text(
                                text = "Interest: ${String.format("%.1f", emiResult.interestPercent)}%",
                                color = theme.screenExpressionColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                HorizontalDivider(color = theme.screenBorderColor.copy(alpha = 0.3f))

                // Breakdown Totals: Principal, Interest, Total Payable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Interest", color = theme.screenExpressionColor, fontSize = 11.sp)
                        Text(
                            text = EmiCalculatorEngine.formatCurrency(emiResult.totalInterest),
                            color = theme.secondaryAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Payment", color = theme.screenExpressionColor, fontSize = 11.sp)
                        Text(
                            text = EmiCalculatorEngine.formatCurrency(emiResult.totalPayment),
                            color = theme.screenTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // --- 2. Interactive Input Controls (Amount, Rate, Tenure) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = theme.surfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Principal Loan Amount
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Loan Amount",
                            color = theme.screenTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = EmiCalculatorEngine.formatCompact(principal),
                            color = theme.accentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedTextField(
                        value = principalInput,
                        onValueChange = onPrincipalChange,
                        modifier = Modifier.fillMaxWidth().testTag("input_principal"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = theme.screenTextColor,
                            unfocusedTextColor = theme.screenTextColor,
                            focusedContainerColor = theme.cardBackground,
                            unfocusedContainerColor = theme.cardBackground,
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.screenBorderColor
                        )
                    )

                    // Quick Chips for Principal
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val presets = listOf(100000.0, 500000.0, 1000000.0, 2500000.0, 5000000.0)
                        items(presets) { pVal ->
                            Surface(
                                color = theme.cardBackground,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.screenBorderColor.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onPrincipalChange(pVal.toLong().toString()) }
                            ) {
                                Text(
                                    text = EmiCalculatorEngine.formatCompact(pVal),
                                    color = theme.screenTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Annual Interest Rate
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interest Rate (% p.a.)",
                            color = theme.screenTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$annualRate%",
                            color = theme.accentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedTextField(
                        value = interestRateInput,
                        onValueChange = onRateChange,
                        modifier = Modifier.fillMaxWidth().testTag("input_rate"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = theme.screenTextColor,
                            unfocusedTextColor = theme.screenTextColor,
                            focusedContainerColor = theme.cardBackground,
                            unfocusedContainerColor = theme.cardBackground,
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.screenBorderColor
                        )
                    )

                    // Loan Type Preset Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val loanTypes = listOf(
                            "Home (8.5%)" to "8.5",
                            "Car (9.2%)" to "9.2",
                            "Personal (12.5%)" to "12.5",
                            "Education (9.0%)" to "9.0"
                        )
                        items(loanTypes) { (name, rVal) ->
                            Surface(
                                color = theme.cardBackground,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.screenBorderColor.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onRateChange(rVal) }
                            ) {
                                Text(
                                    text = name,
                                    color = theme.screenTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Loan Tenure
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Loan Tenure",
                            color = theme.screenTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Toggle Years / Months
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isTenureInYears) theme.accentColor else theme.cardBackground,
                                border = if (!isTenureInYears) androidx.compose.foundation.BorderStroke(1.dp, theme.screenBorderColor.copy(alpha = 0.5f)) else null,
                                modifier = Modifier.clickable { onToggleTenureUnit(true) }
                            ) {
                                Text(
                                    text = "Years",
                                    color = if (isTenureInYears) (if (theme.accentColor.luminance() > 0.6f) Color(0xFF211118) else Color.White) else theme.screenTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (!isTenureInYears) theme.accentColor else theme.cardBackground,
                                border = if (isTenureInYears) androidx.compose.foundation.BorderStroke(1.dp, theme.screenBorderColor.copy(alpha = 0.5f)) else null,
                                modifier = Modifier.clickable { onToggleTenureUnit(false) }
                            ) {
                                Text(
                                    text = "Months",
                                    color = if (!isTenureInYears) (if (theme.accentColor.luminance() > 0.6f) Color(0xFF211118) else Color.White) else theme.screenTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tenureYearsInput,
                        onValueChange = onTenureChange,
                        modifier = Modifier.fillMaxWidth().testTag("input_tenure"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = theme.screenTextColor,
                            unfocusedTextColor = theme.screenTextColor,
                            focusedContainerColor = theme.cardBackground,
                            unfocusedContainerColor = theme.cardBackground,
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.screenBorderColor
                        )
                    )

                    // Quick Tenure Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val tenurePresets = if (isTenureInYears) listOf(5, 10, 15, 20, 25, 30) else listOf(12, 24, 36, 60, 120, 240)
                        items(tenurePresets) { tVal ->
                            Surface(
                                color = theme.cardBackground,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.screenBorderColor.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onTenureChange(tVal.toString()) }
                            ) {
                                Text(
                                    text = if (isTenureInYears) "$tVal Yrs" else "$tVal Mo",
                                    color = theme.screenTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Amortization Schedule Table (Year-by-Year) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = theme.surfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAmortization = !showAmortization },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Yearly Repayment Schedule",
                            color = theme.screenTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = if (showAmortization) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Schedule",
                        tint = theme.screenExpressionColor
                    )
                }

                AnimatedVisibility(visible = showAmortization) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(theme.cardBackground, RoundedCornerShape(6.dp))
                                .border(1.dp, theme.screenBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Year", color = theme.screenTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                            Text("Principal", color = theme.screenTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("Interest", color = theme.screenTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text("Balance", color = theme.screenTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                        }

                        // Rows
                        emiResult.yearlySchedule.forEach { yItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Yr ${yItem.yearNumber}", color = theme.screenTextColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(36.dp))
                                Text(EmiCalculatorEngine.formatCompact(yItem.principalPaid), color = theme.accentColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text(EmiCalculatorEngine.formatCompact(yItem.interestPaid), color = theme.secondaryAccent, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text(EmiCalculatorEngine.formatCompact(yItem.endingBalance), color = theme.screenTextColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }
    }
}
