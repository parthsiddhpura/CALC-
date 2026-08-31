package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemePalette

@Composable
fun StandardKeypad(
    theme: ThemePalette,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onNegate: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val rowSpacing = 8.dp
    val colSpacing = 8.dp
    val keyHeight = 58.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
        // Row 1: AC, ⌫, %, ÷
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
                borderColor = theme.functionButtonBorder,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "btn_ac"
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
                testTag = "btn_backspace"
            )
            CalculatorButton(
                text = "%",
                onClick = { onInput("%") },
                theme = theme,
                backgroundColor = theme.functionButtonBg,
                textColor = theme.functionButtonText,
                borderColor = theme.functionButtonBorder,
                modifier = Modifier.weight(1f),
                testTag = "btn_percent"
            )
            CalculatorButton(
                text = "÷",
                onClick = { onInput("÷") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                borderColor = theme.operatorButtonBorder,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "7",
                onClick = { onInput("7") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_7"
            )
            CalculatorButton(
                text = "8",
                onClick = { onInput("8") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_8"
            )
            CalculatorButton(
                text = "9",
                onClick = { onInput("9") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_9"
            )
            CalculatorButton(
                text = "×",
                onClick = { onInput("×") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                borderColor = theme.operatorButtonBorder,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "4",
                onClick = { onInput("4") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_4"
            )
            CalculatorButton(
                text = "5",
                onClick = { onInput("5") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_5"
            )
            CalculatorButton(
                text = "6",
                onClick = { onInput("6") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_6"
            )
            CalculatorButton(
                text = "−",
                onClick = { onInput("−") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                borderColor = theme.operatorButtonBorder,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "1",
                onClick = { onInput("1") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_1"
            )
            CalculatorButton(
                text = "2",
                onClick = { onInput("2") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_2"
            )
            CalculatorButton(
                text = "3",
                onClick = { onInput("3") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_3"
            )
            CalculatorButton(
                text = "+",
                onClick = { onInput("+") },
                theme = theme,
                backgroundColor = theme.operatorButtonBg,
                textColor = theme.operatorButtonText,
                borderColor = theme.operatorButtonBorder,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f),
                testTag = "btn_add"
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth().height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(colSpacing)
        ) {
            CalculatorButton(
                text = "±",
                onClick = onNegate,
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_negate"
            )
            CalculatorButton(
                text = "0",
                onClick = { onInput("0") },
                theme = theme,
                modifier = Modifier.weight(1f),
                testTag = "btn_0"
            )
            CalculatorButton(
                text = ".",
                onClick = { onInput(".") },
                theme = theme,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                testTag = "btn_dot"
            )
            CalculatorButton(
                text = "=",
                onClick = onEquals,
                theme = theme,
                backgroundBrush = theme.equalsButtonBrush,
                textColor = theme.equalsButtonText,
                borderColor = theme.equalsButtonBorder,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
                testTag = "btn_equals"
            )
        }
    }
}
