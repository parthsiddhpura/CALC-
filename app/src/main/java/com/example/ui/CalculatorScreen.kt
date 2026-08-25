package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CalculatorMode
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.EditNoteDialog
import com.example.ui.components.HistorySheet
import com.example.ui.components.ProgrammerKeypad
import com.example.ui.components.QuickThemeSwitcher
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.StandardKeypad
import com.example.ui.components.ThemePickerSheet
import com.example.ui.components.TipSplitterView
import com.example.ui.components.UnitConverterView
import com.example.ui.theme.CalculatorThemes
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val theme = remember(uiState.currentThemeId) {
        CalculatorThemes.getThemeById(uiState.currentThemeId)
    }
    val haptics = LocalHapticFeedback.current

    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundBrush)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .widthIn(max = 650.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top App Header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title & Theme Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setShowThemeSheet(true) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.accentColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = theme.backgroundColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "CALC +",
                                    color = theme.screenTextColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Theme: ${theme.name}",
                                    color = theme.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Action Icons: Theme Selector & History
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Theme Gallery Button
                            IconButton(
                                onClick = { viewModel.setShowThemeSheet(true) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(theme.surfaceColor)
                                    .testTag("btn_open_theme_picker")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Change Theme",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // History Tape Button
                            IconButton(
                                onClick = { viewModel.setShowHistorySheet(true) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(theme.surfaceColor)
                                    .testTag("btn_open_history")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (historyList.isNotEmpty()) {
                                            Badge(
                                                containerColor = theme.secondaryAccent,
                                                contentColor = theme.backgroundColor
                                            ) {
                                                Text(
                                                    text = "${historyList.size.coerceAtMost(99)}",
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "View History",
                                        tint = theme.screenTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Theme Switcher Bar
                    QuickThemeSwitcher(
                        activeTheme = theme,
                        onSelectTheme = { viewModel.setTheme(it) },
                        onNextTheme = { viewModel.cycleNextTheme() },
                        onPrevTheme = { viewModel.cyclePrevTheme() },
                        onRandomTheme = { viewModel.randomizeTheme() },
                        onOpenFullGallery = { viewModel.setShowThemeSheet(true) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Mode Switcher Tabs
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(CalculatorMode.entries.toTypedArray(), key = { it.name }) { modeItem ->
                            val isSelected = modeItem == uiState.mode
                            Surface(
                                color = if (isSelected) theme.accentColor else theme.surfaceColor,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .clickable { viewModel.setMode(modeItem) }
                                    .testTag("tab_mode_${modeItem.name.lowercase()}")
                            ) {
                                Text(
                                    text = modeItem.title,
                                    color = if (isSelected) theme.backgroundColor else theme.screenExpressionColor,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Middle Content & Screen Display
                AnimatedContent(
                    targetState = uiState.mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "mode_transition",
                    modifier = Modifier.weight(1f)
                ) { targetMode ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        when (targetMode) {
                            CalculatorMode.STANDARD -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    CalculatorDisplay(
                                        expression = uiState.expression,
                                        result = uiState.result,
                                        previewResult = uiState.previewResult,
                                        theme = theme,
                                        angleMode = uiState.angleMode,
                                        hasMemory = uiState.hasMemory,
                                        mode = uiState.mode,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    StandardKeypad(
                                        theme = theme,
                                        onInput = { viewModel.onInput(it, haptics) },
                                        onClear = { viewModel.onClear(haptics) },
                                        onBackspace = { viewModel.onBackspace(haptics) },
                                        onNegate = { viewModel.onNegate(haptics) },
                                        onEquals = { viewModel.onEquals(haptics) },
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            CalculatorMode.SCIENTIFIC -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    CalculatorDisplay(
                                        expression = uiState.expression,
                                        result = uiState.result,
                                        previewResult = uiState.previewResult,
                                        theme = theme,
                                        angleMode = uiState.angleMode,
                                        hasMemory = uiState.hasMemory,
                                        mode = uiState.mode,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    ScientificKeypad(
                                        theme = theme,
                                        angleMode = uiState.angleMode,
                                        isSecondFunction = uiState.isSecondFunction,
                                        onToggleAngleMode = { viewModel.toggleAngleMode() },
                                        onToggleSecondFunction = { viewModel.toggleSecondFunction() },
                                        onInput = { viewModel.onInput(it, haptics) },
                                        onFunction = { viewModel.onFunction(it, haptics) },
                                        onConstant = { viewModel.onConstant(it, haptics) },
                                        onClear = { viewModel.onClear(haptics) },
                                        onBackspace = { viewModel.onBackspace(haptics) },
                                        onNegate = { viewModel.onNegate(haptics) },
                                        onEquals = { viewModel.onEquals(haptics) },
                                        onMemoryAdd = { viewModel.onMemoryAdd(haptics) },
                                        onMemorySubtract = { viewModel.onMemorySubtract(haptics) },
                                        onMemoryRecall = { viewModel.onMemoryRecall(haptics) },
                                        onMemoryClear = { viewModel.onMemoryClear(haptics) },
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            CalculatorMode.PROGRAMMER -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    CalculatorDisplay(
                                        expression = uiState.progPendingOp?.let { "${uiState.progStoredValue ?: 0} $it" } ?: "",
                                        result = uiState.progInput,
                                        previewResult = null,
                                        theme = theme,
                                        angleMode = uiState.angleMode,
                                        hasMemory = false,
                                        mode = uiState.mode,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    ProgrammerKeypad(
                                        theme = theme,
                                        value = uiState.progValue,
                                        activeBase = uiState.progBase,
                                        wordSize = uiState.progWordSize,
                                        onBaseSelect = { viewModel.setProgBase(it) },
                                        onWordSizeSelect = { viewModel.setProgWordSize(it) },
                                        onDigit = { viewModel.onProgDigit(it, haptics) },
                                        onBitwiseOp = { viewModel.onProgBitwiseOp(it, haptics) },
                                        onBitToggle = { viewModel.onProgBitToggle(it, haptics) },
                                        onClear = { viewModel.onProgClear(haptics) },
                                        onBackspace = { viewModel.onProgBackspace(haptics) },
                                        onEquals = { viewModel.onProgEquals(haptics) },
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            CalculatorMode.UNIT_CONVERTER -> {
                                UnitConverterView(
                                    theme = theme,
                                    category = uiState.convCategory,
                                    fromUnit = uiState.convFromUnit,
                                    toUnit = uiState.convToUnit,
                                    inputValue = uiState.convInput,
                                    outputValue = uiState.convOutput,
                                    onCategorySelect = { viewModel.setUnitCategory(it) },
                                    onFromUnitSelect = { viewModel.setFromUnit(it) },
                                    onToUnitSelect = { viewModel.setToUnit(it) },
                                    onSwapUnits = { viewModel.swapUnits() },
                                    onInputChange = { viewModel.onConverterInput(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            CalculatorMode.TIP_SPLIT -> {
                                TipSplitterView(
                                    theme = theme,
                                    billInput = uiState.tipBillInput,
                                    tipPercent = uiState.tipPercent,
                                    peopleCount = uiState.tipPeopleCount,
                                    tipAmount = uiState.tipAmount,
                                    totalAmount = uiState.tipTotal,
                                    perPersonAmount = uiState.tipPerPerson,
                                    onBillChange = { viewModel.onTipBillChange(it) },
                                    onTipPercentChange = { viewModel.onTipPercentChange(it) },
                                    onPeopleChange = { viewModel.onTipPeopleChange(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Theme Gallery Bottom Sheet
        if (uiState.showThemeSheet) {
            ThemePickerSheet(
                activeTheme = theme,
                onSelectTheme = { viewModel.setTheme(it) },
                onRandomTheme = { viewModel.randomizeTheme() },
                isSoundEnabled = uiState.isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                isHapticsEnabled = uiState.isHapticsEnabled,
                onToggleHaptics = { viewModel.toggleHaptics() },
                onDismiss = { viewModel.setShowThemeSheet(false) },
                sheetState = themeSheetState
            )
        }

        // Calculation History Bottom Sheet
        if (uiState.showHistorySheet) {
            HistorySheet(
                historyList = historyList,
                theme = theme,
                searchQuery = uiState.historySearchQuery,
                onSearchQueryChange = { viewModel.setHistorySearchQuery(it) },
                onlyFavorites = uiState.historyOnlyFavorites,
                onToggleOnlyFavorites = { viewModel.setHistoryOnlyFavorites(it) },
                onUseItem = { viewModel.useHistoryItem(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onEditNote = { viewModel.setEditingNoteFor(it) },
                onDeleteItem = { viewModel.deleteHistoryItem(it) },
                onClearAll = { viewModel.clearAllHistory() },
                onDismiss = { viewModel.setShowHistorySheet(false) },
                sheetState = historySheetState
            )
        }

        // Edit History Note Dialog
        if (uiState.editingNoteFor != null) {
            EditNoteDialog(
                history = uiState.editingNoteFor!!,
                theme = theme,
                onSave = { note -> viewModel.saveHistoryNote(uiState.editingNoteFor!!, note) },
                onDismiss = { viewModel.setEditingNoteFor(null) }
            )
        }
    }
}
