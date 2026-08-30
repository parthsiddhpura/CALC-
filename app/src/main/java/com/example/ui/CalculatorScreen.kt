package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.LanguageStrings
import com.example.model.CalculatorMode
import com.example.ui.components.AgeCalculatorView
import com.example.ui.components.BmiCalculatorView
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.DecimalConverterSheet
import com.example.ui.components.EditNoteDialog
import com.example.ui.components.EmiCalculatorView
import com.example.ui.components.EngineeringCalculatorView
import com.example.ui.components.GstCalculatorView
import com.example.ui.components.HistorySheet
import com.example.ui.components.MoreModesSheet
import com.example.ui.components.ProgrammerKeypad
import com.example.ui.components.RealCurrencyConverterView
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.SettingsSheet
import com.example.ui.components.StandardKeypad
import com.example.ui.components.TipSplitterView
import com.example.ui.components.UnitConverterView
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val ageProfiles by viewModel.ageProfilesList.collectAsStateWithLifecycle()
    val theme = remember(uiState) {
        viewModel.getEffectiveTheme(uiState)
    }
    val haptics = LocalHapticFeedback.current

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val moreModesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val decimalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                // Clean Minimal Top Header Bar (No clutter, No shuffle, Single Settings Access)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = theme.accentColor,
                                modifier = Modifier.size(10.dp)
                            ) {}

                            Text(
                                text = "CALC +",
                                color = theme.screenTextColor,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceColor
                            ) {
                                Text(
                                    text = theme.name,
                                    color = theme.screenExpressionColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Settings Icon Button
                        IconButton(
                            onClick = { viewModel.setShowSettingsSheet(true) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(theme.surfaceColor)
                                .size(36.dp)
                                .testTag("btn_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings & Appearance",
                                tint = theme.screenTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Reorganized Navigation: 3 Main Buttons (Standard, GST Calc, Scientific) + "More" Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val mainModes: List<Pair<CalculatorMode, String>> = listOf(
                            Pair(CalculatorMode.STANDARD, LanguageStrings.modeStandard(uiState.currentLanguage)),
                            Pair(CalculatorMode.GST_CALCULATOR, LanguageStrings.modeGst(uiState.currentLanguage)),
                            Pair(CalculatorMode.SCIENTIFIC, LanguageStrings.modeScientific(uiState.currentLanguage))
                        )

                        mainModes.forEach { (modeItem, label) ->
                            val isSelected = uiState.mode == modeItem
                            Surface(
                                color = if (isSelected) theme.accentColor else theme.surfaceColor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setMode(modeItem) }
                                    .testTag("tab_mode_${modeItem.name.lowercase()}")
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) theme.backgroundColor else theme.screenTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // "More" Button (with active mode indicator if a secondary mode is selected)
                        val isMoreSelected = uiState.mode !in listOf(
                            CalculatorMode.STANDARD,
                            CalculatorMode.GST_CALCULATOR,
                            CalculatorMode.SCIENTIFIC
                        )

                        Surface(
                            color = if (isMoreSelected) theme.accentColor else theme.surfaceColor,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setShowMoreModesSheet(true) }
                                .testTag("tab_mode_more")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isMoreSelected) uiState.mode.shortName else LanguageStrings.modeMore(uiState.currentLanguage),
                                    color = if (isMoreSelected) theme.backgroundColor else theme.screenTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isMoreSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "More modes",
                                    tint = if (isMoreSelected) theme.backgroundColor else theme.screenTextColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Middle Content & Screen Display (Expands to fill available space)
                AnimatedContent(
                    targetState = uiState.mode,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                         scaleIn(initialScale = 0.97f, animationSpec = tween(180, easing = FastOutSlowInEasing)))
                            .togetherWith(
                                fadeOut(animationSpec = tween(120)) +
                                scaleOut(targetScale = 0.99f, animationSpec = tween(120))
                            )
                    },
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
                                        displayConfig = uiState.displayConfig,
                                        historyCount = historyList.size,
                                        onToggleAngleMode = { viewModel.toggleAngleMode() },
                                        onOpenHistory = { viewModel.setShowHistorySheet(true) },
                                        onOpenDecimalConverter = { viewModel.setShowDecimalConverterSheet(true) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(bottom = 8.dp)
                                    )

                                    StandardKeypad(
                                        theme = theme,
                                        onInput = { viewModel.onInput(it, haptics) },
                                        onClear = { viewModel.onClear(haptics) },
                                        onBackspace = { viewModel.onBackspace(haptics) },
                                        onNegate = { viewModel.onNegate(haptics) },
                                        onEquals = { viewModel.onEquals(haptics) },
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            CalculatorMode.GST_CALCULATOR -> {
                                GstCalculatorView(
                                    theme = theme,
                                    amountInput = uiState.gstAmountInput,
                                    calculationType = uiState.gstCalculationType,
                                    selectedSlabId = uiState.gstSelectedSlabId,
                                    slabs = uiState.gstSlabs,
                                    currentResult = uiState.gstCurrentResult,
                                    grandTotalGross = uiState.gstGrandTotalGross,
                                    grandTotalGst = uiState.gstGrandTotalGst,
                                    calculationCount = uiState.gstCalculationCount,
                                    language = uiState.currentLanguage,
                                    onInputDigit = { viewModel.onGstInputDigit(it, haptics) },
                                    onEquals = { viewModel.onGstEquals(haptics) },
                                    onClear = { viewModel.onGstClear(haptics) },
                                    onBackspace = { viewModel.onGstBackspace(haptics) },
                                    onToggleType = { viewModel.onGstToggleType(haptics) },
                                    onSelectSlab = { viewModel.onGstSelectSlab(it, haptics) },
                                    onClearGrandTotal = { viewModel.onGstClearGrandTotal() },
                                    onUpdateSlabRate = { id, rate -> viewModel.onGstUpdateSlabRate(id, rate) },
                                    onApplyPreset = { viewModel.onGstApplyPreset(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
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
                                        displayConfig = uiState.displayConfig,
                                        historyCount = historyList.size,
                                        onToggleAngleMode = { viewModel.toggleAngleMode() },
                                        onOpenHistory = { viewModel.setShowHistorySheet(true) },
                                        onOpenDecimalConverter = { viewModel.setShowDecimalConverterSheet(true) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(bottom = 6.dp)
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

                            CalculatorMode.AGE_CALCULATOR -> {
                                AgeCalculatorView(
                                    theme = theme,
                                    birthDateTime = uiState.ageBirthDateTime,
                                    targetDateTime = uiState.ageTargetDateTime,
                                    currentPersonName = uiState.ageCurrentPersonName,
                                    notes = uiState.ageProfileNotes,
                                    savedProfiles = ageProfiles,
                                    selectedProfile = uiState.ageSelectedProfile,
                                    onUpdateBirthDateTime = { viewModel.onAgeUpdateBirthDateTime(it) },
                                    onUpdateTargetDateTime = { viewModel.onAgeUpdateTargetDateTime(it) },
                                    onPersonNameChange = { viewModel.setAgePersonName(it) },
                                    onNotesChange = { viewModel.setAgeProfileNotes(it) },
                                    onSaveProfile = { name, rel, notes -> viewModel.saveCurrentAgeProfile(name, rel, notes) },
                                    onLoadProfile = { viewModel.loadAgeProfile(it) },
                                    onDeleteProfile = { viewModel.deleteAgeProfile(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            CalculatorMode.BMI_CALCULATOR -> {
                                BmiCalculatorView(
                                    theme = theme,
                                    weightInput = uiState.bmiWeightInput,
                                    heightInput = uiState.bmiHeightInput,
                                    ageInput = uiState.bmiAgeInput,
                                    isMetric = uiState.bmiIsMetric,
                                    isMale = uiState.bmiIsMale,
                                    onWeightChange = { viewModel.onBmiWeightChange(it) },
                                    onHeightChange = { viewModel.onBmiHeightChange(it) },
                                    onAgeChange = { viewModel.onBmiAgeChange(it) },
                                    onToggleMetric = { viewModel.onBmiToggleMetric(it) },
                                    onToggleGender = { viewModel.onBmiToggleGender(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            CalculatorMode.CURRENCY_CONVERTER -> {
                                RealCurrencyConverterView(
                                    theme = theme,
                                    fromCode = uiState.currencyFromCode,
                                    toCode = uiState.currencyToCode,
                                    inputAmount = uiState.currencyInput,
                                    outputAmount = uiState.currencyOutput,
                                    isLoading = uiState.isCurrencyLoading,
                                    statusText = uiState.currencyStatusText,
                                    isOnline = uiState.currencyIsOnline,
                                    ratesMap = uiState.currencyRatesMap,
                                    onAmountChange = { viewModel.onCurrencyInputChange(it) },
                                    onFromChange = { viewModel.setCurrencyFrom(it) },
                                    onToChange = { viewModel.setCurrencyTo(it) },
                                    onSwap = { viewModel.swapCurrencies() },
                                    onQuickInrAmount = { viewModel.setQuickInrAmount(it) },
                                    onRefreshRates = { viewModel.fetchLiveCurrencyRates() },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            CalculatorMode.EMI_LOAN -> {
                                EmiCalculatorView(
                                    theme = theme,
                                    principalInput = uiState.emiPrincipalInput,
                                    interestRateInput = uiState.emiInterestRateInput,
                                    tenureYearsInput = uiState.emiTenureInput,
                                    isTenureInYears = uiState.emiIsTenureInYears,
                                    onPrincipalChange = { viewModel.onEmiPrincipalChange(it) },
                                    onRateChange = { viewModel.onEmiRateChange(it) },
                                    onTenureChange = { viewModel.onEmiTenureChange(it) },
                                    onToggleTenureUnit = { viewModel.onEmiToggleTenureUnit(it) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
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
                                        displayConfig = uiState.displayConfig,
                                        historyCount = historyList.size,
                                        onOpenHistory = { viewModel.setShowHistorySheet(true) },
                                        onOpenDecimalConverter = { viewModel.setShowDecimalConverterSheet(true) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(bottom = 6.dp)
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
                                        modifier = Modifier.padding(bottom = 4.dp)
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

                            CalculatorMode.ENGINEERING -> {
                                EngineeringCalculatorView(
                                    theme = theme,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Settings & Appearance Bottom Sheet
        if (uiState.showSettingsSheet) {
            SettingsSheet(
                activeTheme = theme,
                onSelectTheme = { viewModel.setTheme(it) },
                customAccentColor = uiState.customAccentColor,
                onSelectAccentColor = { viewModel.setCustomAccentColor(it) },
                customShapeType = uiState.customShapeType,
                onSelectShapeType = { viewModel.setCustomShapeType(it) },
                customDisplayFont = uiState.customDisplayFont,
                onSelectDisplayFont = { viewModel.setCustomDisplayFont(it) },
                displayConfig = uiState.displayConfig,
                currentLanguage = uiState.currentLanguage,
                onSelectLanguage = { viewModel.setAppLanguage(it) },
                onSelectDisplaySeparator = { viewModel.setDisplaySeparator(it) },
                onSelectDisplayPrecision = { viewModel.setDisplayPrecision(it) },
                onSelectDisplayScale = { viewModel.setDisplayScale(it) },
                onSelectDisplayNotation = { viewModel.setDisplayNotation(it) },
                onToggleLivePreview = { viewModel.toggleLivePreview() },
                onToggleStatusBadges = { viewModel.toggleStatusBadges() },
                onToggleScanlinesOverride = { viewModel.toggleScanlinesOverride() },
                onToggleCopyOnTap = { viewModel.toggleCopyOnTap() },
                onResetDisplaySettings = { viewModel.resetDisplaySettings() },
                isSoundEnabled = uiState.isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                isHapticsEnabled = uiState.isHapticsEnabled,
                onToggleHaptics = { viewModel.toggleHaptics() },
                onResetAppearance = { viewModel.resetAppearanceCustomizations() },
                onDismiss = { viewModel.setShowSettingsSheet(false) },
                sheetState = settingsSheetState
            )
        }

        // More Calculators & Tools Sheet
        if (uiState.showMoreModesSheet) {
            MoreModesSheet(
                activeMode = uiState.mode,
                theme = theme,
                onSelectMode = { viewModel.setMode(it) },
                onDismiss = { viewModel.setShowMoreModesSheet(false) },
                sheetState = moreModesSheetState
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

        // Decimal Conversion Modal Bottom Sheet
        if (uiState.showDecimalConverterSheet) {
            DecimalConverterSheet(
                targetValue = uiState.decimalConverterTarget,
                theme = theme,
                onUseValue = { selectedVal ->
                    viewModel.setShowDecimalConverterSheet(false)
                },
                onDismiss = { viewModel.setShowDecimalConverterSheet(false) },
                sheetState = decimalSheetState
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
