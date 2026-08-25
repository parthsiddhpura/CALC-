package com.example.ui.components

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
    testTag: String = "btn_$text"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = if (theme.pressAnimation == PressAnimationType.BOUNCE) {
        val anim by animateFloatAsState(
            targetValue = if (isPressed) 0.93f else 1.0f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 800f),
            label = "btn_scale"
        )
        anim
    } else 1.0f

    val sinkOffsetY = if (theme.pressAnimation == PressAnimationType.DEEP_SINK) {
        val anim by animateFloatAsState(
            targetValue = if (isPressed) 2.5f else 0f,
            animationSpec = tween(durationMillis = 40),
            label = "btn_sink"
        )
        anim
    } else 0f

    val brutalOffset = if (theme.isBrutalistShadow) {
        val anim by animateFloatAsState(
            targetValue = if (isPressed) 0f else 3.5f,
            animationSpec = tween(durationMillis = 40),
            label = "btn_brutal"
        )
        anim
    } else 3.5f

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
                translationY = sinkOffsetY.dp.toPx()
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
                    if (theme.isBrutalistShadow) {
                        Modifier.graphicsLayer {
                            val diff = (3.5f - brutalOffset).dp.toPx()
                            translationX = diff
                            translationY = diff
                        }
                    } else Modifier
                )
                .then(
                    if (theme.hasShadow && !theme.isBrutalistShadow) {
                        Modifier.shadow(
                            elevation = if (isPressed) (theme.shadowElevationDp / 2) else theme.shadowElevationDp,
                            shape = shape
                        )
                    } else Modifier
                )
                .clip(shape)
                .then(
                    if (backgroundBrush != null) {
                        Modifier.background(backgroundBrush)
                    } else {
                        Modifier.background(
                            if (isPressed && theme.pressAnimation == PressAnimationType.NEON_GLOW) {
                                backgroundColor.copy(alpha = 0.85f)
                            } else backgroundColor
                        )
                    }
                )
                .then(
                    if (borderWidth > 0.dp || (isPressed && theme.pressAnimation == PressAnimationType.NEON_GLOW)) {
                        val activeBorderColor = if (isPressed && theme.pressAnimation == PressAnimationType.NEON_GLOW) {
                            theme.accentColor
                        } else borderColor
                        val activeWidth = if (isPressed && theme.pressAnimation == PressAnimationType.NEON_GLOW) {
                            borderWidth + 1.dp
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
