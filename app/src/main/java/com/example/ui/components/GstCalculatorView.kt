package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.GstEngine
import com.example.model.GstCalculationType
import com.example.model.GstResult
import com.example.model.GstSlab
import com.example.model.ThemePalette

@Composable
fun GstCalculatorView(
    theme: ThemePalette,
    amountInput: String,
    calculationType: GstCalculationType,
    selectedSlabId: Int,
    slabs: List<GstSlab>,
    currentResult: GstResult?,
    grandTotalGross: Double,
    grandTotalGst: Double,
    calculationCount: Int,
    onInputDigit: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleType: () -> Unit,
    onSelectSlab: (GstSlab) -> Unit,
    onClearGrandTotal: () -> Unit,
    onUpdateSlabRate: (Int, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var showRateSetDialog by remember { mutableStateOf(false) }
    var editingSlab by remember { mutableStateOf<GstSlab?>(null) }
    var rateInputText by remember { mutableStateOf("") }
    var showGtBreakdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- 1. Big Casio LCD Style GST Screen & Breakdown Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(theme.borderWidthDp, theme.screenBorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = theme.screenBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Status bar in GST display: Mode Toggle (GST+ / GST-), Active Slab indicator, GT badge, Rate Set
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type selector pills (GST+ Added / GST- Removed)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (calculationType == GstCalculationType.EXCLUSIVE) theme.accentColor else theme.surfaceColor,
                            modifier = Modifier
                                .clickable {
                                    if (calculationType != GstCalculationType.EXCLUSIVE) onToggleType()
                                }
                                .testTag("btn_gst_exclusive")
                        ) {
                            Text(
                                text = "GST+ (Add)",
                                color = if (calculationType == GstCalculationType.EXCLUSIVE) theme.backgroundColor else theme.screenExpressionColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (calculationType == GstCalculationType.INCLUSIVE) theme.secondaryAccent else theme.surfaceColor,
                            modifier = Modifier
                                .clickable {
                                    if (calculationType != GstCalculationType.INCLUSIVE) onToggleType()
                                }
                                .testTag("btn_gst_inclusive")
                        ) {
                            Text(
                                text = "GST− (Extract)",
                                color = if (calculationType == GstCalculationType.INCLUSIVE) theme.backgroundColor else theme.screenExpressionColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // GT & Rate Set buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (calculationCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFB703).copy(alpha = 0.2f),
                                modifier = Modifier.clickable { showGtBreakdown = !showGtBreakdown }
                            ) {
                                Text(
                                    text = "GT: ${GstEngine.formatCurrency(grandTotalGross)}",
                                    color = Color(0xFFFFB703),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = theme.surfaceColor,
                            modifier = Modifier.clickable { showRateSetDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rate Set",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "RATE SET",
                                    color = theme.accentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Main Display Input Amount (Big & Prominent)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    val currentSlab = slabs.firstOrNull { it.id == selectedSlabId } ?: slabs[3]
                    Text(
                        text = if (calculationType == GstCalculationType.EXCLUSIVE) "BASE AMOUNT (BEFORE ${currentSlab.label} GST)" else "GROSS AMOUNT (INCL ${currentSlab.label} GST)",
                        color = theme.screenExpressionColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (amountInput.isEmpty()) "0" else amountInput,
                        color = theme.screenTextColor,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth().testTag("gst_main_amount")
                    )
                }

                // Spacious Detailed Breakdown Panel
                if (currentResult != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HorizontalDivider(
                            color = theme.screenBorderColor.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // 4-Quadrant / Row Tax Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Net Amount",
                                    color = theme.screenExpressionColor,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = GstEngine.formatCurrency(currentResult.netAmount),
                                    color = theme.screenTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "CGST (${currentResult.gstRate / 2}%)",
                                    color = theme.screenExpressionColor,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = GstEngine.formatCurrency(currentResult.cgstAmount),
                                    color = theme.accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SGST (${currentResult.gstRate / 2}%)",
                                    color = theme.screenExpressionColor,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = GstEngine.formatCurrency(currentResult.sgstAmount),
                                    color = theme.accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Tax",
                                    color = theme.screenExpressionColor,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "+${GstEngine.formatCurrency(currentResult.gstAmount)}",
                                    color = theme.accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Emphasized Gross Total Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.surfaceColor.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL GROSS",
                                    color = theme.screenExpressionColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = GstEngine.formatCurrency(currentResult.grossAmount),
                                    color = theme.secondaryAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- 2. Casio Dedicated GST Slab Buttons (Elevated upper bar right below display) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Label indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "── GST RATE SELECTION ──",
                    color = theme.screenExpressionColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // The Casio GST Slab Buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                slabs.forEach { slab ->
                    val isSelected = slab.id == selectedSlabId
                    CalculatorButton(
                        text = "${slab.name}\n${slab.label}",
                        onClick = { onSelectSlab(slab) },
                        theme = theme,
                        backgroundColor = if (isSelected) theme.accentColor else theme.functionButtonBg,
                        textColor = if (isSelected) theme.backgroundColor else theme.functionButtonText,
                        borderColor = if (isSelected) theme.accentColor else theme.functionButtonBorder,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_slab_${slab.id}"
                    )
                }

                // GST GT (Grand Total) button
                CalculatorButton(
                    text = "GST GT",
                    onClick = { showGtBreakdown = true },
                    theme = theme,
                    backgroundColor = if (calculationCount > 0) Color(0xFFFFB703) else theme.functionButtonBg,
                    textColor = if (calculationCount > 0) Color.Black else theme.functionButtonText,
                    borderColor = theme.functionButtonBorder,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_gst_gt"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. Full Number Keypad for GST Entry ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: AC, ⌫, 00, ÷
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "AC",
                    onClick = onClear,
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    borderColor = theme.functionButtonBorder,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_gst_ac"
                )
                CalculatorButton(
                    text = "⌫",
                    onClick = onBackspace,
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    borderColor = theme.functionButtonBorder,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_gst_backspace"
                )
                CalculatorButton(
                    text = "00",
                    onClick = { onInputDigit("00") },
                    theme = theme,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_gst_00"
                )
                CalculatorButton(
                    text = "÷",
                    onClick = { onInputDigit("÷") },
                    theme = theme,
                    backgroundColor = theme.operatorButtonBg,
                    textColor = theme.operatorButtonText,
                    borderColor = theme.operatorButtonBorder,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: 7, 8, 9, ×
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "7",
                    onClick = { onInputDigit("7") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "8",
                    onClick = { onInputDigit("8") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "9",
                    onClick = { onInputDigit("9") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "×",
                    onClick = { onInputDigit("×") },
                    theme = theme,
                    backgroundColor = theme.operatorButtonBg,
                    textColor = theme.operatorButtonText,
                    borderColor = theme.operatorButtonBorder,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: 4, 5, 6, −
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "4",
                    onClick = { onInputDigit("4") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "5",
                    onClick = { onInputDigit("5") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "6",
                    onClick = { onInputDigit("6") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "−",
                    onClick = { onInputDigit("−") },
                    theme = theme,
                    backgroundColor = theme.operatorButtonBg,
                    textColor = theme.operatorButtonText,
                    borderColor = theme.operatorButtonBorder,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4: 1, 2, 3, +
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "1",
                    onClick = { onInputDigit("1") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "2",
                    onClick = { onInputDigit("2") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "3",
                    onClick = { onInputDigit("3") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "+",
                    onClick = { onInputDigit("+") },
                    theme = theme,
                    backgroundColor = theme.operatorButtonBg,
                    textColor = theme.operatorButtonText,
                    borderColor = theme.operatorButtonBorder,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 5: 0, ., ±, =
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton(
                    text = "0",
                    onClick = { onInputDigit("0") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = ".",
                    onClick = { onInputDigit(".") },
                    theme = theme,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "TAX-",
                    onClick = onToggleType,
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "=",
                    onClick = {
                        // Apply current slab
                        val slab = slabs.firstOrNull { it.id == selectedSlabId } ?: slabs[3]
                        onSelectSlab(slab)
                    },
                    theme = theme,
                    backgroundBrush = theme.equalsButtonBrush,
                    textColor = theme.equalsButtonText,
                    borderColor = theme.equalsButtonBorder,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Rate Set Dialog
    if (showRateSetDialog) {
        AlertDialog(
            onDismissRequest = { showRateSetDialog = false },
            title = { Text("Custom GST Slabs (Rate Set)", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Customize the GST percentage rates for buttons GST+0 through GST+4:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    slabs.forEach { slab ->
                        var localRate by remember { mutableStateOf(slab.ratePercent.toString()) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = slab.name,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(70.dp)
                            )
                            OutlinedTextField(
                                value = localRate,
                                onValueChange = { newVal ->
                                    localRate = newVal
                                    val r = newVal.toDoubleOrNull()
                                    if (r != null && r >= 0.0) {
                                        onUpdateSlabRate(slab.id, r)
                                    }
                                },
                                label = { Text("Rate %") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRateSetDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Grand Total (GT) Breakdown Dialog
    if (showGtBreakdown) {
        AlertDialog(
            onDismissRequest = { showGtBreakdown = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Color(0xFFFFB703)
                    )
                    Text("GST Grand Total (GT)", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Accumulated across $calculationCount GST invoices in this session:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Invoices:")
                                Text("$calculationCount", fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total GST Tax:")
                                Text(GstEngine.formatCurrency(grandTotalGst), fontWeight = FontWeight.Bold, color = theme.accentColor)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Grand Total (Gross):", fontWeight = FontWeight.Bold)
                                Text(GstEngine.formatCurrency(grandTotalGross), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFFFB703))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGtBreakdown = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onClearGrandTotal()
                        showGtBreakdown = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear GT")
                }
            }
        )
    }
}
