package com.example.widget

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class WidgetThemeFamily(
    val title: String,
    val description: String,
    @DrawableRes val bgRes: Int,
    @DrawableRes val screenBgRes: Int,
    @DrawableRes val btnBgRes: Int,
    @DrawableRes val btnAccentBgRes: Int,
    val textColorHex: Int,
    val subtextColorHex: Int,
    val accentColorHex: Int,
    val headerTextColorHex: Int,
    val composeBackground: Color,
    val composeAccent: Color
) {
    MINIMAL(
        title = "Minimal",
        description = "Clean, dark monochrome with razor-sharp contrast",
        bgRes = R.drawable.widget_bg_minimal,
        screenBgRes = R.drawable.widget_screen_bg_minimal,
        btnBgRes = R.drawable.widget_btn_bg_minimal,
        btnAccentBgRes = R.drawable.widget_btn_bg_minimal,
        textColorHex = 0xFFFFFFFF.toInt(),
        subtextColorHex = 0xFF8A93A6.toInt(),
        accentColorHex = 0xFFFFFFFF.toInt(),
        headerTextColorHex = 0xFFFFFFFF.toInt(),
        composeBackground = Color(0xFF121418),
        composeAccent = Color(0xFFFFFFFF)
    ),
    GLASS_MODERN(
        title = "Glass / Modern",
        description = "Translucent frosted slate with luminous azure accents",
        bgRes = R.drawable.widget_bg_glass,
        screenBgRes = R.drawable.widget_screen_bg_glass,
        btnBgRes = R.drawable.widget_btn_bg_glass,
        btnAccentBgRes = R.drawable.widget_btn_accent_glass,
        textColorHex = 0xFFFFFFFF.toInt(),
        subtextColorHex = 0xFF94A3B8.toInt(),
        accentColorHex = 0xFF00E5FF.toInt(),
        headerTextColorHex = 0xFF00E5FF.toInt(),
        composeBackground = Color(0xFF131C2E),
        composeAccent = Color(0xFF00E5FF)
    ),
    RETRO(
        title = "Retro Mac 1984 / CRT",
        description = "Macintosh 1984 beige casing & glowing CRT green phosphor",
        bgRes = R.drawable.widget_bg_retro,
        screenBgRes = R.drawable.widget_screen_bg_retro,
        btnBgRes = R.drawable.widget_btn_bg_retro,
        btnAccentBgRes = R.drawable.widget_btn_accent_retro,
        textColorHex = 0xFF00FF66.toInt(),
        subtextColorHex = 0xFF00B347.toInt(),
        accentColorHex = 0xFF00FF66.toInt(),
        headerTextColorHex = 0xFF3D362A.toInt(),
        composeBackground = Color(0xFFD6CEBE),
        composeAccent = Color(0xFF00FF66)
    )
}
