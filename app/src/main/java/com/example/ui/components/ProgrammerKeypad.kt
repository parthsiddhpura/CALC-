package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ProgrammerEngine
import com.example.domain.WordSize
import com.example.model.NumberBase
import com.example.model.ThemePalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgrammerKeypad(
    theme: ThemePalette,
    value: Long,
    activeBase: NumberBase,
    wordSize: WordSize,
    onBaseSelect: (NumberBase) -> Unit,
    onWordSizeSelect: (WordSize) -> Unit,
    onDigit: (String) -> Unit,
    onBitwiseOp: (String) -> Unit,
    onBitToggle: (Int) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowSpacing = 6.dp
    val colSpacing = 6.dp
    val keyHeight = 44.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Base Conversions Card
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NumberBase.entries.forEach { base ->
                    val isSelected = base == activeBase
                    val formatted = ProgrammerEngine.formatInBase(value, base, wordSize)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) theme.accentColor.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { onBaseSelect(base) }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = base.name,
                            color = if (isSelected) theme.accentColor else theme.screenExpressionColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = formatted,
                            color = if (isSelected) theme.screenTextColor else theme.screenExpressionColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Word Size Selector Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WordSize.entries.forEach { ws ->
                val isSelected = ws == wordSize
                Surface(
                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onWordSizeSelect(ws) }
                ) {
                    Text(
                        text = ws.name,
                        color = if (isSelected) (if (theme.accentColor.luminance() > 0.6f) Color(0xFF211118) else Color.White) else theme.screenTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Interactive Binary Bit Grid (32-bit visible or 64-bit grouped)
        Surface(
            color = theme.surfaceColor,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Bits from (wordSize.bits - 1) down to 0
                val totalBits = wordSize.bits.coerceAtMost(32)
                val rows = totalBits / 8
                for (r in 0 until rows) {
                    val startBit = totalBits - 1 - (r * 8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$startBit",
                            color = theme.screenTextColor.copy(alpha = 0.75f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(18.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (b in startBit downTo (startBit - 7)) {
                                val isSet = (value and (1L shl b)) != 0L
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSet) theme.accentColor else theme.cardBackground)
                                        .border(
                                            0.8.dp,
                                            if (isSet) theme.accentColor else theme.screenBorderColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { onBitToggle(b) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isSet) "1" else "0",
                                        color = if (isSet) (if (theme.accentColor.luminance() > 0.6f) Color(0xFF211118) else Color.White) else theme.screenTextColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                if (b % 4 == 0 && b != startBit - 7) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Programmer Keypad
        // Row 1: AND, OR, XOR, NOT, <<
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "AND",
                onClick = { onBitwiseOp("AND") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_and"
            )
            CalculatorButton(
                text = "OR",
                onClick = { onBitwiseOp("OR") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_or"
            )
            CalculatorButton(
                text = "XOR",
                onClick = { onBitwiseOp("XOR") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_xor"
            )
            CalculatorButton(
                text = "NOT",
                onClick = { onBitwiseOp("NOT") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_not"
            )
            CalculatorButton(
                text = "<<",
                onClick = { onBitwiseOp("<<") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_lsh"
            )
        }

        // Row 2: A, B, 7, 8, 9, ÷
        val isHex = activeBase == NumberBase.HEX
        val isOct = activeBase == NumberBase.OCT
        val isBin = activeBase == NumberBase.BIN
        val isDec = activeBase == NumberBase.DEC

        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "A",
                onClick = { if (isHex) onDigit("A") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_a"
            )
            CalculatorButton(
                text = "B",
                onClick = { if (isHex) onDigit("B") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_b"
            )
            val btn7Enabled = !isBin
            CalculatorButton(
                text = "7",
                onClick = { if (btn7Enabled) onDigit("7") },
                theme = theme,
                backgroundColor = if (btn7Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn7Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_7"
            )
            val btn8Enabled = isHex || isDec
            CalculatorButton(
                text = "8",
                onClick = { if (btn8Enabled) onDigit("8") },
                theme = theme,
                backgroundColor = if (btn8Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn8Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_8"
            )
            val btn9Enabled = isHex || isDec
            CalculatorButton(
                text = "9",
                onClick = { if (btn9Enabled) onDigit("9") },
                theme = theme,
                backgroundColor = if (btn9Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn9Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_9"
            )
            CalculatorButton(
                text = "÷",
                onClick = { onBitwiseOp("/") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_div"
            )
        }

        // Row 3: C, D, 4, 5, 6, ×
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "C",
                onClick = { if (isHex) onDigit("C") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_c"
            )
            CalculatorButton(
                text = "D",
                onClick = { if (isHex) onDigit("D") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_d"
            )
            val btn4to6Enabled = !isBin
            CalculatorButton(
                text = "4",
                onClick = { if (btn4to6Enabled) onDigit("4") },
                theme = theme,
                backgroundColor = if (btn4to6Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn4to6Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_4"
            )
            CalculatorButton(
                text = "5",
                onClick = { if (btn4to6Enabled) onDigit("5") },
                theme = theme,
                backgroundColor = if (btn4to6Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn4to6Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_5"
            )
            CalculatorButton(
                text = "6",
                onClick = { if (btn4to6Enabled) onDigit("6") },
                theme = theme,
                backgroundColor = if (btn4to6Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn4to6Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_6"
            )
            CalculatorButton(
                text = "×",
                onClick = { onBitwiseOp("*") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_mul"
            )
        }

        // Row 4: E, F, 1, 2, 3, −
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "E",
                onClick = { if (isHex) onDigit("E") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_e"
            )
            CalculatorButton(
                text = "F",
                onClick = { if (isHex) onDigit("F") },
                theme = theme,
                backgroundColor = if (isHex) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (isHex) theme.accentColor else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_hex_f"
            )
            CalculatorButton(
                text = "1",
                onClick = { onDigit("1") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_1"
            )
            val btn2to3Enabled = !isBin
            CalculatorButton(
                text = "2",
                onClick = { if (btn2to3Enabled) onDigit("2") },
                theme = theme,
                backgroundColor = if (btn2to3Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn2to3Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_2"
            )
            CalculatorButton(
                text = "3",
                onClick = { if (btn2to3Enabled) onDigit("3") },
                theme = theme,
                backgroundColor = if (btn2to3Enabled) theme.numberButtonBg else theme.surfaceColor.copy(alpha = 0.4f),
                textColor = if (btn2to3Enabled) theme.numberButtonText else theme.screenExpressionColor.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_3"
            )
            CalculatorButton(
                text = "−",
                onClick = { onBitwiseOp("-") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_sub"
            )
        }

        // Row 5: AC, ⌫, 0, >>, +, =
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "AC",
                onClick = onClear,
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_ac"
            )
            CalculatorButton(
                text = "⌫",
                onClick = onBackspace,
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_backspace"
            )
            CalculatorButton(
                text = "0",
                onClick = { onDigit("0") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_0"
            )
            CalculatorButton(
                text = ">>",
                onClick = { onBitwiseOp(">>") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_rsh"
            )
            CalculatorButton(
                text = "+",
                onClick = { onBitwiseOp("+") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_add"
            )
            CalculatorButton(
                text = "=",
                onClick = onEquals,
                theme = theme,
                backgroundBrush = theme.equalsButtonBrush,
                textColor = theme.equalsButtonText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "btn_prog_equals"
            )
        }
    }
}
