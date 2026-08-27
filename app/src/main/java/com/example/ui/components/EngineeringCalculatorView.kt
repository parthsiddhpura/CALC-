package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.EngineeringCalculationResult
import com.example.domain.EngineeringCategory
import com.example.domain.EngineeringConstantItem
import com.example.domain.EngineeringEngine
import com.example.model.ThemePalette

@Composable
fun EngineeringCalculatorView(
    theme: ThemePalette,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(EngineeringCategory.OHMS_LAW) }
    val scrollState = rememberScrollState()
    val categoryScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Top Category Bar (Horizontally scrollable) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(categoryScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EngineeringCategory.values().forEach { cat ->
                val isSelected = cat == selectedCategory
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = cat }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(cat.iconName, fontSize = 14.sp)
                        Text(
                            text = cat.displayName,
                            color = if (isSelected) theme.backgroundColor else theme.screenTextColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // --- 2. Category Content ---
        when (selectedCategory) {
            EngineeringCategory.OHMS_LAW -> OhmsLawSection(theme)
            EngineeringCategory.CIRCUITS -> CircuitsSection(theme)
            EngineeringCategory.MECHANICS -> MechanicsSection(theme)
            EngineeringCategory.ENERGY_POWER -> EnergyPowerSection(theme)
            EngineeringCategory.STRESS_STRAIN -> StressStrainSection(theme)
            EngineeringCategory.FLUID_THERMAL -> FluidThermalSection(theme)
            EngineeringCategory.STRUCTURAL -> StructuralBeamSection(theme)
            EngineeringCategory.CONSTANTS -> EngineeringConstantsSection(theme)
        }
    }
}

// ---------------------- 1. OHM'S LAW ----------------------
@Composable
private fun OhmsLawSection(theme: ThemePalette) {
    var voltageInput by remember { mutableStateOf("12") }
    var currentInput by remember { mutableStateOf("2") }
    var resistanceInput by remember { mutableStateOf("") }
    var powerInput by remember { mutableStateOf("") }

    val v = voltageInput.toDoubleOrNull()
    val i = currentInput.toDoubleOrNull()
    val r = resistanceInput.toDoubleOrNull()
    val p = powerInput.toDoubleOrNull()

    val result = remember(v, i, r, p) {
        EngineeringEngine.calcOhmsLaw(v, i, r, p)
    }

    EngineeringCalculatorCard(
        title = "Ohm's Law & Power (V, I, R, P)",
        icon = "⚡",
        theme = theme
    ) {
        Text(
            text = "Fill in any 2 values. The engine automatically solves the remaining electrical parameters.",
            color = theme.screenExpressionColor,
            fontSize = 11.sp
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = voltageInput,
                onValueChange = { voltageInput = it },
                label = "Voltage (V)",
                unit = "Volts",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = currentInput,
                onValueChange = { currentInput = it },
                label = "Current (I)",
                unit = "Amps",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = resistanceInput,
                onValueChange = { resistanceInput = it },
                label = "Resistance (R)",
                unit = "Ohms (Ω)",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = powerInput,
                onValueChange = { powerInput = it },
                label = "Power (P)",
                unit = "Watts (W)",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        EngineeringResultBox(result = result, theme = theme)
    }
}

// ---------------------- 2. CIRCUITS & RC ----------------------
@Composable
private fun CircuitsSection(theme: ThemePalette) {
    var r1Input by remember { mutableStateOf("100") }
    var r2Input by remember { mutableStateOf("220") }
    var r3Input by remember { mutableStateOf("0") }

    var rFilterInput by remember { mutableStateOf("1000") }
    var cFilterInput by remember { mutableStateOf("10") }

    val r1 = r1Input.toDoubleOrNull() ?: 0.0
    val r2 = r2Input.toDoubleOrNull() ?: 0.0
    val r3 = r3Input.toDoubleOrNull() ?: 0.0
    val rFilter = rFilterInput.toDoubleOrNull() ?: 1000.0
    val cFilter = cFilterInput.toDoubleOrNull() ?: 10.0

    val resistorResult = remember(r1, r2, r3) {
        EngineeringEngine.calcSeriesParallel(r1, r2, r3)
    }

    val rcResult = remember(rFilter, cFilter) {
        EngineeringEngine.calcRcCircuit(rFilter, cFilter)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EngineeringCalculatorCard(
            title = "Series & Parallel Resistors",
            icon = "🔌",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = r1Input,
                    onValueChange = { r1Input = it },
                    label = "Resistor R1",
                    unit = "Ω",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = r2Input,
                    onValueChange = { r2Input = it },
                    label = "Resistor R2",
                    unit = "Ω",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = r3Input,
                    onValueChange = { r3Input = it },
                    label = "Resistor R3",
                    unit = "Ω (opt)",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = resistorResult, theme = theme)
        }

        EngineeringCalculatorCard(
            title = "RC Low-Pass / High-Pass Cutoff Filter",
            icon = "📡",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = rFilterInput,
                    onValueChange = { rFilterInput = it },
                    label = "Resistance",
                    unit = "Ω",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = cFilterInput,
                    onValueChange = { cFilterInput = it },
                    label = "Capacitance",
                    unit = "µF",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = rcResult, theme = theme)
        }
    }
}

// ---------------------- 3. MECHANICS ----------------------
@Composable
private fun MechanicsSection(theme: ThemePalette) {
    var massInput by remember { mutableStateOf("15") }
    var accelInput by remember { mutableStateOf("9.81") }

    var forceInput by remember { mutableStateOf("50") }
    var radiusInput by remember { mutableStateOf("0.25") }
    var rpmInput by remember { mutableStateOf("1500") }

    val m = massInput.toDoubleOrNull() ?: 0.0
    val a = accelInput.toDoubleOrNull() ?: 0.0
    val f = forceInput.toDoubleOrNull() ?: 0.0
    val r = radiusInput.toDoubleOrNull() ?: 0.0
    val rpm = rpmInput.toDoubleOrNull() ?: 0.0

    val forceResult = remember(m, a) { EngineeringEngine.calcNewtonForce(m, a) }
    val torqueResult = remember(f, r, rpm) { EngineeringEngine.calcTorquePower(f, r, rpm) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EngineeringCalculatorCard(
            title = "Newton's 2nd Law Force (F = m · a)",
            icon = "⚙️",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = massInput,
                    onValueChange = { massInput = it },
                    label = "Mass (m)",
                    unit = "kg",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = accelInput,
                    onValueChange = { accelInput = it },
                    label = "Acceleration (a)",
                    unit = "m/s²",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = forceResult, theme = theme)
        }

        EngineeringCalculatorCard(
            title = "Torque & Shaft Rotational Power",
            icon = "🔄",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = forceInput,
                    onValueChange = { forceInput = it },
                    label = "Force (F)",
                    unit = "N",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = radiusInput,
                    onValueChange = { radiusInput = it },
                    label = "Moment Arm (r)",
                    unit = "meters",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = rpmInput,
                    onValueChange = { rpmInput = it },
                    label = "Speed",
                    unit = "RPM",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = torqueResult, theme = theme)
        }
    }
}

// ---------------------- 4. ENERGY & POWER ----------------------
@Composable
private fun EnergyPowerSection(theme: ThemePalette) {
    var massInput by remember { mutableStateOf("70") }
    var velocityInput by remember { mutableStateOf("25") }
    var heightInput by remember { mutableStateOf("10") }

    val m = massInput.toDoubleOrNull() ?: 0.0
    val v = velocityInput.toDoubleOrNull() ?: 0.0
    val h = heightInput.toDoubleOrNull() ?: 0.0

    val energyResult = remember(m, v, h) {
        EngineeringEngine.calcKineticPotentialEnergy(m, v, h)
    }

    EngineeringCalculatorCard(
        title = "Kinetic & Potential Energy Conservation",
        icon = "🔋",
        theme = theme
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = massInput,
                onValueChange = { massInput = it },
                label = "Mass (m)",
                unit = "kg",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = velocityInput,
                onValueChange = { velocityInput = it },
                label = "Velocity (v)",
                unit = "m/s",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = heightInput,
                onValueChange = { heightInput = it },
                label = "Height (h)",
                unit = "meters",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }
        EngineeringResultBox(result = energyResult, theme = theme)
    }
}

// ---------------------- 5. STRESS & STRAIN ----------------------
@Composable
private fun StressStrainSection(theme: ThemePalette) {
    var forceInput by remember { mutableStateOf("25000") }
    var areaInput by remember { mutableStateOf("150") }
    var origLenInput by remember { mutableStateOf("1000") }
    var deltaLenInput by remember { mutableStateOf("1.2") }

    val f = forceInput.toDoubleOrNull() ?: 0.0
    val a = areaInput.toDoubleOrNull() ?: 0.0
    val l0 = origLenInput.toDoubleOrNull() ?: 1000.0
    val dl = deltaLenInput.toDoubleOrNull() ?: 1.0

    val stressResult = remember(f, a, l0, dl) {
        EngineeringEngine.calcStressStrain(f, a, l0, dl)
    }

    EngineeringCalculatorCard(
        title = "Mechanical Stress, Strain & Young's Modulus",
        icon = "🧱",
        theme = theme
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = forceInput,
                onValueChange = { forceInput = it },
                label = "Applied Load (F)",
                unit = "N",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = areaInput,
                onValueChange = { areaInput = it },
                label = "Cross-Section Area (A)",
                unit = "mm²",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = origLenInput,
                onValueChange = { origLenInput = it },
                label = "Initial Length (L₀)",
                unit = "mm",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = deltaLenInput,
                onValueChange = { deltaLenInput = it },
                label = "Elongation (ΔL)",
                unit = "mm",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        EngineeringResultBox(result = stressResult, theme = theme)
    }
}

// ---------------------- 6. FLUIDS & THERMAL ----------------------
@Composable
private fun FluidThermalSection(theme: ThemePalette) {
    var forceInput by remember { mutableStateOf("5000") }
    var areaInput by remember { mutableStateOf("25") }
    var depthInput by remember { mutableStateOf("5") }

    var heatMassInput by remember { mutableStateOf("10") }
    var specificHeatInput by remember { mutableStateOf("4184") } // Water = 4184 J/kg°C
    var deltaTInput by remember { mutableStateOf("40") }

    val f = forceInput.toDoubleOrNull() ?: 0.0
    val a = areaInput.toDoubleOrNull() ?: 0.0
    val d = depthInput.toDoubleOrNull() ?: 0.0

    val hm = heatMassInput.toDoubleOrNull() ?: 0.0
    val cp = specificHeatInput.toDoubleOrNull() ?: 4184.0
    val dt = deltaTInput.toDoubleOrNull() ?: 0.0

    val fluidResult = remember(f, a, d) { EngineeringEngine.calcHydraulicPressure(f, a, d) }
    val heatResult = remember(hm, cp, dt) { EngineeringEngine.calcThermalHeatTransfer(hm, cp, dt) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EngineeringCalculatorCard(
            title = "Hydraulic Pressure & Hydrostatic Depth",
            icon = "💧",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = forceInput,
                    onValueChange = { forceInput = it },
                    label = "Piston Force",
                    unit = "N",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = areaInput,
                    onValueChange = { areaInput = it },
                    label = "Piston Area",
                    unit = "cm²",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = depthInput,
                    onValueChange = { depthInput = it },
                    label = "Depth (h)",
                    unit = "meters",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = fluidResult, theme = theme)
        }

        EngineeringCalculatorCard(
            title = "Thermodynamic Heat Transfer (Q = m · c · ΔT)",
            icon = "🌡️",
            theme = theme
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EngineeringInput(
                    value = heatMassInput,
                    onValueChange = { heatMassInput = it },
                    label = "Mass (m)",
                    unit = "kg",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = specificHeatInput,
                    onValueChange = { specificHeatInput = it },
                    label = "Specific Heat (c)",
                    unit = "J/(kg·°C)",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
                EngineeringInput(
                    value = deltaTInput,
                    onValueChange = { deltaTInput = it },
                    label = "Temp Rise (ΔT)",
                    unit = "°C",
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }
            EngineeringResultBox(result = heatResult, theme = theme)
        }
    }
}

// ---------------------- 7. STRUCTURAL BEAM ----------------------
@Composable
private fun StructuralBeamSection(theme: ThemePalette) {
    var loadKnInput by remember { mutableStateOf("15") }
    var lengthInput by remember { mutableStateOf("6") }
    var isUdl by remember { mutableStateOf(false) }
    var modulusGpaInput by remember { mutableStateOf("200") }
    var inertiaInput by remember { mutableStateOf("5000") }

    val load = loadKnInput.toDoubleOrNull() ?: 0.0
    val len = lengthInput.toDoubleOrNull() ?: 0.0
    val mod = modulusGpaInput.toDoubleOrNull() ?: 200.0
    val i = inertiaInput.toDoubleOrNull() ?: 5000.0

    val beamResult = remember(load, len, isUdl, mod, i) {
        EngineeringEngine.calcBeamBending(load, len, isUdl, mod, i)
    }

    EngineeringCalculatorCard(
        title = "Beam Bending Moment & Deflection",
        icon = "🏗️",
        theme = theme
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (!isUdl) theme.accentColor else theme.surfaceColor,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isUdl = false }
            ) {
                Text(
                    text = "Point Load (P)",
                    color = if (!isUdl) theme.backgroundColor else theme.screenTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isUdl) theme.accentColor else theme.surfaceColor,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isUdl = true }
            ) {
                Text(
                    text = "Uniform Load (w)",
                    color = if (isUdl) theme.backgroundColor else theme.screenTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = loadKnInput,
                onValueChange = { loadKnInput = it },
                label = if (isUdl) "Load w (kN/m)" else "Load P (kN)",
                unit = if (isUdl) "kN/m" else "kN",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = lengthInput,
                onValueChange = { lengthInput = it },
                label = "Span Length (L)",
                unit = "meters",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineeringInput(
                value = modulusGpaInput,
                onValueChange = { modulusGpaInput = it },
                label = "Young's Modulus",
                unit = "GPa (Steel=200)",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
            EngineeringInput(
                value = inertiaInput,
                onValueChange = { inertiaInput = it },
                label = "Moment of Inertia (I)",
                unit = "cm⁴",
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        EngineeringResultBox(result = beamResult, theme = theme)
    }
}

// ---------------------- 8. CONSTANTS ----------------------
@Composable
private fun EngineeringConstantsSection(theme: ThemePalette) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredConstants = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            EngineeringEngine.CONSTANTS_LIST
        } else {
            EngineeringEngine.CONSTANTS_LIST.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.symbol.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    EngineeringCalculatorCard(
        title = "Fundamental Engineering & Physical Constants",
        icon = "📐",
        theme = theme
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search constants (e.g., gravity, speed, planck, steel)", fontSize = 12.sp, color = theme.screenExpressionColor) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = theme.accentColor, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.screenBorderColor.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredConstants.forEach { c ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.surfaceColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = c.name,
                                color = theme.screenTextColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.accentColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = c.symbol,
                                    color = theme.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${c.value} ${c.unit}",
                                color = theme.secondaryAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = c.description,
                            color = theme.screenExpressionColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------------- REUSABLE COMPONENTS ----------------------
@Composable
private fun EngineeringCalculatorCard(
    title: String,
    icon: String,
    theme: ThemePalette,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(theme.borderWidthDp, theme.screenBorderColor, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = theme.screenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon, fontSize = 20.sp)
                Text(
                    text = title.uppercase(),
                    color = theme.accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            content()
        }
    }
}

@Composable
private fun EngineeringInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    theme: ThemePalette,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        placeholder = { Text(unit, fontSize = 11.sp, color = theme.screenExpressionColor) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = theme.accentColor,
            unfocusedBorderColor = theme.screenBorderColor.copy(alpha = 0.35f),
            focusedLabelColor = theme.accentColor,
            unfocusedLabelColor = theme.screenExpressionColor
        ),
        modifier = modifier
    )
}

@Composable
private fun EngineeringResultBox(
    result: EngineeringCalculationResult,
    theme: ThemePalette
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = theme.surfaceColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = result.primaryLabel.uppercase(),
                color = theme.screenExpressionColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = result.primaryValue,
                color = theme.secondaryAccent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            HorizontalDivider(color = theme.screenBorderColor.copy(alpha = 0.25f))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                result.secondaryResults.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = label, color = theme.screenExpressionColor, fontSize = 11.sp)
                        Text(
                            text = value,
                            color = theme.screenTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.accentColor.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Formula: ${result.formulaUsed}",
                        color = theme.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = result.explanation,
                        color = theme.screenTextColor,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
