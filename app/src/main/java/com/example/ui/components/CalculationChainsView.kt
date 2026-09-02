package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CalculationChainEngine
import com.example.model.CalculationChain
import com.example.model.ChainNode
import com.example.model.ChainNodeType
import com.example.model.ChainScenario
import com.example.model.ThemePalette
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationChainsView(
    theme: ThemePalette,
    chainsList: List<CalculationChain>,
    activeChain: CalculationChain,
    onSelectChain: (CalculationChain) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeScenarioIndex by remember { mutableStateOf(0) }
    val scenarios = listOf(
        ChainScenario("Baseline", 1.0, 0.0),
        ChainScenario("Optimistic (+20%)", 1.2, 2.0),
        ChainScenario("Conservative (-15%)", 0.85, -2.0)
    )
    val currentScenario = scenarios[activeScenarioIndex]

    // Local mutable state for node inputs
    val nodePrimaryValues = remember(activeChain.id) {
        mutableStateMapOf<String, Double>().apply {
            activeChain.nodes.forEach { put(it.id, it.primaryValue) }
        }
    }

    // Modal dialog state for direct keyboard typing
    var editingNodeForKeyboard by remember { mutableStateOf<ChainNode?>(null) }
    var keyboardInputText by remember { mutableStateOf("") }

    // Build the dynamic chain with current primary values
    val currentChainInstance = remember(activeChain.id, nodePrimaryValues.toMap()) {
        activeChain.copy(
            nodes = activeChain.nodes.map { node ->
                node.copy(primaryValue = nodePrimaryValues[node.id] ?: node.primaryValue)
            }
        )
    }

    // Evaluate the reactive cascade in sequence
    val evaluatedNodes = remember(currentChainInstance, currentScenario) {
        CalculationChainEngine.evaluateChain(currentChainInstance, currentScenario)
    }

    val numberFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }

    val finalNode = evaluatedNodes.lastOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        // Top Header & Mode Description
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calculation Chains & Reactive Flow",
                    color = theme.screenTextColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Each result feeds seamlessly into the next step",
                    color = theme.screenExpressionColor,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = {
                    // Reset to defaults
                    activeChain.nodes.forEach {
                        nodePrimaryValues[it.id] = it.primaryValue
                    }
                    activeScenarioIndex = 0
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Chain",
                    tint = theme.screenExpressionColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Chain Selector Chips - Sleek horizontal scrolling row
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(chainsList, key = { it.id }) { chain ->
                val isSelected = chain.id == activeChain.id
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) theme.accentColor else theme.screenBorderColor.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectChain(chain) }
                        .testTag("chip_chain_${chain.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = chain.title,
                            color = if (isSelected) theme.backgroundColor else theme.screenTextColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Reactive Pipeline Scrollable List (Entire view is scrollable/movable)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // What-If Scenario Comparison Bar (Movable as part of the list)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "What-If:",
                                color = theme.screenTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            scenarios.forEachIndexed { index, scenario ->
                                val isScenarioActive = activeScenarioIndex == index
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isScenarioActive) theme.accentColor else theme.cardBackground,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { activeScenarioIndex = index }
                                ) {
                                    Text(
                                        text = scenario.name,
                                        color = if (isScenarioActive) theme.backgroundColor else theme.screenTextColor,
                                        fontSize = 10.sp,
                                        fontWeight = if (isScenarioActive) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FINAL RESULT SECTION (Scrollable inside the list, never blocks the screen)
            if (finalNode != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.accentColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = theme.accentColor
                                    ) {
                                        Text(
                                            text = "FINAL RESULT",
                                            color = theme.backgroundColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = finalNode.title,
                                        color = theme.screenTextColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = currentScenario.name,
                                    color = theme.screenExpressionColor,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "End Outcome",
                                        color = theme.screenExpressionColor,
                                        fontSize = 10.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (finalNode.unit == "₹" || finalNode.unit == "$") {
                                            Text(
                                                text = finalNode.unit,
                                                color = theme.accentColor,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = numberFormatter.format(finalNode.calculatedOutput),
                                            color = theme.accentColor,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (finalNode.unit.isNotEmpty() && finalNode.unit != "₹" && finalNode.unit != "$") {
                                            Text(
                                                text = finalNode.unit,
                                                color = theme.screenTextColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Live Cascaded",
                                    color = theme.secondaryAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Pipeline Nodes
            itemsIndexed(evaluatedNodes, key = { _, node -> node.id }) { index, node ->
                val currentPrimary = nodePrimaryValues[node.id] ?: node.primaryValue
                val stepNumber = index + 1
                val isFirst = index == 0
                val isLast = index == evaluatedNodes.size - 1

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Node Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLast) theme.accentColor.copy(alpha = 0.12f) else theme.surfaceColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isLast) 1.5.dp else 1.dp,
                            color = if (isLast) theme.accentColor else theme.screenBorderColor.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header Row: Step #, Title, Node Type Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = theme.accentColor,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$stepNumber",
                                                color = theme.backgroundColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = node.title,
                                            color = theme.screenTextColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (node.description.isNotBlank()) {
                                            Text(
                                                text = node.description,
                                                color = theme.screenExpressionColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                // Node Operation Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = theme.cardBackground,
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, theme.accentColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = getNodeTypeLabel(node.type),
                                        color = theme.accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Interactive Parameter Row: tap amount directly to change
                            if (node.type != ChainNodeType.CUSTOM_FORMULA) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Parameter:",
                                        color = theme.screenExpressionColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    // Directly Tappable Amount Pill to Edit Value
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = theme.accentColor.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.6f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                editingNodeForKeyboard = node
                                                keyboardInputText = if (currentPrimary == currentPrimary.toLong().toDouble())
                                                    currentPrimary.toLong().toString()
                                                else currentPrimary.toString()
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "${if (node.unit == "₹" || node.unit == "$") node.unit else ""}${numberFormatter.format(currentPrimary)}${if (node.unit.isNotEmpty() && node.unit != "₹" && node.unit != "$") " " + node.unit else ""}",
                                                color = theme.accentColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Tap to edit amount",
                                                tint = theme.accentColor,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                Slider(
                                    value = currentPrimary.toFloat().coerceIn(node.minValue.toFloat(), node.maxValue.toFloat()),
                                    onValueChange = { nodePrimaryValues[node.id] = it.toDouble() },
                                    valueRange = node.minValue.toFloat()..node.maxValue.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = theme.accentColor,
                                        activeTrackColor = theme.accentColor,
                                        inactiveTrackColor = theme.cardBackground
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Resulting Step Output
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(theme.cardBackground, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isFirst) "Initial Input" else if (isLast) "Step Outcome" else "Step Output",
                                    color = theme.screenExpressionColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (node.unit == "₹" || node.unit == "$") {
                                        Text(
                                            text = node.unit,
                                            color = if (isLast) theme.accentColor else theme.screenTextColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = numberFormatter.format(node.calculatedOutput),
                                        color = if (isLast) theme.accentColor else theme.screenTextColor,
                                        fontSize = if (isLast) 18.sp else 15.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (node.unit.isNotEmpty() && node.unit != "₹" && node.unit != "$") {
                                        Text(
                                            text = node.unit,
                                            color = theme.screenExpressionColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Connecting Flow Pipe / Connector
                    if (!isLast) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(10.dp)
                                    .background(theme.accentColor.copy(alpha = 0.5f))
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Cascade into next step",
                                tint = theme.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Final Summary & Insights Banner
            if (finalNode != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.accentColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Reactive Pipeline Summary",
                                    color = theme.screenTextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "When any variable above is adjusted or typed directly, every linked node dynamically updates the entire chain instantly with zero lag.",
                                color = theme.screenExpressionColor,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Direct Keyboard Typing Dialog
    if (editingNodeForKeyboard != null) {
        val targetNode = editingNodeForKeyboard!!
        AlertDialog(
            onDismissRequest = { editingNodeForKeyboard = null },
            title = {
                Text(
                    text = "Type Custom Amount",
                    color = theme.screenTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter exact value for ${targetNode.title} (${targetNode.unit}):",
                        color = theme.screenExpressionColor,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = keyboardInputText,
                        onValueChange = { keyboardInputText = it },
                        placeholder = { Text("e.g. 50, 75000, 2.5", color = theme.screenExpressionColor.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.screenBorderColor,
                            focusedTextColor = theme.screenTextColor,
                            unfocusedTextColor = theme.screenTextColor,
                            focusedContainerColor = theme.surfaceColor,
                            unfocusedContainerColor = theme.surfaceColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = keyboardInputText.toDoubleOrNull()
                        if (parsed != null && parsed >= 0.0) {
                            nodePrimaryValues[targetNode.id] = parsed
                        }
                        editingNodeForKeyboard = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accentColor,
                        contentColor = theme.backgroundColor
                    )
                ) {
                    Text("Apply Amount", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNodeForKeyboard = null }) {
                    Text("Cancel", color = theme.screenExpressionColor)
                }
            },
            containerColor = theme.surfaceColor
        )
    }
}

private fun getNodeTypeLabel(type: ChainNodeType): String {
    return when (type) {
        ChainNodeType.SOURCE_INPUT -> "Base Source"
        ChainNodeType.SUBTRACT_AMOUNT -> "Deduct Amount"
        ChainNodeType.ADD_AMOUNT -> "Add Amount"
        ChainNodeType.MULTIPLY_VALUE -> "Multiply Factor"
        ChainNodeType.DIVIDE_VALUE -> "Divide"
        ChainNodeType.PERCENTAGE_DEDUCT -> "Deduct %"
        ChainNodeType.PERCENTAGE_ADD -> "Markup %"
        ChainNodeType.PERCENTAGE_ALLOCATE -> "Allocate %"
        ChainNodeType.COMPOUND_GROWTH -> "Compound SIP"
        ChainNodeType.CUSTOM_FORMULA -> "Formula"
    }
}

