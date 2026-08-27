package com.example.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ThemeId {
    CYBERPUNK,
    SYNTHWAVE,
    NEO_BRUTALISM,
    CRT_TERMINAL,
    MATCHA_ZEN,
    SUNSET_GLOW,
    OLED_OBSIDIAN,
    PASTEL_BUBBLEGUM,
    MONOCHROME_LUXURY,
    NORDIC_ICE,
    GAMEBOY_8BIT,
    SOLARIZED_DARK,
    SAKURA_BLOOM,
    MATRIX_HACKER,
    AMETHYST_ROYAL,
    MAC_CLASSIC_1984,
    MAC_CLASSIC_1984_DARK,
    VAPORWAVE_95,
    ESPRESSO_ROAST,
    LAVA_MAGMA,
    COSMIC_AURORA,
    MIDNIGHT_TOKYO,
    EMERALD_FOREST,
    STEAMPUNK_BRASS,
    COTTON_CANDY
}

enum class ButtonShapeType {
    PILL,
    SQUIRCLE,
    ROUNDED_SQUARE,
    BRUTALIST_RECT,
    CIRCLE
}

enum class PressAnimationType {
    BOUNCE,
    DEEP_SINK,
    NEON_GLOW,
    BRUTAL_OFFSET
}

enum class DisplayFontType {
    MONOSPACE,
    MODERN_SANS,
    DIGITAL_LCD,
    ROUNDED
}

data class ThemePalette(
    val id: ThemeId,
    val name: String,
    val subtitle: String,
    val category: String,
    val isDark: Boolean,
    
    // Background & Containers
    val backgroundBrush: Brush,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val cardBackground: Color,
    
    // Display Screen
    val screenBackground: Color,
    val screenBorderColor: Color,
    val screenTextColor: Color,
    val screenExpressionColor: Color,
    val screenPreviewColor: Color,
    val cursorColor: Color,
    val displayFont: DisplayFontType,
    val hasScanlines: Boolean = false,
    
    // Keypad - Number Keys (0-9, .)
    val numberButtonBg: Color,
    val numberButtonText: Color,
    val numberButtonBorder: Color = Color.Transparent,
    
    // Keypad - Basic Operators (+, -, *, /)
    val operatorButtonBg: Color,
    val operatorButtonText: Color,
    val operatorButtonBorder: Color = Color.Transparent,
    
    // Keypad - Functions (C, AC, DEL, %, +/-, Trig, Log, (), etc.)
    val functionButtonBg: Color,
    val functionButtonText: Color,
    val functionButtonBorder: Color = Color.Transparent,
    
    // Equals Action Key (Special Accent)
    val equalsButtonBrush: Brush,
    val equalsButtonText: Color,
    val equalsButtonBorder: Color = Color.Transparent,
    
    // Accent Glow & Highlights
    val accentColor: Color,
    val secondaryAccent: Color,
    val glowColor: Color,
    
    // Physical Styling
    val shapeType: ButtonShapeType,
    val cornerRadiusDp: Dp = 16.dp,
    val borderWidthDp: Dp = 0.dp,
    val hasShadow: Boolean = false,
    val shadowElevationDp: Dp = 2.dp,
    val isBrutalistShadow: Boolean = false,
    val brutalistShadowColor: Color = Color.Black,
    val pressAnimation: PressAnimationType = PressAnimationType.BOUNCE,
    
    // Status Bar & Navigation
    val statusBarDarkIcons: Boolean = false
) {
    fun getShape(): Shape {
        return when (shapeType) {
            ButtonShapeType.PILL -> RoundedCornerShape(percent = 50)
            ButtonShapeType.CIRCLE -> CircleShape
            ButtonShapeType.SQUIRCLE -> RoundedCornerShape(cornerRadiusDp)
            ButtonShapeType.ROUNDED_SQUARE -> RoundedCornerShape(cornerRadiusDp)
            ButtonShapeType.BRUTALIST_RECT -> RoundedCornerShape(cornerRadiusDp)
        }
    }
}
