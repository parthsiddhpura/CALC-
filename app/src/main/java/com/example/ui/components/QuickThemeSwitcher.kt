package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeId
import com.example.model.ThemePalette
import com.example.ui.theme.CalculatorThemes

@Composable
fun QuickThemeSwitcher(
    activeTheme: ThemePalette,
    onSelectTheme: (ThemeId) -> Unit,
    onNextTheme: () -> Unit,
    onPrevTheme: () -> Unit,
    onRandomTheme: () -> Unit,
    onOpenFullGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    val listState = rememberLazyListState()

    val filteredThemes = remember(selectedFilter) {
        when (selectedFilter) {
            "Dark" -> CalculatorThemes.allThemes.filter { it.isDark }
            "Light" -> CalculatorThemes.allThemes.filter { !it.isDark }
            else -> CalculatorThemes.allThemes
        }
    }

    // Scroll to selected theme only when expanded
    LaunchedEffect(activeTheme.id, selectedFilter, isExpanded) {
        if (isExpanded) {
            val index = filteredThemes.indexOfFirst { it.id == activeTheme.id }
            if (index >= 0) {
                listState.scrollToItem(index)
            }
        }
    }

    Surface(
        color = activeTheme.surfaceColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_theme_switcher_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Main Compact Switcher Bar (Always Visible)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Prev theme button & Active Theme Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onPrevTheme,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(activeTheme.cardBackground)
                            .testTag("quick_theme_prev")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Previous theme",
                            tint = activeTheme.screenTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    // Theme Name & Swatch Pills
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isExpanded = !isExpanded }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        // Mini color dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(activeTheme.accentColor)
                                .border(1.dp, activeTheme.screenTextColor.copy(alpha = 0.5f), CircleShape)
                        )

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activeTheme.name,
                                    color = activeTheme.screenTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    color = activeTheme.accentColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (activeTheme.isDark) "Dark" else "Light",
                                        color = activeTheme.accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = activeTheme.category,
                                color = activeTheme.screenExpressionColor,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onNextTheme,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(activeTheme.cardBackground)
                            .testTag("quick_theme_next")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Next theme",
                            tint = activeTheme.screenTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Right Quick Actions: Randomize, Full Gallery, Expand Tray
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Random Theme Shuffle Button
                    IconButton(
                        onClick = onRandomTheme,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(activeTheme.cardBackground)
                            .testTag("quick_theme_random")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Randomize theme",
                            tint = activeTheme.secondaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Full Gallery Sheet Button
                    IconButton(
                        onClick = onOpenFullGallery,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(activeTheme.cardBackground)
                            .testTag("quick_theme_gallery_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "All Themes Gallery",
                            tint = activeTheme.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Expand / Collapse quick carousel
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(activeTheme.cardBackground)
                            .testTag("quick_theme_expand_toggle")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Hide Swatches" else "Show Swatches",
                            tint = activeTheme.screenExpressionColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Theme Swatches Carousel (Expandable or visible)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Quick Filter Pills (All / Dark / Light)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("All", "Dark", "Light").forEach { filter ->
                            val isFilterSelected = selectedFilter == filter
                            Surface(
                                color = if (isFilterSelected) activeTheme.accentColor else activeTheme.cardBackground,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { selectedFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isFilterSelected) activeTheme.backgroundColor else activeTheme.screenExpressionColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (isFilterSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "${filteredThemes.size} styles",
                            color = activeTheme.screenExpressionColor,
                            fontSize = 11.sp
                        )
                    }

                    // Horizontal Theme Swatches Tray
                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(filteredThemes, key = { it.id.name }) { itemTheme ->
                            val isSelected = itemTheme.id == activeTheme.id
                            QuickThemePill(
                                theme = itemTheme,
                                isSelected = isSelected,
                                onSelect = { onSelectTheme(itemTheme.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickThemePill(
    theme: ThemePalette,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = theme.backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, theme.accentColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier.border(0.8.dp, theme.surfaceColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .testTag("quick_theme_item_${theme.id.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Mini 3-color palette dot stack
            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(theme.accentColor)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(theme.secondaryAccent)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(theme.operatorButtonBg)
                )
            }

            Text(
                text = theme.name,
                color = theme.screenTextColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = theme.accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
