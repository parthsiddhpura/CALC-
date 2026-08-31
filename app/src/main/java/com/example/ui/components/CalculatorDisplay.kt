package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleMode
import com.example.model.CalculatorMode
import com.example.model.DisplayConfig
import com.example.model.DisplayFontType
import com.example.model.DisplayFormatter
import com.example.model.DisplayNotation
import com.example.model.DisplayPrecisionMode
import com.example.model.DisplayScaleSize
import com.example.model.DisplaySeparatorStyle
import com.example.model.ThemePalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    displayConfig: DisplayConfig = DisplayConfig(),
    historyCount: Int = 0,
    onToggleAngleMode: (() -> Unit)? = null,
    onOpenHistory: (() -> Unit)? = null,
    onOpenDecimalConverter: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val exprScrollState = rememberScrollState()
    var justCopied by remember { mutableStateOf(false) }

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

    // Format expression and results according to Display Preferences
    val formattedExpr = remember(expression, displayConfig.separatorStyle) {
        DisplayFormatter.formatExpression(expression, displayConfig.separatorStyle)
    }

    val formattedResult = remember(result, displayConfig.separatorStyle, displayConfig.precisionMode, displayConfig.notation) {
        DisplayFormatter.formatNumber(
            valueStr = result,
            separatorStyle = displayConfig.separatorStyle,
            precisionMode = displayConfig.precisionMode,
            notation = displayConfig.notation
        )
    }

    val formattedPreview = remember(previewResult, displayConfig.separatorStyle, displayConfig.precisionMode, displayConfig.notation) {
        previewResult?.let {
            DisplayFormatter.formatNumber(
                valueStr = it,
                separatorStyle = displayConfig.separatorStyle,
                precisionMode = displayConfig.precisionMode,
                notation = displayConfig.notation
            )
        }
    }

    fun copyToClipboard(content: String, label: String = "Calculation") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        Toast.makeText(context, "Copied: $content", Toast.LENGTH_SHORT).show()
        justCopied = true
        coroutineScope.launch {
            delay(1500)
            justCopied = false
        }
    }

    BoxWithConstraints(
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
                onClick = {
                    if (displayConfig.copyOnTap) {
                        copyToClipboard(formattedResult, "Result")
                    }
                },
                onLongClick = {
                    val fullEquation = if (formattedExpr.isNotBlank() && formattedResult != "0") {
                        "$formattedExpr = $formattedResult"
                    } else formattedResult
                    copyToClipboard(fullEquation, "Calculation")
                }
            )
            .testTag("calculator_display")
    ) {
        val isVeryCompact = maxHeight < 115.dp
        val isCompact = maxHeight < 145.dp
        val innerPadding = if (isVeryCompact) 8.dp else if (isCompact) 10.dp else 14.dp

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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status & Actions Row: Mode, Angle, Badges, Format indicators, Decimal Conv, Quick Copy, History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (displayConfig.showStatusBadges) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 3.dp else 5.dp)
                    ) {
                        // Mode Badge
                        Surface(
                            color = theme.accentColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = mode.shortName.uppercase(),
                                color = theme.accentColor,
                                fontSize = if (isCompact) 9.sp else 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 6.dp, vertical = 2.dp)
                            )
                        }

                        // Angle Mode Badge (DEG / RAD) - Tap to toggle!
                        if (mode == CalculatorMode.STANDARD || mode == CalculatorMode.SCIENTIFIC) {
                            Surface(
                                color = theme.secondaryAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = if (onToggleAngleMode != null) {
                                    Modifier.clickable { onToggleAngleMode() }
                                } else Modifier
                            ) {
                                Text(
                                    text = angleMode.name,
                                    color = theme.secondaryAccent,
                                    fontSize = if (isCompact) 9.sp else 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fontFamily,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 6.dp, vertical = 2.dp)
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
                                    fontSize = if (isCompact) 9.sp else 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = fontFamily,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Display Notation Badge if non-standard
                        if (displayConfig.notation != DisplayNotation.STANDARD) {
                            Surface(
                                color = theme.surfaceColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (displayConfig.notation == DisplayNotation.SCIENTIFIC) "SCI" else "ENG",
                                    color = theme.screenExpressionColor,
                                    fontSize = if (isCompact) 8.sp else 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = fontFamily,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Right Actions: Quick Copy Icon, Decimal Converter (F↔D / DEC) and History Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 3.dp else 6.dp)
                ) {
                    // Quick Copy Action Button
                    Surface(
                        shape = CircleShape,
                        color = if (justCopied) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                copyToClipboard(formattedResult, "Result")
                            }
                            .testTag("btn_display_copy")
                    ) {
                        Box(
                            modifier = Modifier.padding(if (isCompact) 4.dp else 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (justCopied) Icons.Default.Done else Icons.Default.ContentCopy,
                                contentDescription = "Copy Result",
                                tint = if (justCopied) theme.accentColor else theme.screenExpressionColor,
                                modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
                            )
                        }
                    }

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
                                modifier = Modifier.padding(horizontal = if (isCompact) 4.dp else 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Transform,
                                    contentDescription = "Decimal Conversion",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(if (isCompact) 11.dp else 13.dp)
                                )
                                Text(
                                    text = "F↔D",
                                    color = theme.accentColor,
                                    fontSize = if (isCompact) 9.sp else 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    // History Tape Button
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
                                modifier = Modifier.padding(if (isCompact) 3.dp else 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History",
                                    tint = theme.screenTextColor,
                                    modifier = Modifier.size(if (isCompact) 13.dp else 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 2.dp else 6.dp))

            // Expression Row (with horizontal scroll)
            val exprBaseSize = if (isCompact) (displayConfig.scaleSize.exprSp - 4).coerceAtLeast(13) else displayConfig.scaleSize.exprSp
            if (formattedExpr.isNotEmpty() || !isVeryCompact) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(exprScrollState),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (formattedExpr.isEmpty()) "0" else formattedExpr,
                        color = if (formattedExpr.isEmpty()) theme.screenExpressionColor.copy(alpha = 0.4f) else theme.screenExpressionColor,
                        fontSize = exprBaseSize.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.testTag("expression_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 1.dp else 3.dp))

            // Main Result Row + Live Preview Result
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                // Live preview if enabled and available
                if (displayConfig.showLivePreview) {
                    AnimatedVisibility(
                        visible = formattedPreview != null && formattedPreview != formattedResult,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (formattedPreview != null) {
                            Text(
                                text = "= $formattedPreview",
                                color = theme.screenPreviewColor,
                                fontSize = (exprBaseSize - 2).coerceAtLeast(14).sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(bottom = 1.dp)
                                    .testTag("preview_result_text")
                            )
                        }
                    }
                }

                // Main Result with smooth responsive scale
                val baseSp = if (isVeryCompact) {
                    32f
                } else if (isCompact) {
                    (displayConfig.scaleSize.resultSp.toFloat() - 8f).coerceAtLeast(34f)
                } else {
                    displayConfig.scaleSize.resultSp.toFloat()
                }
                val targetSp = when {
                    formattedResult.length > 16 -> (baseSp - 20f).coerceAtLeast(20f)
                    formattedResult.length > 12 -> (baseSp - 14f).coerceAtLeast(24f)
                    formattedResult.length > 8 -> (baseSp - 8f).coerceAtLeast(28f)
                    else -> baseSp
                }
                val animatedSp by animateFloatAsState(
                    targetValue = targetSp,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
                    label = "result_font_scale"
                )

                Text(
                    text = formattedResult,
                    color = theme.screenTextColor,
                    fontSize = animatedSp.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("main_result_text")
                )
            }
        }
    }
}
