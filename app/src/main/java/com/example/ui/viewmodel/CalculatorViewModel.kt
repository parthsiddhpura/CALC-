package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryRepository
import com.example.domain.CalculatorEngine
import com.example.domain.ProgrammerEngine
import com.example.domain.SoundHapticHelper
import com.example.domain.WordSize
import com.example.model.AngleMode
import com.example.model.CalculationHistory
import com.example.model.CalculatorMode
import com.example.model.ConversionUnit
import com.example.model.NumberBase
import com.example.model.ThemeId
import com.example.model.ThemePalette
import com.example.model.UnitCategory
import com.example.model.UnitConverterData
import com.example.ui.theme.CalculatorThemes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.random.Random

data class CalculatorUiState(
    val mode: CalculatorMode = CalculatorMode.STANDARD,
    val currentThemeId: ThemeId = ThemeId.CYBERPUNK,
    val expression: String = "",
    val result: String = "0",
    val previewResult: String? = null,
    val angleMode: AngleMode = AngleMode.DEG,
    val isSecondFunction: Boolean = false,
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val lastEvaluated: Boolean = false,
    
    // Sound & Haptic
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    
    // Sheets & Dialogs
    val showThemeSheet: Boolean = false,
    val showHistorySheet: Boolean = false,
    val showModeSelector: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val editingNoteFor: CalculationHistory? = null,
    val historySearchQuery: String = "",
    val historyOnlyFavorites: Boolean = false,
    
    // Programmer Mode State
    val progValue: Long = 0L,
    val progInput: String = "0",
    val progBase: NumberBase = NumberBase.DEC,
    val progWordSize: WordSize = WordSize.QWORD,
    val progPendingOp: String? = null,
    val progStoredValue: Long? = null,
    
    // Unit Converter State
    val convCategory: UnitCategory = UnitCategory.LENGTH,
    val convFromUnit: ConversionUnit = UnitConverterData.categories[UnitCategory.LENGTH]!![0],
    val convToUnit: ConversionUnit = UnitConverterData.categories[UnitCategory.LENGTH]!![1],
    val convInput: String = "1",
    val convOutput: String = "0.001",
    
    // Tip Splitter State
    val tipBillInput: String = "50.00",
    val tipPercent: Float = 18f,
    val tipPeopleCount: Int = 2,
    val tipAmount: Double = 9.0,
    val tipTotal: Double = 59.0,
    val tipPerPerson: Double = 29.5
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    val soundHapticHelper: SoundHapticHelper = SoundHapticHelper(application)
    private val prefs: SharedPreferences = application.getSharedPreferences("chromacalc_prefs", Context.MODE_PRIVATE)

    private val _uiState: MutableStateFlow<CalculatorUiState>
    val uiState: StateFlow<CalculatorUiState>

    private val _searchQuery = MutableStateFlow("")
    private val _onlyFavorites = MutableStateFlow(false)

    val historyList: StateFlow<List<CalculationHistory>>

    init {
        val savedThemeName = prefs.getString("saved_theme_id", null)
        val initialTheme = if (savedThemeName != null) {
            try {
                ThemeId.valueOf(savedThemeName)
            } catch (e: Exception) {
                ThemeId.CYBERPUNK
            }
        } else {
            ThemeId.CYBERPUNK
        }
        val savedSound = prefs.getBoolean("saved_sound_enabled", true)
        val savedHaptics = prefs.getBoolean("saved_haptics_enabled", true)

        _uiState = MutableStateFlow(
            CalculatorUiState(
                currentThemeId = initialTheme,
                isSoundEnabled = savedSound,
                isHapticsEnabled = savedHaptics
            )
        )
        uiState = _uiState.asStateFlow()

        val db = AppDatabase.getDatabase(application)
        repository = HistoryRepository(db.historyDao())

        historyList = combine(
            repository.allHistory,
            _searchQuery,
            _onlyFavorites
        ) { all, query, favsOnly ->
            var list = all
            if (favsOnly) {
                list = list.filter { it.isFavorite }
            }
            if (query.isNotBlank()) {
                val q = query.lowercase(Locale.ROOT)
                list = list.filter {
                    it.expression.lowercase(Locale.ROOT).contains(q) ||
                    it.result.lowercase(Locale.ROOT).contains(q) ||
                    it.note.lowercase(Locale.ROOT).contains(q)
                }
            }
            list
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    val currentTheme: ThemePalette
        get() = CalculatorThemes.getThemeById(_uiState.value.currentThemeId)

    fun setMode(mode: CalculatorMode) {
        _uiState.update { it.copy(mode = mode, showModeSelector = false) }
    }

    fun setTheme(themeId: ThemeId) {
        prefs.edit().putString("saved_theme_id", themeId.name).commit()
        _uiState.update { it.copy(currentThemeId = themeId) }
    }

    fun cycleNextTheme() {
        val all = CalculatorThemes.allThemes
        val currentIndex = all.indexOfFirst { it.id == _uiState.value.currentThemeId }
        val nextIndex = if (currentIndex >= 0) (currentIndex + 1) % all.size else 0
        setTheme(all[nextIndex].id)
    }

    fun cyclePrevTheme() {
        val all = CalculatorThemes.allThemes
        val currentIndex = all.indexOfFirst { it.id == _uiState.value.currentThemeId }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else all.size - 1
        setTheme(all[prevIndex].id)
    }

    fun randomizeTheme() {
        val all = CalculatorThemes.allThemes
        val current = _uiState.value.currentThemeId
        val candidates = all.filter { it.id != current }
        if (candidates.isNotEmpty()) {
            val randomTheme = candidates[Random.nextInt(candidates.size)]
            setTheme(randomTheme.id)
        }
    }

    fun toggleSound() {
        _uiState.update {
            val nextVal = !it.isSoundEnabled
            prefs.edit().putBoolean("saved_sound_enabled", nextVal).apply()
            it.copy(isSoundEnabled = nextVal)
        }
    }

    fun toggleHaptics() {
        _uiState.update {
            val nextVal = !it.isHapticsEnabled
            prefs.edit().putBoolean("saved_haptics_enabled", nextVal).apply()
            it.copy(isHapticsEnabled = nextVal)
        }
    }

    fun toggleAngleMode() {
        _uiState.update {
            val newMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
            val preview = CalculatorEngine.evaluatePreview(it.expression, newMode)
            it.copy(angleMode = newMode, previewResult = preview)
        }
    }

    fun toggleSecondFunction() {
        _uiState.update { it.copy(isSecondFunction = !it.isSecondFunction) }
    }

    fun setShowThemeSheet(show: Boolean) {
        _uiState.update { it.copy(showThemeSheet = show) }
    }

    fun setShowHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun setShowModeSelector(show: Boolean) {
        _uiState.update { it.copy(showModeSelector = show) }
    }

    fun setShowSettingsSheet(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }

    fun setEditingNoteFor(history: CalculationHistory?) {
        _uiState.update { it.copy(editingNoteFor = history) }
    }

    fun setHistorySearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(historySearchQuery = query) }
    }

    fun setHistoryOnlyFavorites(onlyFavs: Boolean) {
        _onlyFavorites.value = onlyFavs
        _uiState.update { it.copy(historyOnlyFavorites = onlyFavs) }
    }

    // --- Standard & Scientific Calculation Inputs ---

    fun onInput(char: String, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            var newExpr = state.expression
            var newResult = state.result
            val wasEvaluated = state.lastEvaluated

            if (wasEvaluated) {
                // If user enters operator after '=' continue with previous result
                if ("+−×÷^%".contains(char)) {
                    newExpr = if (newResult != "Error") newResult + char else ""
                } else {
                    newExpr = char
                }
            } else {
                newExpr += char
            }

            val preview = CalculatorEngine.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                lastEvaluated = false
            )
        }
    }

    fun onFunction(func: String, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            var newExpr = state.expression
            if (state.lastEvaluated) {
                newExpr = if (state.result != "Error") "$func(${state.result})" else "$func("
            } else {
                newExpr += "$func("
            }
            val preview = CalculatorEngine.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                lastEvaluated = false
            )
        }
    }

    fun onConstant(constant: String, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            var newExpr = if (state.lastEvaluated) constant else state.expression + constant
            val preview = CalculatorEngine.evaluatePreview(newExpr, state.angleMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                lastEvaluated = false
            )
        }
    }

    fun onNegate(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            val expr = state.expression
            val newExpr = if (expr.startsWith("-(")) {
                expr.removePrefix("-(").removeSuffix(")")
            } else if (expr.startsWith("-")) {
                expr.removePrefix("-")
            } else if (expr.isNotEmpty()) {
                "-($expr)"
            } else {
                "-"
            }
            val preview = CalculatorEngine.evaluatePreview(newExpr, state.angleMode)
            state.copy(expression = newExpr, previewResult = preview, lastEvaluated = false)
        }
    }

    fun onBackspace(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isClear = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                val newExpr = state.expression.dropLast(1)
                val preview = CalculatorEngine.evaluatePreview(newExpr, state.angleMode)
                state.copy(expression = newExpr, previewResult = preview, lastEvaluated = false)
            } else {
                state.copy(result = "0", previewResult = null, lastEvaluated = false)
            }
        }
    }

    fun onClear(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isClear = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled, isHeavy = true)

        _uiState.update {
            it.copy(
                expression = "",
                result = "0",
                previewResult = null,
                lastEvaluated = false
            )
        }
    }

    fun onEquals(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isEquals = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled, isHeavy = true)

        val state = _uiState.value
        if (state.expression.isBlank()) return

        val evaluated = CalculatorEngine.evaluate(state.expression, state.angleMode)
        if (evaluated != "Error" && evaluated.isNotBlank()) {
            viewModelScope.launch {
                repository.insert(
                    expression = state.expression,
                    result = evaluated,
                    mode = state.mode.name
                )
            }
        }

        _uiState.update {
            it.copy(
                result = evaluated,
                previewResult = null,
                lastEvaluated = true
            )
        }
    }

    // --- Memory Operations ---

    fun onMemoryAdd(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)
        val currentVal = _uiState.value.result.replace(",", "").toDoubleOrNull() ?: 0.0
        val newMem = _uiState.value.memoryValue + currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    fun onMemorySubtract(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)
        val currentVal = _uiState.value.result.replace(",", "").toDoubleOrNull() ?: 0.0
        val newMem = _uiState.value.memoryValue - currentVal
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    fun onMemoryRecall(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)
        if (!_uiState.value.hasMemory) return
        val memStr = CalculatorEngine.formatWithoutCommas(_uiState.value.memoryValue)
        onInput(memStr, haptics)
    }

    fun onMemoryClear(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isClear = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)
        _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
    }

    // --- History actions ---

    fun useHistoryItem(history: CalculationHistory, reuseAsExpression: Boolean = false) {
        _uiState.update {
            if (reuseAsExpression) {
                it.copy(
                    expression = history.expression,
                    result = history.result,
                    previewResult = null,
                    lastEvaluated = true,
                    showHistorySheet = false
                )
            } else {
                it.copy(
                    expression = it.expression + history.result.replace(",", ""),
                    previewResult = CalculatorEngine.evaluatePreview(it.expression + history.result.replace(",", ""), it.angleMode),
                    lastEvaluated = false,
                    showHistorySheet = false
                )
            }
        }
    }

    fun toggleFavorite(history: CalculationHistory) {
        viewModelScope.launch {
            repository.toggleFavorite(history)
        }
    }

    fun saveHistoryNote(history: CalculationHistory, note: String) {
        viewModelScope.launch {
            repository.updateNote(history, note)
            setEditingNoteFor(null)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // --- Programmer Mode Inputs ---

    fun setProgBase(base: NumberBase) {
        _uiState.update {
            val formatted = ProgrammerEngine.formatInBase(it.progValue, base, it.progWordSize)
            it.copy(progBase = base, progInput = formatted)
        }
    }

    fun setProgWordSize(wordSize: WordSize) {
        _uiState.update {
            val masked = ProgrammerEngine.applyWordSizeMask(it.progValue, wordSize)
            val formatted = ProgrammerEngine.formatInBase(masked, it.progBase, wordSize)
            it.copy(progWordSize = wordSize, progValue = masked, progInput = formatted)
        }
    }

    fun onProgDigit(digit: String, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            val newInput = if (state.progInput == "0" || state.lastEvaluated) digit else state.progInput + digit
            val newValue = ProgrammerEngine.parseValue(newInput, state.progBase, state.progWordSize)
            state.copy(
                progInput = newInput,
                progValue = newValue,
                lastEvaluated = false
            )
        }
    }

    fun onProgBitwiseOp(op: String, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            if (op == "NOT") {
                val notVal = ProgrammerEngine.performNot(state.progValue, state.progWordSize)
                val formatted = ProgrammerEngine.formatInBase(notVal, state.progBase, state.progWordSize)
                state.copy(progValue = notVal, progInput = formatted, lastEvaluated = true)
            } else {
                state.copy(
                    progPendingOp = op,
                    progStoredValue = state.progValue,
                    progInput = "0"
                )
            }
        }
    }

    fun onProgEquals(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isEquals = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled, isHeavy = true)

        _uiState.update { state ->
            val op = state.progPendingOp
            val first = state.progStoredValue
            if (op != null && first != null) {
                val result = ProgrammerEngine.performBitwise(first, state.progValue, op, state.progWordSize)
                val formatted = ProgrammerEngine.formatInBase(result, state.progBase, state.progWordSize)
                
                viewModelScope.launch {
                    repository.insert(
                        expression = "$first $op ${state.progValue}",
                        result = "$result ($formatted)",
                        mode = CalculatorMode.PROGRAMMER.name
                    )
                }

                state.copy(
                    progValue = result,
                    progInput = formatted,
                    progPendingOp = null,
                    progStoredValue = null,
                    lastEvaluated = true
                )
            } else {
                state
            }
        }
    }

    fun onProgBitToggle(bitIndex: Int, haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            val toggled = ProgrammerEngine.toggleBit(state.progValue, bitIndex, state.progWordSize)
            val formatted = ProgrammerEngine.formatInBase(toggled, state.progBase, state.progWordSize)
            state.copy(progValue = toggled, progInput = formatted)
        }
    }

    fun onProgClear(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isClear = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled, isHeavy = true)

        _uiState.update {
            it.copy(
                progValue = 0L,
                progInput = "0",
                progPendingOp = null,
                progStoredValue = null
            )
        }
    }

    fun onProgBackspace(haptics: HapticFeedback? = null) {
        soundHapticHelper.playClick(_uiState.value.isSoundEnabled, isClear = true)
        soundHapticHelper.triggerHaptic(haptics, _uiState.value.isHapticsEnabled)

        _uiState.update { state ->
            if (state.progInput.length > 1) {
                val drop = state.progInput.dropLast(1)
                val v = ProgrammerEngine.parseValue(drop, state.progBase, state.progWordSize)
                state.copy(progInput = drop, progValue = v)
            } else {
                state.copy(progInput = "0", progValue = 0L)
            }
        }
    }

    // --- Unit Converter Inputs ---

    fun setUnitCategory(category: UnitCategory) {
        val units = UnitConverterData.categories[category] ?: return
        val from = units[0]
        val to = if (units.size > 1) units[1] else units[0]
        _uiState.update {
            val convResult = calculateUnitConversion(it.convInput, category, from, to)
            it.copy(
                convCategory = category,
                convFromUnit = from,
                convToUnit = to,
                convOutput = convResult
            )
        }
    }

    fun setFromUnit(unit: ConversionUnit) {
        _uiState.update {
            val convResult = calculateUnitConversion(it.convInput, it.convCategory, unit, it.convToUnit)
            it.copy(convFromUnit = unit, convOutput = convResult)
        }
    }

    fun setToUnit(unit: ConversionUnit) {
        _uiState.update {
            val convResult = calculateUnitConversion(it.convInput, it.convCategory, it.convFromUnit, unit)
            it.copy(convToUnit = unit, convOutput = convResult)
        }
    }

    fun swapUnits() {
        _uiState.update {
            val from = it.convFromUnit
            val to = it.convToUnit
            val convResult = calculateUnitConversion(it.convInput, it.convCategory, to, from)
            it.copy(convFromUnit = to, convToUnit = from, convOutput = convResult)
        }
    }

    fun onConverterInput(text: String) {
        _uiState.update {
            val convResult = calculateUnitConversion(text, it.convCategory, it.convFromUnit, it.convToUnit)
            it.copy(convInput = text, convOutput = convResult)
        }
    }

    private fun calculateUnitConversion(
        input: String,
        category: UnitCategory,
        from: ConversionUnit,
        to: ConversionUnit
    ): String {
        val v = input.toDoubleOrNull() ?: return "0"
        val res = UnitConverterData.convert(category, v, from, to)
        val df = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US))
        return df.format(res)
    }

    // --- Tip & Split Inputs ---

    fun onTipBillChange(bill: String) {
        _uiState.update {
            val (tip, total, perPerson) = computeTipValues(bill, it.tipPercent, it.tipPeopleCount)
            it.copy(
                tipBillInput = bill,
                tipAmount = tip,
                tipTotal = total,
                tipPerPerson = perPerson
            )
        }
    }

    fun onTipPercentChange(percent: Float) {
        _uiState.update {
            val (tip, total, perPerson) = computeTipValues(it.tipBillInput, percent, it.tipPeopleCount)
            it.copy(
                tipPercent = percent,
                tipAmount = tip,
                tipTotal = total,
                tipPerPerson = perPerson
            )
        }
    }

    fun onTipPeopleChange(count: Int) {
        val safeCount = count.coerceAtLeast(1)
        _uiState.update {
            val (tip, total, perPerson) = computeTipValues(it.tipBillInput, it.tipPercent, safeCount)
            it.copy(
                tipPeopleCount = safeCount,
                tipAmount = tip,
                tipTotal = total,
                tipPerPerson = perPerson
            )
        }
    }

    private fun computeTipValues(billStr: String, tipPct: Float, people: Int): Triple<Double, Double, Double> {
        val bill = billStr.toDoubleOrNull() ?: 0.0
        val tip = bill * (tipPct / 100.0)
        val total = bill + tip
        val perPerson = if (people > 0) total / people else total
        return Triple(tip, total, perPerson)
    }

    override fun onCleared() {
        super.onCleared()
        soundHapticHelper.release()
    }
}
