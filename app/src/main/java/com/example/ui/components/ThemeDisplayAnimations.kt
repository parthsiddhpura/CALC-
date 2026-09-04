package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.ThemePalette
import kotlin.math.sin

/**
 * Renders a unique, tasteful, professional display animation tailored to every theme's
 * aesthetic category and color palette. Completely free of cheap graphics; uses pure
 * mathematical waves, precision geometric verniers, specular refractions, and glowing light motes.
 */
@Composable
fun ThemeAmbientDisplayAnimation(
    modifier: Modifier = Modifier,
    theme: ThemePalette
) {
    val infiniteTransition = rememberInfiniteTransition(label = "theme_ambient_anim")

    // General wave phase cycle (4.5s loop)
    val wavePhase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_wave_phase"
    )

    // Breathing pulse cycle (2.8s loop)
    val breathPulse = infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_breath_pulse"
    )

    // Linear scanning sweep (6.5s loop)
    val sweepProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_sweep_progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val accent = theme.accentColor
        val secondary = theme.secondaryAccent
        val wavePhaseVal = wavePhase.value
        val breathPulseVal = breathPulse.value
        val sweepProgressVal = sweepProgress.value

        when {
            // 1. NEUMORPHIC CATEGORY: Soft Specular Breathing Refraction & Liquid Light Edge
            theme.category.contains("Neumorphic", ignoreCase = true) -> {
                // Soft glowing liquid light rim along the bottom display edge
                val glowBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.15f * breathPulseVal),
                        accent.copy(alpha = 0.45f * breathPulseVal),
                        accent.copy(alpha = 0.15f * breathPulseVal),
                        Color.Transparent
                    )
                )
                drawLine(
                    brush = glowBrush,
                    start = Offset(w * 0.15f, h - 2f),
                    end = Offset(w * 0.85f, h - 2f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Delicate specular breathing halo in top-left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.10f * breathPulseVal),
                            Color.Transparent
                        ),
                        center = Offset(24.dp.toPx(), 20.dp.toPx()),
                        radius = 45.dp.toPx()
                    ),
                    center = Offset(24.dp.toPx(), 20.dp.toPx()),
                    radius = 45.dp.toPx()
                )
            }

            // 2. RETRO & VINTAGE CATEGORY: CRT Phosphor Flutter & Blinking Terminal Cursor
            theme.category.contains("Retro", ignoreCase = true) || theme.category.contains("Vintage", ignoreCase = true) -> {
                // CRT subtle horizontal phosphor scan line
                val scanY = sweepProgressVal * h
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            accent.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, scanY),
                    end = Offset(w, scanY),
                    strokeWidth = 1.dp.toPx()
                )

                // Blinking terminal cursor block in top-right [ █ ]
                if (breathPulseVal > 0.65f) {
                    drawRect(
                        color = accent.copy(alpha = 0.70f),
                        topLeft = Offset(w - 24.dp.toPx(), 14.dp.toPx()),
                        size = Size(6.dp.toPx(), 10.dp.toPx())
                    )
                }
            }

            // 3. KAWAII & PLAYFUL CATEGORY: Floating Pastel Sparkle Motes
            theme.category.contains("Kawaii", ignoreCase = true) || theme.category.contains("Playful", ignoreCase = true) -> {
                val sparkleCount = 4
                for (i in 0 until sparkleCount) {
                    val phaseOffset = i * 1.57f
                    val floatY = h - (12.dp.toPx() + (sin(wavePhaseVal + phaseOffset) * 6.dp.toPx()))
                    val floatX = (w * 0.2f) + i * (w * 0.2f)
                    val alpha = (0.35f + 0.45f * sin(wavePhaseVal * 1.5f + phaseOffset)).coerceIn(0.1f, 0.85f)
                    val starR = (3f + 1.5f * sin(wavePhaseVal + phaseOffset)).dp.toPx()

                    // Draw 4-point subtle star sparkle
                    drawLine(
                        color = accent.copy(alpha = alpha),
                        start = Offset(floatX - starR, floatY),
                        end = Offset(floatX + starR, floatY),
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accent.copy(alpha = alpha),
                        start = Offset(floatX, floatY - starR),
                        end = Offset(floatX, floatY + starR),
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.9f),
                        radius = starR * 0.35f,
                        center = Offset(floatX, floatY)
                    )
                }
            }

            // 4. INDUSTRIAL & SKEUOMORPHIC CATEGORY: Precision Calibrated Vernier Scale
            theme.category.contains("Industrial", ignoreCase = true) || theme.category.contains("Skeuomorphic", ignoreCase = true) -> {
                val rulerY = h - 6.dp.toPx()
                val totalTicks = 24
                val spacing = w / (totalTicks + 1)

                for (i in 1..totalTicks) {
                    val x = i * spacing
                    val isMajor = (i % 4 == 0)
                    val tickHeight = if (isMajor) 6.dp.toPx() else 3.dp.toPx()
                    val tickAlpha = if (isMajor) 0.50f else 0.25f

                    drawLine(
                        color = accent.copy(alpha = tickAlpha),
                        start = Offset(x, rulerY),
                        end = Offset(x, rulerY - tickHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Precision indicator cursor gliding back and forth
                val cursorX = (w * 0.1f) + ((sin(wavePhaseVal * 0.8f) + 1f) / 2f) * (w * 0.8f)
                drawLine(
                    color = secondary.copy(alpha = 0.85f),
                    start = Offset(cursorX, rulerY),
                    end = Offset(cursorX, rulerY - 9.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawCircle(
                    color = secondary,
                    radius = 2.dp.toPx(),
                    center = Offset(cursorX, rulerY - 10.dp.toPx())
                )
            }

            // 5. INTERACTIVE DARK / OBSIDIAN / CYBERPUNK / DEFAULT: Dynamic Multi-Harmonic Calculation Wave
            else -> {
                // Harmonic mathematical wave across baseline
                val wavePath = Path()
                val startX = 0f
                val baseY = h - 6.dp.toPx()
                val waveHeight = 4.dp.toPx()

                var isFirst = true
                var x = startX
                val step = 4.dp.toPx()

                while (x <= w) {
                    val normX = x / w
                    // Dual harmonic calculation wave: fundamental + octave
                    val y = baseY +
                            (sin(normX * 12f + wavePhaseVal) * waveHeight * 0.65f) +
                            (sin(normX * 24f - wavePhaseVal * 1.4f) * waveHeight * 0.35f)

                    if (isFirst) {
                        wavePath.moveTo(x, y)
                        isFirst = false
                    } else {
                        wavePath.lineTo(x, y)
                    }
                    x += step
                }

                // Glowing gradient wave stroke
                val waveBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.35f * breathPulseVal),
                        accent.copy(alpha = 0.80f * breathPulseVal),
                        secondary.copy(alpha = 0.75f * breathPulseVal),
                        accent.copy(alpha = 0.35f * breathPulseVal),
                        Color.Transparent
                    )
                )
                drawPath(
                    path = wavePath,
                    brush = waveBrush,
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Ambient corner bracket accents
                val bracketLen = 10.dp.toPx()
                val bracketColor = accent.copy(alpha = 0.25f * breathPulseVal)
                val strokeW = 1.dp.toPx()

                // Top-left bracket
                drawLine(bracketColor, Offset(4f, 4f), Offset(4f + bracketLen, 4f), strokeW)
                drawLine(bracketColor, Offset(4f, 4f), Offset(4f, 4f + bracketLen), strokeW)

                // Top-right bracket
                drawLine(bracketColor, Offset(w - 4f, 4f), Offset(w - 4f - bracketLen, 4f), strokeW)
                drawLine(bracketColor, Offset(w - 4f, 4f), Offset(w - 4f, 4f + bracketLen), strokeW)
            }
        }
    }
}
