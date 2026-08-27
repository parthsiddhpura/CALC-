package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConversionUnit
import com.example.model.ThemePalette
import com.example.model.UnitCategory
import com.example.model.UnitConverterData
import java.util.Locale

@Composable
fun UnitConverterView(
    theme: ThemePalette,
    category: UnitCategory,
    fromUnit: ConversionUnit,
    toUnit: ConversionUnit,
    inputValue: String,
    outputValue: String,
    onCategorySelect: (UnitCategory) -> Unit,
    onFromUnitSelect: (ConversionUnit) -> Unit,
    onToUnitSelect: (ConversionUnit) -> Unit,
    onSwapUnits: () -> Unit,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val availableUnits = UnitConverterData.categories[category] ?: emptyList()

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category Selector Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(UnitCategory.entries.toTypedArray()) { cat ->
                val isSelected = cat == category
                Surface(
                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { onCategorySelect(cat) }
                ) {
                    Text(
                        text = cat.displayName,
                        color = if (isSelected) theme.backgroundColor else theme.screenExpressionColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // From Unit Card
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(16.dp),
            border = if (theme.borderWidthDp > 0.dp) androidx.compose.foundation.BorderStroke(theme.borderWidthDp, theme.screenBorderColor.copy(alpha = 0.5f)) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FROM",
                        color = theme.screenExpressionColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Unit dropdown trigger
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.cardBackground)
                                .clickable { showFromMenu = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${fromUnit.name} (${fromUnit.symbol})",
                                color = theme.accentColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select from unit",
                                tint = theme.accentColor
                            )
                        }

                        DropdownMenu(
                            expanded = showFromMenu,
                            onDismissRequest = { showFromMenu = false },
                            modifier = Modifier.background(theme.cardBackground)
                        ) {
                            availableUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${unit.name} (${unit.symbol})",
                                            color = theme.screenTextColor,
                                            fontWeight = if (unit == fromUnit) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onFromUnitSelect(unit)
                                        showFromMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = inputValue.ifEmpty { "0" },
                    color = theme.screenTextColor,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }

        // Swap button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onSwapUnits,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.accentColor)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap units",
                    tint = theme.backgroundColor
                )
            }
        }

        // To Unit Card
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(16.dp),
            border = if (theme.borderWidthDp > 0.dp) androidx.compose.foundation.BorderStroke(theme.borderWidthDp, theme.screenBorderColor.copy(alpha = 0.5f)) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TO (RESULT)",
                        color = theme.screenExpressionColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Unit dropdown trigger
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.cardBackground)
                                .clickable { showToMenu = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${toUnit.name} (${toUnit.symbol})",
                                color = theme.secondaryAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select to unit",
                                tint = theme.secondaryAccent
                            )
                        }

                        DropdownMenu(
                            expanded = showToMenu,
                            onDismissRequest = { showToMenu = false },
                            modifier = Modifier.background(theme.cardBackground)
                        ) {
                            availableUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${unit.name} (${unit.symbol})",
                                            color = theme.screenTextColor,
                                            fontWeight = if (unit == toUnit) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onToUnitSelect(unit)
                                        showToMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = outputValue.ifEmpty { "0" },
                    color = theme.secondaryAccent,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }

        // Mini Numeric Keypad for unit inputs
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keyHeight = 48.dp
            val colSpacing = 8.dp

            // Row 1: 7, 8, 9, ⌫
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(
                    text = "7",
                    onClick = { onInputChange(if (inputValue == "0") "7" else inputValue + "7") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "8",
                    onClick = { onInputChange(if (inputValue == "0") "8" else inputValue + "8") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "9",
                    onClick = { onInputChange(if (inputValue == "0") "9" else inputValue + "9") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "⌫",
                    onClick = {
                        val next = if (inputValue.length > 1) inputValue.dropLast(1) else "0"
                        onInputChange(next)
                    },
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: 4, 5, 6, C
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(
                    text = "4",
                    onClick = { onInputChange(if (inputValue == "0") "4" else inputValue + "4") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "5",
                    onClick = { onInputChange(if (inputValue == "0") "5" else inputValue + "5") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "6",
                    onClick = { onInputChange(if (inputValue == "0") "6" else inputValue + "6") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "C",
                    onClick = { onInputChange("0") },
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: 1, 2, 3, ±
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(
                    text = "1",
                    onClick = { onInputChange(if (inputValue == "0") "1" else inputValue + "1") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "2",
                    onClick = { onInputChange(if (inputValue == "0") "2" else inputValue + "2") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "3",
                    onClick = { onInputChange(if (inputValue == "0") "3" else inputValue + "3") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "±",
                    onClick = {
                        val next = if (inputValue.startsWith("-")) inputValue.removePrefix("-") else if (inputValue != "0") "-$inputValue" else "0"
                        onInputChange(next)
                    },
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4: 0 (span 2), ., 00
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(
                    text = "0",
                    onClick = { if (inputValue != "0") onInputChange(inputValue + "0") },
                    theme = theme,
                    modifier = Modifier.weight(2f)
                )
                CalculatorButton(
                    text = ".",
                    onClick = { if (!inputValue.contains(".")) onInputChange(inputValue + ".") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = "00",
                    onClick = { if (inputValue != "0") onInputChange(inputValue + "00") },
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TipSplitterView(
    theme: ThemePalette,
    billInput: String,
    tipPercent: Float,
    peopleCount: Int,
    tipAmount: Double,
    totalAmount: Double,
    perPersonAmount: Double,
    onBillChange: (String) -> Unit,
    onTipPercentChange: (Float) -> Unit,
    onPeopleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Cards Grid
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Per Person Big Callout
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PER PERSON SHARE (₹)",
                        color = theme.screenExpressionColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "₹${String.format(Locale.US, "%.2f", perPersonAmount)}",
                        color = theme.accentColor,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(theme.cardBackground)
                )

                // Breakdown Row (Tip & Total)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Tip (${tipPercent.toInt()}%)",
                            color = theme.screenExpressionColor,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", tipAmount)}",
                            color = theme.screenTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total with Tip",
                            color = theme.screenExpressionColor,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", totalAmount)}",
                            color = theme.secondaryAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Bill Amount Input Card
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "BILL AMOUNT (₹)",
                    color = theme.screenExpressionColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (billInput.isNotEmpty()) "₹$billInput" else "₹0.00",
                    color = theme.screenTextColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Tip Percentage Chips & Slider
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TIP PERCENTAGE",
                        color = theme.screenExpressionColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${tipPercent.toInt()}%",
                        color = theme.accentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10f, 15f, 18f, 20f, 25f).forEach { pct ->
                        val isSelected = tipPercent == pct
                        Surface(
                            color = if (isSelected) theme.accentColor else theme.cardBackground,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTipPercentChange(pct) }
                        ) {
                            Text(
                                text = "${pct.toInt()}%",
                                color = if (isSelected) theme.backgroundColor else theme.screenTextColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = tipPercent,
                    onValueChange = onTipPercentChange,
                    valueRange = 0f..40f,
                    steps = 39,
                    colors = SliderDefaults.colors(
                        thumbColor = theme.accentColor,
                        activeTrackColor = theme.accentColor,
                        inactiveTrackColor = theme.cardBackground
                    )
                )
            }
        }

        // Split Count Stepper
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SPLIT AMONG",
                        color = theme.screenExpressionColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "$peopleCount ${if (peopleCount == 1) "person" else "people"}",
                        color = theme.screenTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { if (peopleCount > 1) onPeopleChange(peopleCount - 1) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.cardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease people",
                            tint = theme.screenTextColor
                        )
                    }

                    Text(
                        text = "$peopleCount",
                        color = theme.accentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(30.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onPeopleChange(peopleCount + 1) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.cardBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase people",
                            tint = theme.screenTextColor
                        )
                    }
                }
            }
        }

        // Keypad for Bill Input
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val keyHeight = 44.dp
            val colSpacing = 6.dp

            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(text = "7", onClick = { onBillChange(billInput + "7") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "8", onClick = { onBillChange(billInput + "8") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "9", onClick = { onBillChange(billInput + "9") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(
                    text = "⌫",
                    onClick = { onBillChange(if (billInput.length > 1) billInput.dropLast(1) else "") },
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(text = "4", onClick = { onBillChange(billInput + "4") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "5", onClick = { onBillChange(billInput + "5") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "6", onClick = { onBillChange(billInput + "6") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(
                    text = "C",
                    onClick = { onBillChange("") },
                    theme = theme,
                    backgroundColor = theme.functionButtonBg,
                    textColor = theme.functionButtonText,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(text = "1", onClick = { onBillChange(billInput + "1") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "2", onClick = { onBillChange(billInput + "2") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = "3", onClick = { onBillChange(billInput + "3") }, theme = theme, modifier = Modifier.weight(1f))
                CalculatorButton(text = ".", onClick = { if (!billInput.contains(".")) onBillChange(billInput + ".") }, theme = theme, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(keyHeight),
                horizontalArrangement = Arrangement.spacedBy(colSpacing)
            ) {
                CalculatorButton(text = "0", onClick = { onBillChange(billInput + "0") }, theme = theme, modifier = Modifier.weight(2f))
                CalculatorButton(text = "00", onClick = { onBillChange(billInput + "00") }, theme = theme, modifier = Modifier.weight(2f))
            }
        }
    }
}
