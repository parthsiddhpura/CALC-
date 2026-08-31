package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorMode
import com.example.model.ThemePalette

data class ModeItemData(
    val mode: CalculatorMode,
    val title: String,
    val description: String,
    val icon: ImageVector
)

val MORE_MODES = listOf(
    ModeItemData(
        mode = CalculatorMode.BMI_CALCULATOR,
        title = "BMI Calculator",
        description = "Body Mass Index, BMR, ideal weight & metabolic health gauge",
        icon = Icons.Default.FitnessCenter
    ),
    ModeItemData(
        mode = CalculatorMode.CURRENCY_CONVERTER,
        title = "Live Currency & Rupees (₹)",
        description = "Real-time Forex rates for all 160+ world currencies & INR presets",
        icon = Icons.Default.CurrencyExchange
    ),
    ModeItemData(
        mode = CalculatorMode.AGE_CALCULATOR,
        title = "Age & Real Vedic Rashi",
        description = "Live chronological age, profiles history, export/share & Jyotish horoscope",
        icon = Icons.Default.AutoAwesome
    ),
    ModeItemData(
        mode = CalculatorMode.EMI_LOAN,
        title = "EMI & Loan Calculator",
        description = "Monthly installment, total interest & loan analysis",
        icon = Icons.Default.AccountBalance
    ),
    ModeItemData(
        mode = CalculatorMode.PROGRAMMER,
        title = "Programmer Mode",
        description = "HEX, DEC, OCT, BIN base conversions & bitwise operators",
        icon = Icons.Default.Code
    ),
    ModeItemData(
        mode = CalculatorMode.UNIT_CONVERTER,
        title = "Unit Converter",
        description = "Length, Mass, Temperature, Speed, Data & Time conversion",
        icon = Icons.Default.SwapHoriz
    ),
    ModeItemData(
        mode = CalculatorMode.TIP_SPLIT,
        title = "Tip & Split (₹)",
        description = "Bill split, custom tip percentage & per-person sharing in Rupees",
        icon = Icons.Default.ReceiptLong
    ),
    ModeItemData(
        mode = CalculatorMode.ENGINEERING,
        title = "Engineering Calculation",
        description = "Ohm's law, RC circuits, mechanics, stress/strain, beam bending & constants",
        icon = Icons.Default.Calculate
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreModesSheet(
    activeMode: CalculatorMode,
    theme: ThemePalette,
    onSelectMode: (CalculatorMode) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = theme.surfaceColor,
        contentColor = theme.screenTextColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header
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
                        color = theme.accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "More Calculators & Tools",
                            color = theme.screenTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Specialized calculation modes",
                            color = theme.screenExpressionColor,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = theme.screenTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Modes List (Scrollable on any screen)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 36.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                items(MORE_MODES, key = { it.mode.name }) { item ->
                    val isSelected = item.mode == activeMode
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = theme.cardBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) theme.accentColor else theme.screenBorderColor.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectMode(item.mode)
                                onDismiss()
                            }
                            .testTag("more_mode_${item.mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) theme.backgroundColor else theme.accentColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = item.title,
                                        color = theme.screenTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        color = theme.screenExpressionColor,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = theme.accentColor,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = theme.backgroundColor,
                                            modifier = Modifier.size(14.dp)
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
