package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleMode
import com.example.model.CalculatorMode
import com.example.model.DisplayFontType
import com.example.model.ThemePalette

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    previewResult: String?,
    theme: ThemePalette,
    angleMode: AngleMode,
    hasMemory: Boolean,
    mode: CalculatorMode,
    historyCount: Int = 0,
    onOpenHistory: (() -> Unit)? = null,
    onOpenDecimalConverter: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exprScrollState = rememberScrollState()

    LaunchedEffect(expression) {
        if (exprScrollState.maxValue > 0) {
            exprScrollState.scrollTo(exprScrollState.maxValue)
        }
    }

    val fontFamily = when (theme.displayFont) {
        DisplayFontType.MONOSPACE -> FontFamily.Monospace
        DisplayFontType.DIGITAL_LCD -> FontFamily.Monospace
        DisplayFontType.MODERN_SANS -> FontFamily.SansSerif
        DisplayFontType.ROUNDED -> FontFamily.SansSerif
    }

    val screenShape = RoundedCornerShape(theme.cornerRadiusDp.coerceAtLeast(14.dp))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(screenShape)
            .background(theme.screenBackground)
            .then(
                if (theme.borderWidthDp > 0.dp) {
                    Modifier.border(theme.borderWidthDp, theme.screenBorderColor, screenShape)
                } else Modifier
            )
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    val textToCopy = if (expression.isNotBlank() && result != "0") "$expression = $result" else result
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Calculation", textToCopy)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied: $textToCopy", Toast.LENGTH_SHORT).show()
                }
            )
            .padding(14.dp)
            .testTag("calculator_display")
    ) {
        // CRT Scanline Overlay if enabled
        if (theme.hasScanlines) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val step = 4.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x2200FF66),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status & Actions Row: Mode, Angle, Decimal Converter Button, History Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Mode Badge
                    Surface(
                        color = theme.accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mode.shortName.uppercase(),
                            color = theme.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Angle Mode Badge (DEG / RAD)
                    if (mode == CalculatorMode.STANDARD || mode == CalculatorMode.SCIENTIFIC) {
                        Surface(
                            color = theme.secondaryAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = angleMode.name,
                                color = theme.secondaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = fontFamily,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Memory Badge
                    if (hasMemory) {
                        Surface(
                            color = Color(0xFFFFB703).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "M",
                                color = Color(0xFFFFB703),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Right Actions: Decimal Converter (F↔D / DEC) and History Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Decimal Conversion Quick Pill Button
                    if (onOpenDecimalConverter != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.surfaceColor,
                            modifier = Modifier
                                .clickable { onOpenDecimalConverter() }
                                .testTag("btn_display_decimal_conv")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Transform,
                                    contentDescription = "Decimal Conversion",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "F↔D",
                                    color = theme.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // History Tape Button (relocated here seamlessly)
                    if (onOpenHistory != null) {
                        Surface(
                            shape = CircleShape,
                            color = theme.surfaceColor,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onOpenHistory() }
                                .testTag("btn_display_history")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (historyCount > 0) {
                                        Badge(
                                            containerColor = theme.secondaryAccent,
                                            contentColor = theme.backgroundColor
                                        ) {
                                            Text(
                                                text = "${historyCount.coerceAtMost(99)}",
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History",
                                    tint = theme.screenTextColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expression Row (with horizontal scroll)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    color = if (expression.isEmpty()) theme.screenExpressionColor.copy(alpha = 0.4f) else theme.screenExpressionColor,
                    fontSize = 24.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Result Row + Live Preview Result
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                // Live preview if available
                AnimatedVisibility(
                    visible = previewResult != null && previewResult != result,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (previewResult != null) {
                        Text(
                            text = "= $previewResult",
                            color = theme.screenPreviewColor,
                            fontSize = 20.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .testTag("preview_result_text")
                        )
                    }
                }

                // Main Result
                val resultFontSize = when {
                    result.length > 14 -> 32.sp
                    result.length > 10 -> 40.sp
                    result.length > 7 -> 48.sp
                    else -> 56.sp
                }

                Text(
                    text = result,
                    color = theme.screenTextColor,
                    fontSize = resultFontSize,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("main_result_text")
                )
            }
        }
    }
}
