package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LanguageStrings
import com.example.model.AppLanguage
import com.example.model.ButtonShapeType
import com.example.model.DisplayConfig
import com.example.model.DisplayFontType
import com.example.model.DisplayFormatter
import com.example.model.DisplayNotation
import com.example.model.DisplayPrecisionMode
import com.example.model.DisplayScaleSize
import com.example.model.DisplaySeparatorStyle
import com.example.model.ThemeId
import com.example.model.ThemePalette
import com.example.ui.theme.CalculatorThemes

data class AccentColorPreset(
    val name: String,
    val colorValue: Long, // ARGB Long
    val color: Color
)

val ACCENT_PRESETS = listOf(
    AccentColorPreset("Apple Blue", 0xFF007AFF, Color(0xFF007AFF)),
    AccentColorPreset("Cyber Yellow", 0xFFFFE600, Color(0xFFFFE600)),
    AccentColorPreset("Emerald", 0xFF10B981, Color(0xFF10B981)),
    AccentColorPreset("Sunset Orange", 0xFFFF5722, Color(0xFFFF5722)),
    AccentColorPreset("Neon Pink", 0xFFFF2A85, Color(0xFFFF2A85)),
    AccentColorPreset("Electric Violet", 0xFFA855F7, Color(0xFFA855F7)),
    AccentColorPreset("Cyan Glow", 0xFF06B6D4, Color(0xFF06B6D4)),
    AccentColorPreset("Crimson Red", 0xFFEF4444, Color(0xFFEF4444)),
    AccentColorPreset("Gold Amber", 0xFFF59E0B, Color(0xFFF59E0B)),
    AccentColorPreset("Mint Green", 0xFF2EC4B6, Color(0xFF2EC4B6)),
    AccentColorPreset("Coral Peach", 0xFFFF70A6, Color(0xFFFF70A6)),
    AccentColorPreset("Indigo Deep", 0xFF6366F1, Color(0xFF6366F1)),
    AccentColorPreset("Lime Punch", 0xFF84CC16, Color(0xFF84CC16)),
    AccentColorPreset("Hot Magenta", 0xFFEC4899, Color(0xFFEC4899)),
    AccentColorPreset("Teal Marine", 0xFF14B8A6, Color(0xFF14B8A6)),
    AccentColorPreset("Mac White", 0xFFFFFFFF, Color(0xFFFFFFFF)),
    AccentColorPreset("Rose Gold", 0xFFE0A96D, Color(0xFFE0A96D)),
    AccentColorPreset("Sky Blue", 0xFF38BDF8, Color(0xFF38BDF8)),
    AccentColorPreset("Ruby Red", 0xFFBE123C, Color(0xFFBE123C)),
    AccentColorPreset("Tangerine", 0xFFFB923C, Color(0xFFFB923C)),
    AccentColorPreset("Pure Charcoal", 0xFF374151, Color(0xFF374151)),
    AccentColorPreset("Lavender", 0xFFC084FC, Color(0xFFC084FC)),
    AccentColorPreset("Turquoise", 0xFF00F5D4, Color(0xFF00F5D4)),
    AccentColorPreset("Sunburst Gold", 0xFFFFD166, Color(0xFFFFD166))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    activeTheme: ThemePalette,
    onSelectTheme: (ThemeId) -> Unit,
    customAccentColor: Long?,
    onSelectAccentColor: (Long?) -> Unit,
    customShapeType: ButtonShapeType?,
    onSelectShapeType: (ButtonShapeType?) -> Unit,
    customDisplayFont: DisplayFontType?,
    onSelectDisplayFont: (DisplayFontType?) -> Unit,
    displayConfig: DisplayConfig,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectDisplaySeparator: (DisplaySeparatorStyle) -> Unit,
    onSelectDisplayPrecision: (DisplayPrecisionMode) -> Unit,
    onSelectDisplayScale: (DisplayScaleSize) -> Unit,
    onSelectDisplayNotation: (DisplayNotation) -> Unit,
    onToggleLivePreview: () -> Unit,
    onToggleStatusBadges: () -> Unit,
    onToggleScanlinesOverride: () -> Unit,
    onToggleCopyOnTap: () -> Unit,
    onResetDisplaySettings: () -> Unit,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    isHapticsEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    onResetAppearance: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("appearance") } // "appearance", "display", "language", "haptics", "about"
    var themeCategory by remember { mutableStateOf("All") }
    var themeSearchQuery by remember { mutableStateOf("") }
    var themeFilterDarkLight by remember { mutableStateOf("All") }
    var languageSearchQuery by remember { mutableStateOf("") }

    val haptics = LocalHapticFeedback.current

    val categories = listOf("All", "Retro", "Futuristic", "Modern Art", "Minimal & Aesthetic", "Atmospheric", "Playful", "Luxury & Clean")

    val filteredThemes = remember(themeCategory, themeSearchQuery, themeFilterDarkLight) {
        CalculatorThemes.allThemes.filter { theme ->
            val matchesCategory = if (themeCategory == "All") true else theme.category.contains(themeCategory, ignoreCase = true)
            val matchesSearch = if (themeSearchQuery.isBlank()) true else {
                theme.name.contains(themeSearchQuery, ignoreCase = true) ||
                theme.subtitle.contains(themeSearchQuery, ignoreCase = true) ||
                theme.category.contains(themeSearchQuery, ignoreCase = true)
            }
            val matchesMode = when (themeFilterDarkLight) {
                "Dark Only" -> theme.isDark
                "Light Only" -> !theme.isDark
                else -> true
            }
            matchesCategory && matchesSearch && matchesMode
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = activeTheme.surfaceColor,
        contentColor = activeTheme.screenTextColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = activeTheme.accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = activeTheme.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = LanguageStrings.settingsTitle(currentLanguage),
                            color = activeTheme.screenTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Themes, Languages, Audio & Haptics",
                            color = activeTheme.screenExpressionColor,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = activeTheme.screenTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Segmented Bar (Appearance, Display, Language, Haptics, About)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(activeTheme.cardBackground)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    Triple("appearance", LanguageStrings.tabThemes(currentLanguage), Icons.Default.Palette),
                    Triple("display", LanguageStrings.tabDisplay(currentLanguage), Icons.Default.Tv),
                    Triple("language", LanguageStrings.tabLanguage(currentLanguage), Icons.Default.Translate),
                    Triple("haptics", LanguageStrings.tabHaptics(currentLanguage), Icons.Default.Vibration),
                    Triple("about", LanguageStrings.tabAbout(currentLanguage), Icons.Default.Info)
                ).forEach { (sectionKey, label, icon) ->
                    val isSelected = selectedSection == sectionKey
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) activeTheme.accentColor else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSection = sectionKey }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) activeTheme.backgroundColor else activeTheme.screenExpressionColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = label,
                                color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenExpressionColor,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section Content
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedSection == "appearance") {
                    // --- 1. ACCENT COLOR CUSTOMIZATION ---
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ColorLens,
                                            contentDescription = null,
                                            tint = activeTheme.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Accent Color",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (customAccentColor != null) {
                                        Text(
                                            text = "Reset",
                                            color = activeTheme.accentColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable { onSelectAccentColor(null) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Default Option
                                    item {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { onSelectAccentColor(null) }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(activeTheme.accentColor)
                                                    .border(
                                                        width = if (customAccentColor == null) 3.dp else 1.dp,
                                                        color = if (customAccentColor == null) activeTheme.screenTextColor else Color.Transparent,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (customAccentColor == null) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Default active",
                                                        tint = activeTheme.backgroundColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Theme",
                                                color = activeTheme.screenExpressionColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    // Presets
                                    items(ACCENT_PRESETS) { preset ->
                                        val isSelected = customAccentColor == preset.colorValue
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { onSelectAccentColor(preset.colorValue) }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(preset.color)
                                                    .border(
                                                        width = if (isSelected) 3.dp else 1.dp,
                                                        color = if (isSelected) activeTheme.screenTextColor else Color.Transparent,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = if (preset.name == "Mac White" || preset.name == "Cyber Yellow") Color.Black else Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = preset.name.split(" ").first(),
                                                color = activeTheme.screenExpressionColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- 2. BUTTON SHAPE CUSTOMIZATION ---
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null,
                                            tint = activeTheme.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Button Shape",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (customShapeType != null) {
                                        Text(
                                            text = "Reset",
                                            color = activeTheme.accentColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable { onSelectShapeType(null) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val shapes = listOf(
                                        Pair(null, "Theme"),
                                        Pair(ButtonShapeType.SQUIRCLE, "Squircle"),
                                        Pair(ButtonShapeType.PILL, "Pill"),
                                        Pair(ButtonShapeType.ROUNDED_SQUARE, "Square"),
                                        Pair(ButtonShapeType.BRUTALIST_RECT, "Sharp")
                                    )

                                    shapes.forEach { (shape, name) ->
                                        val isSelected = (shape == null && customShapeType == null) || (shape == customShapeType)
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor else activeTheme.surfaceColor,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSelectShapeType(shape) }
                                        ) {
                                            Text(
                                                text = name,
                                                color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenTextColor,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- 3. THEMES SEARCH & CATEGORY BAR ---
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = activeTheme.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Themes Gallery (${filteredThemes.size})",
                                        color = activeTheme.screenTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Search Field
                            OutlinedTextField(
                                value = themeSearchQuery,
                                onValueChange = { themeSearchQuery = it },
                                placeholder = { Text("Search themes (e.g. Neo, Cyber, Retro)...", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = activeTheme.screenExpressionColor
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = activeTheme.accentColor,
                                    unfocusedBorderColor = activeTheme.surfaceColor,
                                    focusedContainerColor = activeTheme.cardBackground,
                                    unfocusedContainerColor = activeTheme.cardBackground,
                                    focusedTextColor = activeTheme.screenTextColor,
                                    unfocusedTextColor = activeTheme.screenTextColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Categories
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { cat ->
                                    val isCatSelected = cat == themeCategory
                                    Surface(
                                        color = if (isCatSelected) activeTheme.accentColor else activeTheme.cardBackground,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { themeCategory = cat }
                                    ) {
                                        Text(
                                            text = cat,
                                            color = if (isCatSelected) activeTheme.backgroundColor else activeTheme.screenExpressionColor,
                                            fontSize = 12.sp,
                                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Themes Grid
                    items(filteredThemes.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { themeItem ->
                                val isThemeSelected = themeItem.id == activeTheme.id
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = themeItem.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isThemeSelected) 2.5.dp else 1.dp,
                                        color = if (isThemeSelected) activeTheme.accentColor else themeItem.screenBorderColor.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectTheme(themeItem.id) }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = themeItem.name,
                                                color = themeItem.screenTextColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            if (isThemeSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Active",
                                                    tint = activeTheme.accentColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = themeItem.subtitle,
                                            color = themeItem.screenExpressionColor,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Mini Color Swatches
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(themeItem.screenBackground)
                                                    .border(0.5.dp, Color.Gray, CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(themeItem.accentColor)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(themeItem.numberButtonBg)
                                                    .border(0.5.dp, Color.Gray, CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(themeItem.operatorButtonBg)
                                            )
                                        }
                                    }
                                }
                            }

                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else if (selectedSection == "display") {
                    // ==========================================
                    // --- DEDICATED DISPLAY & NUMBER FORMATTING ---
                    // ==========================================

                    // 1. Interactive Live Display Preview Box
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = activeTheme.screenBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                activeTheme.screenBorderColor.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val demoFont = when (customDisplayFont ?: activeTheme.displayFont) {
                                DisplayFontType.MONOSPACE, DisplayFontType.DIGITAL_LCD -> FontFamily.Monospace
                                else -> FontFamily.SansSerif
                            }

                            val sampleExpr = "1250000 × 1.18 + 500"
                            val sampleResult = "1475500"
                            val formattedSampleExpr = DisplayFormatter.formatExpression(sampleExpr, displayConfig.separatorStyle)
                            val formattedSampleResult = DisplayFormatter.formatNumber(
                                sampleResult,
                                displayConfig.separatorStyle,
                                displayConfig.precisionMode,
                                displayConfig.notation
                            )

                            Box(modifier = Modifier.padding(14.dp)) {
                                if (activeTheme.hasScanlines) {
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

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (displayConfig.showStatusBadges) {
                                                Surface(
                                                    color = activeTheme.accentColor.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "DEG",
                                                        color = activeTheme.accentColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Surface(
                                                    color = activeTheme.secondaryAccent.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "M",
                                                        color = activeTheme.secondaryAccent,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "LIVE DISPLAY PREVIEW",
                                            color = activeTheme.accentColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = formattedSampleExpr,
                                        color = activeTheme.screenExpressionColor,
                                        fontSize = 16.sp,
                                        fontFamily = demoFont,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (displayConfig.showLivePreview) {
                                        Text(
                                            text = "= $formattedSampleResult",
                                            color = activeTheme.screenPreviewColor,
                                            fontSize = 14.sp,
                                            fontFamily = demoFont,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Text(
                                        text = formattedSampleResult,
                                        color = activeTheme.screenTextColor,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = demoFont,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // 2. Display Font Selector
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FontDownload,
                                            contentDescription = null,
                                            tint = activeTheme.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Display Font Type",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (customDisplayFont != null) {
                                        Text(
                                            text = "Reset",
                                            color = activeTheme.accentColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable { onSelectDisplayFont(null) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val fonts = listOf(
                                        Pair(null, "Theme"),
                                        Pair(DisplayFontType.MONOSPACE, "Mono"),
                                        Pair(DisplayFontType.DIGITAL_LCD, "LCD"),
                                        Pair(DisplayFontType.MODERN_SANS, "Sans"),
                                        Pair(DisplayFontType.ROUNDED, "Round")
                                    )

                                    fonts.forEach { (font, name) ->
                                        val isSelected = (font == null && customDisplayFont == null) || (font == customDisplayFont)
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor else activeTheme.surfaceColor,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSelectDisplayFont(font) }
                                        ) {
                                            Text(
                                                text = name,
                                                color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenTextColor,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Thousands Separator / Number Formatting
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Numbers,
                                        contentDescription = null,
                                        tint = activeTheme.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Thousands Separator",
                                        color = activeTheme.screenTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    DisplaySeparatorStyle.values().forEach { style ->
                                        val isSelected = displayConfig.separatorStyle == style
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor.copy(alpha = 0.15f) else activeTheme.surfaceColor,
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, activeTheme.accentColor) else null,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onSelectDisplaySeparator(style) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = style.displayName,
                                                        color = activeTheme.screenTextColor,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = "e.g. ${style.sample}",
                                                        color = activeTheme.screenExpressionColor,
                                                        fontSize = 11.sp
                                                    )
                                                }

                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = activeTheme.accentColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Decimal Precision
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = activeTheme.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Decimal Precision Mode",
                                        color = activeTheme.screenTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val precisions = listOf(
                                        Pair(DisplayPrecisionMode.AUTO, "Auto"),
                                        Pair(DisplayPrecisionMode.TWO_DECIMALS, "2 Dec"),
                                        Pair(DisplayPrecisionMode.FOUR_DECIMALS, "4 Dec"),
                                        Pair(DisplayPrecisionMode.SIX_DECIMALS, "6 Dec"),
                                        Pair(DisplayPrecisionMode.EXACT, "Exact")
                                    )

                                    precisions.forEach { (prec, name) ->
                                        val isSelected = displayConfig.precisionMode == prec
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor else activeTheme.surfaceColor,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSelectDisplayPrecision(prec) }
                                        ) {
                                            Text(
                                                text = name,
                                                color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenTextColor,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. Display Text Sizing
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = null,
                                        tint = activeTheme.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Display Size & Scaling",
                                        color = activeTheme.screenTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DisplayScaleSize.values().forEach { scale ->
                                        val isSelected = displayConfig.scaleSize == scale
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor else activeTheme.surfaceColor,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSelectDisplayScale(scale) }
                                        ) {
                                            Text(
                                                text = scale.displayName,
                                                color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenTextColor,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 9.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 6. Number Notation (Standard, Scientific, Engineering)
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Number Notation",
                                    color = activeTheme.screenTextColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DisplayNotation.values().forEach { not ->
                                        val isSelected = displayConfig.notation == not
                                        Surface(
                                            color = if (isSelected) activeTheme.accentColor else activeTheme.surfaceColor,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSelectDisplayNotation(not) }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 7.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = not.displayName.split(" ").first(),
                                                    color = if (isSelected) activeTheme.backgroundColor else activeTheme.screenTextColor,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                                Text(
                                                    text = not.sample,
                                                    color = if (isSelected) activeTheme.backgroundColor.copy(alpha = 0.8f) else activeTheme.screenExpressionColor,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 7. Display Switches: Live Preview, Badges, Scanlines, Tap to Copy
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Live Preview Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Live Calculation Preview",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Show intermediate \"= result\" preview in real time",
                                            color = activeTheme.screenExpressionColor,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = displayConfig.showLivePreview,
                                        onCheckedChange = { onToggleLivePreview() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = activeTheme.backgroundColor,
                                            checkedTrackColor = activeTheme.accentColor
                                        )
                                    )
                                }

                                HorizontalDivider(color = activeTheme.surfaceColor)

                                // Status Badges Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Status Indicators & Badges",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Show Mode, Angle (DEG/RAD) & Memory (M) indicators",
                                            color = activeTheme.screenExpressionColor,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = displayConfig.showStatusBadges,
                                        onCheckedChange = { onToggleStatusBadges() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = activeTheme.backgroundColor,
                                            checkedTrackColor = activeTheme.accentColor
                                        )
                                    )
                                }

                                HorizontalDivider(color = activeTheme.surfaceColor)

                                // Quick Tap to Copy Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Tap Result to Copy",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Instant 1-tap copy to clipboard from display screen",
                                            color = activeTheme.screenExpressionColor,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = displayConfig.copyOnTap,
                                        onCheckedChange = { onToggleCopyOnTap() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = activeTheme.backgroundColor,
                                            checkedTrackColor = activeTheme.accentColor
                                        )
                                    )
                                }

                                HorizontalDivider(color = activeTheme.surfaceColor)

                                // Retro Scanlines Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Retro CRT Scanline Overlay",
                                            color = activeTheme.screenTextColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (displayConfig.showScanlinesOverride == null) "Theme Default" else if (displayConfig.showScanlinesOverride == true) "Forced ON" else "Forced OFF",
                                            color = activeTheme.screenExpressionColor,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Button(
                                        onClick = onToggleScanlinesOverride,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = activeTheme.surfaceColor,
                                            contentColor = activeTheme.accentColor
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = when (displayConfig.showScanlinesOverride) {
                                                null -> "Theme"
                                                true -> "ON"
                                                false -> "OFF"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 8. Reset Display Settings
                    item {
                        Button(
                            onClick = onResetDisplaySettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = activeTheme.cardBackground,
                                contentColor = activeTheme.accentColor
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Display Settings to Defaults", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (selectedSection == "language") {
                    // --- LANGUAGE SELECTION SECTION ---
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = "Language",
                                        tint = activeTheme.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LanguageStrings.chooseLanguage(currentLanguage),
                                            color = activeTheme.screenTextColor,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Select your preferred language for calculations and tax labels",
                                            color = activeTheme.screenExpressionColor,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                // Search Field
                                OutlinedTextField(
                                    value = languageSearchQuery,
                                    onValueChange = { languageSearchQuery = it },
                                    placeholder = { Text("Search language or country...") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = activeTheme.screenExpressionColor
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = activeTheme.surfaceColor,
                                        unfocusedContainerColor = activeTheme.surfaceColor,
                                        focusedBorderColor = activeTheme.accentColor,
                                        unfocusedBorderColor = activeTheme.surfaceColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                HorizontalDivider(color = activeTheme.surfaceColor)

                                val filteredLanguages = remember(languageSearchQuery) {
                                    AppLanguage.values().filter { lang ->
                                        if (languageSearchQuery.isBlank()) true else {
                                            lang.nativeName.contains(languageSearchQuery, ignoreCase = true) ||
                                            lang.displayName.contains(languageSearchQuery, ignoreCase = true) ||
                                            lang.regionName.contains(languageSearchQuery, ignoreCase = true)
                                        }
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    filteredLanguages.forEach { lang ->
                                        val isSelected = lang == currentLanguage
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) activeTheme.accentColor.copy(alpha = 0.18f) else activeTheme.surfaceColor,
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, activeTheme.accentColor) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onSelectLanguage(lang)
                                                }
                                                .testTag("lang_option_${lang.code}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(
                                                        text = lang.flagEmoji,
                                                        fontSize = 24.sp
                                                    )
                                                    Column {
                                                        Text(
                                                            text = lang.nativeName,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = activeTheme.screenTextColor
                                                        )
                                                        Text(
                                                            text = "${lang.displayName} • ${lang.regionName}",
                                                            fontSize = 12.sp,
                                                            color = activeTheme.screenExpressionColor
                                                        )
                                                    }
                                                }

                                                if (isSelected) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = activeTheme.accentColor,
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Selected",
                                                                tint = activeTheme.backgroundColor,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (selectedSection == "haptics") {
                    // --- HAPTICS & SOUND SETTINGS ---
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Haptics Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Vibration,
                                            contentDescription = "Haptics",
                                            tint = activeTheme.accentColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Tactile Haptics",
                                                color = activeTheme.screenTextColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Vibrate on button press & operations",
                                                color = activeTheme.screenExpressionColor,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = isHapticsEnabled,
                                        onCheckedChange = { onToggleHaptics() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = activeTheme.backgroundColor,
                                            checkedTrackColor = activeTheme.accentColor
                                        )
                                    )
                                }

                                if (isHapticsEnabled) {
                                    Button(
                                        onClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = activeTheme.surfaceColor,
                                            contentColor = activeTheme.accentColor
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Test Vibration Feedback", fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(
                                    color = activeTheme.surfaceColor,
                                    thickness = 1.dp
                                )

                                // Sound Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Sound",
                                            tint = activeTheme.secondaryAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Keystroke Audio",
                                                color = activeTheme.screenTextColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Mechanical audio click sound effects",
                                                color = activeTheme.screenExpressionColor,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = isSoundEnabled,
                                        onCheckedChange = { onToggleSound() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = activeTheme.backgroundColor,
                                            checkedTrackColor = activeTheme.secondaryAccent
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else if (selectedSection == "about") {
                    // --- ABOUT & RESET ---
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = activeTheme.cardBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "CALC +",
                                    color = activeTheme.screenTextColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Version 8.8 • Featuring 25+ Retro & Modern Themes, Multi-Language Internationalization, Rich Display Customization, GST & Global Tax Engine, Scientific & Programmer calculators, Live Age Chronometer, EMI and Unit Converters.",
                                    color = activeTheme.screenExpressionColor,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                HorizontalDivider(color = activeTheme.surfaceColor)

                                Button(
                                    onClick = onResetAppearance,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = activeTheme.surfaceColor,
                                        contentColor = activeTheme.accentColor
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reset Appearance Customizations", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
