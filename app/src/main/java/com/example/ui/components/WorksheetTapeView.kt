package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.WorksheetTapeEngine
import com.example.model.ThemePalette
import com.example.model.WorksheetDocument
import com.example.model.WorksheetLine
import com.example.model.WorksheetLineType
import com.example.model.WorksheetTemplate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksheetTapeView(
    theme: ThemePalette,
    documents: List<WorksheetDocument>,
    activeDocument: WorksheetDocument,
    onSaveDocument: (WorksheetDocument) -> Unit,
    onSelectDocument: (WorksheetDocument) -> Unit,
    onDeleteDocument: (String) -> Unit,
    onNewDocument: () -> Unit,
    onApplyTemplate: (WorksheetTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Undo / Redo history stack for current document
    var undoStack by remember(activeDocument.id) { mutableStateOf<List<List<WorksheetLine>>>(emptyList()) }
    var redoStack by remember(activeDocument.id) { mutableStateOf<List<List<WorksheetLine>>>(emptyList()) }

    // Resizable split layout: ratio of tape height (0.35f to 0.70f)
    var tapeWeight by remember { mutableFloatStateOf(0.48f) }

    // Selected line for editing or keypad input
    var selectedLineIndex by remember(activeDocument.id) {
        mutableIntStateOf(activeDocument.lines.size - 1)
    }

    // Input buffer for current figure being entered
    var currentInputBuffer by remember { mutableStateOf("") }
    var currentOperator by remember { mutableStateOf("+") }

    // Dialog & Sheet States
    var showDocumentsDrawer by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showTemplateSheet by remember { mutableStateOf(false) }
    var showEditLineDialog by remember { mutableStateOf(false) }
    var showCustomKeyDialog by remember { mutableStateOf(false) }
    var showAddLineDialog by remember { mutableStateOf(false) }
    var lineBeingEdited by remember { mutableStateOf<WorksheetLine?>(null) }
    var lineEditIndex by remember { mutableIntStateOf(-1) }

    // Custom Key preset (e.g. +18% GST or -10% Disc)
    var customKeyRate by remember { mutableStateOf("18") }
    var customKeyType by remember { mutableStateOf("GST") } // "GST" (+) or "DISC" (-)

    // Sheet controller
    val docSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val templateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tapeListState = rememberLazyListState()

    // High-contrast colors
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val tapeBg = if (isLightCanvas) Color(0xFFFCFBF9) else Color(0xFF1E161C)
    val tapeBorder = if (isLightCanvas) Color(0xFFE5DDD5) else Color(0xFF382330)
    val primaryTextColor = if (isLightCanvas) Color(0xFF2C2428) else Color(0xFFF9EEF3)
    val secondaryTextColor = if (isLightCanvas) Color(0xFF7A6B72) else Color(0xFFBFAAB3)
    val discountColor = Color(0xFFE53935)
    val profitColor = Color(0xFF2E7D32)
    val subtotalColor = theme.accentColor
    val activeSelectionBg = theme.accentColor.copy(alpha = 0.16f)

    // Helper to push document update with undo
    fun updateLines(newLines: List<WorksheetLine>) {
        undoStack = undoStack + listOf(activeDocument.lines)
        redoStack = emptyList()
        val recalculated = WorksheetTapeEngine.recalculate(newLines)
        val grandTotal = recalculated.lastOrNull()?.runningTotal ?: 0.0
        onSaveDocument(activeDocument.copy(lines = recalculated, grandTotal = grandTotal))
    }

    // Scroll to bottom when lines change
    LaunchedEffect(activeDocument.lines.size) {
        if (activeDocument.lines.isNotEmpty()) {
            tapeListState.animateScrollToItem(activeDocument.lines.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
    ) {
        // --- 1. TOP HEADER TOOLBAR ---
        Surface(
            color = theme.surfaceColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showDocumentsDrawer = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Documents",
                            tint = primaryTextColor
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = activeDocument.title,
                                color = primaryTextColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable {
                                    lineBeingEdited = null
                                    lineEditIndex = -2 // code for editing doc title
                                    showEditLineDialog = true
                                }
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename",
                                tint = secondaryTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${activeDocument.lines.size} steps • Total: ${WorksheetTapeEngine.formatNumber(activeDocument.grandTotal)}",
                            color = secondaryTextColor,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Templates
                    IconButton(
                        onClick = { showTemplateSheet = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Templates",
                            tint = primaryTextColor
                        )
                    }

                    // Share
                    IconButton(
                        onClick = { showShareSheet = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = theme.accentColor
                        )
                    }
                }
            }
        }

        // --- 2. PAPER TAPE CANVAS (TOP RESIZABLE SECTION) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(tapeWeight)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Surface(
                color = tapeBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tapeBorder),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Serrated paper tape header accent
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(theme.accentColor, theme.secondaryAccent, theme.accentColor)
                                )
                            )
                    )

                    // Tape Lines List
                    LazyColumn(
                        state = tapeListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(activeDocument.lines) { index, line ->
                            val isSelected = index == selectedLineIndex

                            TapeLineRow(
                                line = line,
                                isSelected = isSelected,
                                primaryColor = primaryTextColor,
                                secondaryColor = secondaryTextColor,
                                subtotalColor = subtotalColor,
                                discountColor = discountColor,
                                profitColor = profitColor,
                                selectionBg = activeSelectionBg,
                                onSelect = {
                                    selectedLineIndex = index
                                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                },
                                onEdit = {
                                    lineBeingEdited = line
                                    lineEditIndex = index
                                    showEditLineDialog = true
                                },
                                onDelete = {
                                    val updated = activeDocument.lines.toMutableList()
                                    if (index in updated.indices) {
                                        updated.removeAt(index)
                                        updateLines(updated)
                                        if (selectedLineIndex >= updated.size) {
                                            selectedLineIndex = updated.size - 1
                                        }
                                    }
                                }
                            )
                        }

                        // Add line bottom button
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showAddLineDialog = true }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Step",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Step / Variable / Subtotal",
                                    color = theme.accentColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. DRAGGABLE SPLIT RESIZER BAR ("FITS YOUR HAND") ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        // 1000px roughly screen height; convert drag delta to weight
                        val deltaWeight = dragAmount / 1600f
                        tapeWeight = (tapeWeight + deltaWeight).coerceIn(0.25f, 0.72f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(theme.surfaceColor)
                    .border(1.dp, theme.accentColor.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag to resize",
                    tint = primaryTextColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Drag to fit hand",
                    color = secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                // Quick preset toggle pills
                listOf(0.35f to "35%", 0.48f to "50%", 0.65f to "65%").forEach { (targetWeight, label) ->
                    val isCurrent = kotlin.math.abs(tapeWeight - targetWeight) < 0.08f
                    Surface(
                        shape = CircleShape,
                        color = if (isCurrent) theme.accentColor else Color.Transparent,
                        modifier = Modifier.clickable {
                            tapeWeight = targetWeight
                            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        }
                    ) {
                        Text(
                            text = label,
                            color = if (isCurrent) Color.White else secondaryTextColor,
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // --- 4. TAPE QUICK STATUS & CONTROLS TOOLBAR ---
        Surface(
            color = theme.surfaceColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Undo, Redo, Clear
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (undoStack.isNotEmpty()) {
                                val prev = undoStack.last()
                                undoStack = undoStack.dropLast(1)
                                redoStack = redoStack + listOf(activeDocument.lines)
                                val recalculated = WorksheetTapeEngine.recalculate(prev)
                                onSaveDocument(activeDocument.copy(lines = recalculated, grandTotal = recalculated.lastOrNull()?.runningTotal ?: 0.0))
                            }
                        },
                        enabled = undoStack.isNotEmpty(),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (undoStack.isNotEmpty()) primaryTextColor else secondaryTextColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                val next = redoStack.last()
                                redoStack = redoStack.dropLast(1)
                                undoStack = undoStack + listOf(activeDocument.lines)
                                val recalculated = WorksheetTapeEngine.recalculate(next)
                                onSaveDocument(activeDocument.copy(lines = recalculated, grandTotal = recalculated.lastOrNull()?.runningTotal ?: 0.0))
                            }
                        },
                        enabled = redoStack.isNotEmpty(),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (redoStack.isNotEmpty()) primaryTextColor else secondaryTextColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Customize Key Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = theme.accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { showCustomKeyDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Custom Key",
                                tint = theme.accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${if (customKeyType == "GST") "+" else "-"}$customKeyRate%",
                                color = theme.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Right: Grand Total Display & Copy
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.cardBackground)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            val formatted = WorksheetTapeEngine.formatNumber(activeDocument.grandTotal)
                            clipboardManager.setText(AnnotatedString(formatted))
                            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "TOTAL",
                        color = secondaryTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = WorksheetTapeEngine.formatNumber(activeDocument.grandTotal),
                        color = primaryTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = theme.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // --- 5. WORKSHEET PAPER TAPE KEYPAD (BOTTOM SECTION) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f - tapeWeight)
                .background(theme.surfaceColor)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            WorksheetKeypad(
                theme = theme,
                customKeyLabel = "${if (customKeyType == "GST") "+" else "-"}$customKeyRate%",
                onDigit = { digit ->
                    currentInputBuffer += digit
                    // If a calculation line is selected, update it reactively
                    if (selectedLineIndex in activeDocument.lines.indices) {
                        val current = activeDocument.lines[selectedLineIndex]
                        if (current.lineType == WorksheetLineType.CALCULATION || current.lineType == WorksheetLineType.PERCENTAGE) {
                            val updated = activeDocument.lines.toMutableList()
                            val newRaw = if (current.rawValue == "0") digit else current.rawValue + digit
                            updated[selectedLineIndex] = current.copy(rawValue = newRaw)
                            updateLines(updated)
                        }
                    }
                },
                onOperator = { op ->
                    currentOperator = op
                    // Append new calculation step
                    val newLine = WorksheetLine(
                        operator = op,
                        rawValue = if (currentInputBuffer.isNotBlank()) currentInputBuffer else "0",
                        note = ""
                    )
                    currentInputBuffer = ""
                    val updated = activeDocument.lines + listOf(newLine)
                    selectedLineIndex = updated.size - 1
                    updateLines(updated)
                },
                onCustomKey = {
                    // Custom key applies percentage line e.g. +18% or -10%
                    val op = if (customKeyType == "GST") "+" else "-"
                    val newLine = WorksheetLine(
                        lineType = WorksheetLineType.PERCENTAGE,
                        operator = op,
                        rawValue = customKeyRate,
                        note = if (customKeyType == "GST") "GST / VAT" else "Discount"
                    )
                    val updated = activeDocument.lines + listOf(newLine)
                    selectedLineIndex = updated.size - 1
                    updateLines(updated)
                },
                onSubtotal = {
                    // Insert Subtotal line
                    val newLine = WorksheetLine(
                        lineType = WorksheetLineType.SUB_TOTAL,
                        operator = "+",
                        rawValue = "",
                        note = "Subtotal"
                    )
                    val updated = activeDocument.lines + listOf(newLine)
                    selectedLineIndex = updated.size - 1
                    updateLines(updated)
                },
                onGrandTotal = {
                    // Insert Grand Total line
                    val newLine = WorksheetLine(
                        lineType = WorksheetLineType.GRAND_TOTAL,
                        operator = "+",
                        rawValue = "",
                        note = "Grand Total"
                    )
                    val updated = activeDocument.lines + listOf(newLine)
                    selectedLineIndex = updated.size - 1
                    updateLines(updated)
                },
                onBackspace = {
                    if (selectedLineIndex in activeDocument.lines.indices) {
                        val current = activeDocument.lines[selectedLineIndex]
                        if (current.rawValue.isNotEmpty()) {
                            val trimmed = current.rawValue.dropLast(1).ifEmpty { "0" }
                            val updated = activeDocument.lines.toMutableList()
                            updated[selectedLineIndex] = current.copy(rawValue = trimmed)
                            updateLines(updated)
                        }
                    }
                },
                onClear = {
                    // Clear current line or reset
                    currentInputBuffer = ""
                    if (selectedLineIndex in activeDocument.lines.indices) {
                        val updated = activeDocument.lines.toMutableList()
                        updated[selectedLineIndex] = activeDocument.lines[selectedLineIndex].copy(rawValue = "0")
                        updateLines(updated)
                    }
                },
                onAddNote = {
                    if (selectedLineIndex in activeDocument.lines.indices) {
                        lineBeingEdited = activeDocument.lines[selectedLineIndex]
                        lineEditIndex = selectedLineIndex
                        showEditLineDialog = true
                    }
                }
            )
        }
    }

    // --- BOTTOM SHEETS & DIALOGS ---

    // 1. Documents Drawer Sheet
    if (showDocumentsDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showDocumentsDrawer = false },
            sheetState = docSheetState,
            containerColor = theme.surfaceColor
        ) {
            DocumentsDrawerContent(
                documents = documents,
                activeId = activeDocument.id,
                theme = theme,
                onSelect = { doc ->
                    onSelectDocument(doc)
                    showDocumentsDrawer = false
                },
                onNew = {
                    onNewDocument()
                    showDocumentsDrawer = false
                },
                onDelete = { id ->
                    onDeleteDocument(id)
                },
                onDismiss = { showDocumentsDrawer = false }
            )
        }
    }

    // 2. Templates Sheet
    if (showTemplateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTemplateSheet = false },
            sheetState = templateSheetState,
            containerColor = theme.surfaceColor
        ) {
            TemplatesSheetContent(
                theme = theme,
                onSelect = { template ->
                    onApplyTemplate(template)
                    showTemplateSheet = false
                },
                onDismiss = { showTemplateSheet = false }
            )
        }
    }

    // 3. Share & Export Sheet
    if (showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
            sheetState = shareSheetState,
            containerColor = theme.surfaceColor
        ) {
            ShareSheetContent(
                document = activeDocument,
                theme = theme,
                onDismiss = { showShareSheet = false }
            )
        }
    }

    // 4. Edit Line / Note Dialog
    if (showEditLineDialog) {
        EditLineDialog(
            isDocumentTitle = lineEditIndex == -2,
            currentDocTitle = activeDocument.title,
            line = lineBeingEdited,
            theme = theme,
            onSaveTitle = { newTitle ->
                onSaveDocument(activeDocument.copy(title = newTitle))
                showEditLineDialog = false
            },
            onSaveLine = { updatedLine ->
                if (lineEditIndex in activeDocument.lines.indices) {
                    val updated = activeDocument.lines.toMutableList()
                    updated[lineEditIndex] = updatedLine
                    updateLines(updated)
                }
                showEditLineDialog = false
            },
            onDismiss = { showEditLineDialog = false }
        )
    }

    // 5. Add Line Dialog
    if (showAddLineDialog) {
        AddLineDialog(
            theme = theme,
            onAdd = { newLine ->
                val updated = activeDocument.lines + listOf(newLine)
                selectedLineIndex = updated.size - 1
                updateLines(updated)
                showAddLineDialog = false
            },
            onDismiss = { showAddLineDialog = false }
        )
    }

    // 6. Custom Key Config Dialog
    if (showCustomKeyDialog) {
        CustomKeyDialog(
            currentRate = customKeyRate,
            currentType = customKeyType,
            theme = theme,
            onSave = { rate, type ->
                customKeyRate = rate
                customKeyType = type
                showCustomKeyDialog = false
            },
            onDismiss = { showCustomKeyDialog = false }
        )
    }
}

/**
 * Individual Row in the Paper Tape
 */
@Composable
fun TapeLineRow(
    line: WorksheetLine,
    isSelected: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    subtotalColor: Color,
    discountColor: Color,
    profitColor: Color,
    selectionBg: Color,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = if (isSelected) selectionBg else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        when (line.lineType) {
            WorksheetLineType.COMMENT_HEADER -> {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "# ${line.note.ifBlank { line.rawValue }}",
                        color = primaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            WorksheetLineType.VARIABLE_SET -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${line.variableName ?: "Variable"} = ${WorksheetTapeEngine.formatNumber(line.evaluatedNumber)}",
                        color = subtotalColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = secondaryColor, modifier = Modifier.size(12.dp))
                    }
                }
            }

            WorksheetLineType.SUB_TOTAL -> {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    HorizontalDivider(color = primaryColor.copy(alpha = 0.4f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = line.operator, color = primaryColor, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text(text = WorksheetTapeEngine.formatNumber(line.evaluatedNumber), color = primaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            if (!line.variableName.isNullOrBlank()) {
                                Text(text = "= ${line.variableName}", color = subtotalColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            if (line.note.isNotBlank()) {
                                Text(text = line.note, color = secondaryColor, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            WorksheetLineType.GRAND_TOTAL -> {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    HorizontalDivider(color = primaryColor, thickness = 2.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider(color = primaryColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = line.operator, color = primaryColor, fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text(text = WorksheetTapeEngine.formatNumber(line.evaluatedNumber), color = subtotalColor, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            Text(text = line.note.ifBlank { "TOTAL" }, color = primaryColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            WorksheetLineType.PERCENTAGE -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = line.operator, color = if (line.operator == "-") discountColor else profitColor, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${WorksheetTapeEngine.formatNumber(line.evaluatedNumber)}%",
                            color = if (line.operator == "-") discountColor else profitColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        line.percentageDelta?.let { delta ->
                            Text(
                                text = "| ${if (delta >= 0) "+" else ""}${WorksheetTapeEngine.formatNumber(delta)}",
                                color = if (delta < 0) discountColor else profitColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (line.note.isNotBlank()) {
                            Text(text = line.note, color = secondaryColor, fontSize = 12.sp)
                        }
                    }

                    if (isSelected) {
                        Row {
                            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = secondaryColor, modifier = Modifier.size(12.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = discountColor, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            WorksheetLineType.CALCULATION -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = line.operator,
                            color = primaryColor,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(16.dp)
                        )
                        val numDisplay = if (line.rawValue.matches(Regex("[a-zA-Z_]+"))) line.rawValue else WorksheetTapeEngine.formatNumber(line.evaluatedNumber)
                        Text(
                            text = numDisplay,
                            color = if (line.rawValue.matches(Regex("[a-zA-Z_]+"))) subtotalColor else primaryColor,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                        if (!line.variableName.isNullOrBlank()) {
                            Text(text = "= ${line.variableName}", color = subtotalColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (line.note.isNotBlank()) {
                            Text(text = line.note, color = secondaryColor, fontSize = 12.sp)
                        }
                    }

                    if (isSelected) {
                        Row {
                            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = secondaryColor, modifier = Modifier.size(12.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = discountColor, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * CalcTape Responsive Keypad
 */
@Composable
fun WorksheetKeypad(
    theme: ThemePalette,
    customKeyLabel: String,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onCustomKey: () -> Unit,
    onSubtotal: () -> Unit,
    onGrandTotal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onAddNote: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val numBtnBg = if (isLightCanvas) Color(0xFFFFFFFF) else theme.numberButtonBg
    val numBtnText = if (isLightCanvas) Color(0xFF1E1E1E) else theme.numberButtonText
    val opBtnBg = if (isLightCanvas) Color(0xFFEFF3F8) else theme.operatorButtonBg
    val opBtnText = if (isLightCanvas) Color(0xFF0F172A) else theme.operatorButtonText
    val fnBtnBg = if (isLightCanvas) Color(0xFFFDE8ED) else theme.functionButtonBg
    val fnBtnText = if (isLightCanvas) Color(0xFF3B1A23) else theme.functionButtonText

    val keys = listOf(
        listOf(
            KeyItem("AC", fnBtnBg, fnBtnText, isAction = true) { onClear() },
            KeyItem("⌫", fnBtnBg, fnBtnText, isAction = true) { onBackspace() },
            KeyItem("Note", fnBtnBg, fnBtnText, isAction = true) { onAddNote() },
            KeyItem(customKeyLabel, theme.accentColor.copy(alpha = 0.25f), theme.accentColor, isAction = true) { onCustomKey() }
        ),
        listOf(
            KeyItem("7", numBtnBg, numBtnText) { onDigit("7") },
            KeyItem("8", numBtnBg, numBtnText) { onDigit("8") },
            KeyItem("9", numBtnBg, numBtnText) { onDigit("9") },
            KeyItem("÷", opBtnBg, opBtnText) { onOperator("/") }
        ),
        listOf(
            KeyItem("4", numBtnBg, numBtnText) { onDigit("4") },
            KeyItem("5", numBtnBg, numBtnText) { onDigit("5") },
            KeyItem("6", numBtnBg, numBtnText) { onDigit("6") },
            KeyItem("×", opBtnBg, opBtnText) { onOperator("*") }
        ),
        listOf(
            KeyItem("1", numBtnBg, numBtnText) { onDigit("1") },
            KeyItem("2", numBtnBg, numBtnText) { onDigit("2") },
            KeyItem("3", numBtnBg, numBtnText) { onDigit("3") },
            KeyItem("−", opBtnBg, opBtnText) { onOperator("-") }
        ),
        listOf(
            KeyItem("0", numBtnBg, numBtnText) { onDigit("0") },
            KeyItem(".", numBtnBg, numBtnText) { onDigit(".") },
            KeyItem("=", theme.accentColor, if (theme.accentColor.luminance() > 0.6f) Color.Black else Color.White, isAction = true) { onSubtotal() },
            KeyItem("+", opBtnBg, opBtnText) { onOperator("+") }
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { rowKeys ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowKeys.forEach { keyItem ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = keyItem.bg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                keyItem.onClick()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = keyItem.label,
                                color = keyItem.text,
                                fontSize = if (keyItem.label.length > 2) 13.sp else 19.sp,
                                fontWeight = if (keyItem.isAction) FontWeight.Bold else FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class KeyItem(
    val label: String,
    val bg: Color,
    val text: Color,
    val isAction: Boolean = false,
    val onClick: () -> Unit
)

/**
 * Documents Drawer / Bottom Sheet Content
 */
@Composable
fun DocumentsDrawerContent(
    documents: List<WorksheetDocument>,
    activeId: String,
    theme: ThemePalette,
    onSelect: (WorksheetDocument) -> Unit,
    onNew: () -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val subtextColor = if (isLightCanvas) Color(0xFF64748B) else Color(0xFF94A3B8)
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = theme.accentColor)
                Text(text = "Worksheet Documents (${documents.size})", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onNew) {
                Icon(imageVector = Icons.Default.PostAdd, contentDescription = "New", tint = theme.accentColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(documents.size) { idx ->
                val doc = documents[idx]
                val isActive = doc.id == activeId

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isActive) theme.accentColor.copy(alpha = 0.15f) else theme.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isActive) 1.5.dp else 1.dp,
                        color = if (isActive) theme.accentColor else Color.Gray.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(doc) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = if (isActive) theme.accentColor else subtextColor
                            )
                            Column {
                                Text(
                                    text = doc.title,
                                    color = textColor,
                                    fontSize = 15.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold
                                )
                                Text(
                                    text = "${doc.lines.size} steps • ${dateFormat.format(Date(doc.updatedAt))}",
                                    color = subtextColor,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = WorksheetTapeEngine.formatNumber(doc.grandTotal),
                                color = if (isActive) theme.accentColor else textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            if (documents.size > 1) {
                                IconButton(onClick = { onDelete(doc.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNew,
            colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Create New Worksheet", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Pre-made Professional Templates Sheet
 */
@Composable
fun TemplatesSheetContent(
    theme: ThemePalette,
    onSelect: (WorksheetTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    val templates = remember { WorksheetTapeEngine.getDefaultTemplates() }
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val subtextColor = if (isLightCanvas) Color(0xFF64748B) else Color(0xFF94A3B8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = theme.accentColor)
                Text(text = "Pre-made Templates", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = textColor)
            }
        }

        Text(
            text = "Tap a template to instantly load a complete calculation worksheet:",
            color = subtextColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(templates.size) { idx ->
                val template = templates[idx]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(template) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = template.title, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Surface(shape = CircleShape, color = theme.accentColor.copy(alpha = 0.15f)) {
                                Text(
                                    text = "${template.lines.size} steps",
                                    color = theme.accentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = template.description, color = subtextColor, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Share & Export Bottom Sheet
 */
@Composable
fun ShareSheetContent(
    document: WorksheetDocument,
    theme: ThemePalette,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptics = LocalHapticFeedback.current

    val plainText = remember(document) { WorksheetTapeEngine.exportToPlainText(document) }
    val markdownText = remember(document) { WorksheetTapeEngine.exportToMarkdown(document) }
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val subtextColor = if (isLightCanvas) Color(0xFF64748B) else Color(0xFF94A3B8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = theme.accentColor)
                Text(text = "Share & Export Worksheet", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = textColor)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Receipt Preview Box
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isLightCanvas) Color(0xFFF8FAFC) else Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                item {
                    Text(
                        text = plainText,
                        color = textColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, plainText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share ${document.title}"))
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Share via WhatsApp / Email / Apps", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(plainText))
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.cardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Text(text = "Copy Plain Text", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(markdownText))
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.cardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Text(text = "Copy Markdown", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Edit Line / Note / Variable Dialog
 */
@Composable
fun EditLineDialog(
    isDocumentTitle: Boolean,
    currentDocTitle: String,
    line: WorksheetLine?,
    theme: ThemePalette,
    onSaveTitle: (String) -> Unit,
    onSaveLine: (WorksheetLine) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)

    var titleInput by remember { mutableStateOf(currentDocTitle) }
    var rawValueInput by remember { mutableStateOf(line?.rawValue ?: "") }
    var noteInput by remember { mutableStateOf(line?.note ?: "") }
    var variableInput by remember { mutableStateOf(line?.variableName ?: "") }
    var selectedOperator by remember { mutableStateOf(line?.operator ?: "+") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isDocumentTitle) "Rename Worksheet" else "Edit Step / Note",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isDocumentTitle) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Worksheet Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = theme.accentColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    line?.let {
                        // Operator selection
                        if (it.lineType == WorksheetLineType.CALCULATION || it.lineType == WorksheetLineType.PERCENTAGE) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("+", "-", "*", "/").forEach { op ->
                                    val isOpSelected = selectedOperator == op
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isOpSelected) theme.accentColor else theme.cardBackground,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                        modifier = Modifier.clickable { selectedOperator = op }
                                    ) {
                                        Text(
                                            text = op,
                                            color = if (isOpSelected) Color.White else textColor,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Amount / Value Field
                        OutlinedTextField(
                            value = rawValueInput,
                            onValueChange = { rawValueInput = it },
                            label = { Text("Amount / Number or Variable") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = theme.accentColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Note / Description Field
                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = { noteInput = it },
                            label = { Text("Item Note / Description (e.g. wax & wick)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = theme.accentColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Output Variable Name (e.g. = Price)
                        OutlinedTextField(
                            value = variableInput,
                            onValueChange = { variableInput = it },
                            label = { Text("Assign to Variable (e.g. Quantity, Price)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = theme.accentColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isDocumentTitle) {
                        onSaveTitle(titleInput.ifBlank { "Worksheet" })
                    } else {
                        line?.let {
                            onSaveLine(
                                it.copy(
                                    operator = selectedOperator,
                                    rawValue = rawValueInput.ifBlank { "0" },
                                    note = noteInput,
                                    variableName = variableInput.ifBlank { null }
                                )
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Cancel", color = textColor)
            }
        }
    )
}

/**
 * Add Line Dialog
 */
@Composable
fun AddLineDialog(
    theme: ThemePalette,
    onAdd: (WorksheetLine) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)

    var stepType by remember { mutableStateOf(WorksheetLineType.CALCULATION) }
    var operatorInput by remember { mutableStateOf("+") }
    var valueInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var variableInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Step to Tape", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Type selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        WorksheetLineType.CALCULATION to "Step (+, -)",
                        WorksheetLineType.PERCENTAGE to "Tax / %",
                        WorksheetLineType.SUB_TOTAL to "Subtotal",
                        WorksheetLineType.COMMENT_HEADER to "Header"
                    ).forEach { (type, label) ->
                        val isSelected = stepType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) theme.accentColor else theme.cardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f).clickable { stepType = type }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else textColor,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                if (stepType == WorksheetLineType.CALCULATION) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("+", "-", "*", "/").forEach { op ->
                            val isOpSelected = operatorInput == op
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isOpSelected) theme.accentColor else theme.cardBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                modifier = Modifier.clickable { operatorInput = op }
                            ) {
                                Text(
                                    text = op,
                                    color = if (isOpSelected) Color.White else textColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                if (stepType != WorksheetLineType.SUB_TOTAL) {
                    OutlinedTextField(
                        value = valueInput,
                        onValueChange = { valueInput = it },
                        label = { Text(if (stepType == WorksheetLineType.PERCENTAGE) "Rate % (e.g. 18)" else "Number or Variable") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textColor, unfocusedTextColor = textColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note / Description (e.g. labor, discount)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textColor, unfocusedTextColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newLine = WorksheetLine(
                        lineType = stepType,
                        operator = if (stepType == WorksheetLineType.PERCENTAGE) (if (operatorInput == "-") "-" else "+") else operatorInput,
                        rawValue = valueInput.ifBlank { "0" },
                        note = noteInput,
                        variableName = variableInput.ifBlank { null }
                    )
                    onAdd(newLine)
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor)
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancel", color = textColor)
            }
        }
    )
}

/**
 * Custom Key Configuration Dialog
 */
@Composable
fun CustomKeyDialog(
    currentRate: String,
    currentType: String,
    theme: ThemePalette,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightCanvas = theme.backgroundColor.luminance() > 0.45f || theme.surfaceColor.luminance() > 0.45f
    val textColor = if (isLightCanvas) Color(0xFF1E293B) else Color(0xFFF1F5F9)

    var rateInput by remember { mutableStateOf(currentRate) }
    var selectedType by remember { mutableStateOf(currentType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Quick Key", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select key type & percentage rate:", color = textColor, fontSize = 12.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("GST" to "+ Tax / GST", "DISC" to "− Discount").forEach { (typeKey, label) ->
                        val isSelected = selectedType == typeKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) theme.accentColor else theme.cardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f).clickable { selectedType = typeKey }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Presets
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("5", "10", "12", "15", "18", "28").forEach { preset ->
                        Surface(
                            shape = CircleShape,
                            color = if (rateInput == preset) theme.accentColor.copy(alpha = 0.2f) else theme.cardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor),
                            modifier = Modifier.clickable { rateInput = preset }
                        ) {
                            Text(
                                text = "$preset%",
                                color = theme.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = rateInput,
                    onValueChange = { rateInput = it },
                    label = { Text("Percentage Rate %") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textColor, unfocusedTextColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(rateInput.ifBlank { "18" }, selectedType) },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor)
            ) {
                Text("Set Key", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
