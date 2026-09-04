package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ButtonShapeType
import com.example.model.DisplayFontType
import com.example.model.PressAnimationType
import com.example.model.ThemePalette

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    theme: ThemePalette,
    modifier: Modifier = Modifier,
    backgroundColor: Color = theme.numberButtonBg,
    backgroundBrush: Brush? = null,
    textColor: Color = theme.numberButtonText,
    borderColor: Color = theme.numberButtonBorder,
    borderWidth: Dp = theme.borderWidthDp,
    fontSize: TextUnit = 22.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    isSpanTwo: Boolean = false,
    contentDescription: String? = null,
    testTag: String = "btn_$text",
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Ultra-crisp, zero-latency physical tactile spring
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && theme.pressAnimation == PressAnimationType.BOUNCE -> 0.88f
            isPressed -> 0.92f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 1600f
        ),
        label = "btn_scale"
    )

    // Only compute specialized offsets if theme explicitly requires them
    val sinkOffsetY = if (isPressed && theme.pressAnimation == PressAnimationType.DEEP_SINK) 3.5f else 0f
    val brutalDiff = if (theme.isBrutalistShadow && isPressed) 3.5f else 0f

    val currentBgColor = if (isPressed) {
        when {
            theme.hasBatSignal -> Color(0xFF283344)
            theme.hasArcReactor -> Color(0xFF1F2D42)
            theme.pressAnimation == PressAnimationType.NEON_GLOW -> theme.accentColor.copy(alpha = 0.35f)
            else -> backgroundColor.copy(alpha = 0.84f)
        }
    } else backgroundColor

    val shape: Shape = remember(theme.shapeType, theme.cornerRadiusDp) {
        when (theme.shapeType) {
            ButtonShapeType.PILL -> RoundedCornerShape(percent = 50)
            ButtonShapeType.CIRCLE -> CircleShape
            ButtonShapeType.SQUIRCLE -> RoundedCornerShape(theme.cornerRadiusDp)
            ButtonShapeType.ROUNDED_SQUARE -> RoundedCornerShape(theme.cornerRadiusDp)
            ButtonShapeType.BRUTALIST_RECT -> RoundedCornerShape(theme.cornerRadiusDp.coerceAtMost(8.dp))
        }
    }

    val fontFamily = remember(theme.displayFont) {
        when (theme.displayFont) {
            DisplayFontType.MONOSPACE -> FontFamily.Monospace
            DisplayFontType.DIGITAL_LCD -> FontFamily.Monospace
            DisplayFontType.MODERN_SANS -> FontFamily.SansSerif
            DisplayFontType.ROUNDED -> FontFamily.SansSerif
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                if (sinkOffsetY != 0f) {
                    translationY = sinkOffsetY.dp.toPx()
                }
            }
            .testTag(testTag)
    ) {
        // Neo-brutalist solid shadow underlay
        if (theme.isBrutalistShadow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.5.dp, y = 3.5.dp)
                    .clip(shape)
                    .background(theme.brutalistShadowColor)
            )
        }

        // Main Button Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (theme.isBrutalistShadow && brutalDiff > 0f) {
                        Modifier.graphicsLayer {
                            val px = brutalDiff.dp.toPx()
                            translationX = px
                            translationY = px
                        }
                    } else Modifier
                )
                .then(
                    if (theme.hasShadow && !theme.isBrutalistShadow && theme.shadowElevationDp > 0.dp) {
                        Modifier.shadow(
                            elevation = if (isPressed) theme.shadowElevationDp / 2 else theme.shadowElevationDp,
                            shape = shape
                        )
                    } else Modifier
                )
                .clip(shape)
                .then(
                    if (backgroundBrush != null) {
                        Modifier.background(backgroundBrush)
                    } else {
                        Modifier.background(currentBgColor)
                    }
                )
                .then(
                    if (borderWidth > 0.dp || (isPressed && (theme.pressAnimation == PressAnimationType.NEON_GLOW || theme.hasBatSignal || theme.hasArcReactor))) {
                        val activeBorderColor = if (isPressed && (theme.pressAnimation == PressAnimationType.NEON_GLOW || theme.hasBatSignal || theme.hasArcReactor)) {
                            theme.accentColor
                        } else borderColor
                        val activeWidth = if (isPressed && (theme.pressAnimation == PressAnimationType.NEON_GLOW || theme.hasBatSignal || theme.hasArcReactor)) {
                            borderWidth + 1.2.dp
                        } else borderWidth
                        Modifier.border(activeWidth, activeBorderColor, shape)
                    } else Modifier
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
