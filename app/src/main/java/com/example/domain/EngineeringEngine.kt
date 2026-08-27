package com.example.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class EngineeringCategory(val displayName: String, val iconName: String) {
    OHMS_LAW("Ohm's & Power", "⚡"),
    CIRCUITS("Circuits & RC", "🔌"),
    MECHANICS("Mechanics & Force", "⚙️"),
    ENERGY_POWER("Work & Energy", "🔋"),
    STRESS_STRAIN("Stress & Materials", "🧱"),
    FLUID_THERMAL("Fluids & Heat", "🌡️"),
    STRUCTURAL("Beam & Structural", "🏗️"),
    CONSTANTS("Eng Constants", "📐")
}

data class EngineeringConstantItem(
    val name: String,
    val symbol: String,
    val value: String,
    val unit: String,
    val description: String
)

data class EngineeringCalculationResult(
    val primaryLabel: String,
    val primaryValue: String,
    val secondaryResults: List<Pair<String, String>>,
    val formulaUsed: String,
    val explanation: String
)

object EngineeringEngine {

    val CONSTANTS_LIST = listOf(
        EngineeringConstantItem("Speed of Light in Vacuum", "c", "2.99792458 × 10⁸", "m/s", "Fundamental cosmic speed limit"),
        EngineeringConstantItem("Standard Gravitational Acceleration", "g", "9.80665", "m/s²", "Standard Earth surface gravity"),
        EngineeringConstantItem("Universal Gravitational Constant", "G", "6.67430 × 10⁻¹¹", "N·m²/kg²", "Newtonian gravity constant"),
        EngineeringConstantItem("Planck's Constant", "h", "6.62607015 × 10⁻³⁴", "J·s", "Quantum of electromagnetic action"),
        EngineeringConstantItem("Elementary Charge", "e", "1.602176634 × 10⁻¹⁹", "C", "Electric charge of a proton/electron"),
        EngineeringConstantItem("Boltzmann Constant", "k_B", "1.380649 × 10⁻²³", "J/K", "Relates thermal energy to temperature"),
        EngineeringConstantItem("Universal Gas Constant", "R", "8.314462618", "J/(mol·K)", "Molar ideal gas constant"),
        EngineeringConstantItem("Permittivity of Free Space", "ε₀", "8.8541878128 × 10⁻¹²", "F/m", "Electric constant of vacuum"),
        EngineeringConstantItem("Permeability of Free Space", "μ₀", "1.256637062 × 10⁻⁶", "N/A² (H/m)", "Magnetic constant of vacuum"),
        EngineeringConstantItem("Stefan-Boltzmann Constant", "σ", "5.670374419 × 10⁻⁸", "W/(m²·K⁴)", "Blackbody radiative heat flux"),
        EngineeringConstantItem("Avogadro's Number", "N_A", "6.02214076 × 10²³", "mol⁻¹", "Number of constituent particles per mole"),
        EngineeringConstantItem("Density of Water at 4°C", "ρ_water", "1000", "kg/m³", "Standard reference liquid density"),
        EngineeringConstantItem("Atmospheric Pressure (1 atm)", "P_atm", "101,325", "Pa (1.01325 bar)", "Standard sea level pressure"),
        EngineeringConstantItem("Steel Elastic Modulus (avg)", "E_steel", "200 × 10⁹", "Pa (200 GPa)", "Structural mild steel Young's modulus"),
        EngineeringConstantItem("Concrete Elastic Modulus (avg)", "E_conc", "30 × 10⁹", "Pa (30 GPa)", "Standard structural concrete modulus")
    )

    private val df = DecimalFormat("#,##0.####", DecimalFormatSymbols(Locale.US))

    fun formatNumber(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Invalid"
        return if (kotlin.math.abs(value) >= 1e7 || (kotlin.math.abs(value) < 1e-4 && value != 0.0)) {
            String.format(Locale.US, "%.4e", value)
        } else {
            df.format(value)
        }
    }

    // --- 1. Ohm's Law & DC/AC Power ---
    fun calcOhmsLaw(voltage: Double?, current: Double?, resistance: Double?, power: Double?): EngineeringCalculationResult {
        return when {
            voltage != null && current != null && voltage > 0 && current > 0 -> {
                val r = voltage / current
                val p = voltage * current
                EngineeringCalculationResult(
                    primaryLabel = "Resistance (R)",
                    primaryValue = "${formatNumber(r)} Ω",
                    secondaryResults = listOf(
                        "Power (P)" to "${formatNumber(p)} Watts (W)",
                        "Voltage (V)" to "${formatNumber(voltage)} V",
                        "Current (I)" to "${formatNumber(current)} A"
                    ),
                    formulaUsed = "R = V / I  |  P = V × I",
                    explanation = "Direct electrical impedance and power dissipated by the current under applied voltage."
                )
            }
            voltage != null && resistance != null && voltage > 0 && resistance > 0 -> {
                val i = voltage / resistance
                val p = (voltage * voltage) / resistance
                EngineeringCalculationResult(
                    primaryLabel = "Current (I)",
                    primaryValue = "${formatNumber(i)} Amperes (A)",
                    secondaryResults = listOf(
                        "Power (P)" to "${formatNumber(p)} Watts (W)",
                        "Voltage (V)" to "${formatNumber(voltage)} V",
                        "Resistance (R)" to "${formatNumber(resistance)} Ω"
                    ),
                    formulaUsed = "I = V / R  |  P = V² / R",
                    explanation = "Resulting electrical flow and Joule heating power across the specified resistor."
                )
            }
            current != null && resistance != null && current > 0 && resistance > 0 -> {
                val v = current * resistance
                val p = current * current * resistance
                EngineeringCalculationResult(
                    primaryLabel = "Voltage (V)",
                    primaryValue = "${formatNumber(v)} Volts (V)",
                    secondaryResults = listOf(
                        "Power (P)" to "${formatNumber(p)} Watts (W)",
                        "Current (I)" to "${formatNumber(current)} A",
                        "Resistance (R)" to "${formatNumber(resistance)} Ω"
                    ),
                    formulaUsed = "V = I × R  |  P = I² × R",
                    explanation = "Potential drop across the resistance and active heat dissipated."
                )
            }
            power != null && voltage != null && power > 0 && voltage > 0 -> {
                val i = power / voltage
                val r = (voltage * voltage) / power
                EngineeringCalculationResult(
                    primaryLabel = "Current (I)",
                    primaryValue = "${formatNumber(i)} Amperes (A)",
                    secondaryResults = listOf(
                        "Resistance (R)" to "${formatNumber(r)} Ω",
                        "Power (P)" to "${formatNumber(power)} W",
                        "Voltage (V)" to "${formatNumber(voltage)} V"
                    ),
                    formulaUsed = "I = P / V  |  R = V² / P",
                    explanation = "Derived operating current and effective load impedance for specified power consumption."
                )
            }
            else -> {
                EngineeringCalculationResult(
                    primaryLabel = "Ohm's Law",
                    primaryValue = "Enter any 2 parameters",
                    secondaryResults = listOf(
                        "Voltage (V)" to "V = I × R",
                        "Current (I)" to "I = V / R",
                        "Resistance (R)" to "R = V / I",
                        "Power (P)" to "P = V × I = I²R"
                    ),
                    formulaUsed = "V = I × R  |  P = V × I",
                    explanation = "Fundamental relationship governing linear electrical circuits."
                )
            }
        }
    }

    // --- 2. Resistors & RC Filter ---
    fun calcSeriesParallel(r1: Double, r2: Double, r3: Double = 0.0): EngineeringCalculationResult {
        val rSeries = r1 + r2 + r3
        val rParallel = if (r1 > 0 && r2 > 0) {
            if (r3 > 0) 1.0 / (1.0 / r1 + 1.0 / r2 + 1.0 / r3) else (r1 * r2) / (r1 + r2)
        } else 0.0

        return EngineeringCalculationResult(
            primaryLabel = "Equivalent Resistance",
            primaryValue = "Series: ${formatNumber(rSeries)} Ω",
            secondaryResults = listOf(
                "Parallel Equivalent" to "${formatNumber(rParallel)} Ω",
                "Total Conductance (G)" to "${formatNumber(if (rParallel > 0) 1.0 / rParallel else 0.0)} Siemens (S)",
                "R1" to "${formatNumber(r1)} Ω",
                "R2" to "${formatNumber(r2)} Ω"
            ),
            formulaUsed = "R_series = R1 + R2 + R3  |  1/R_parallel = 1/R1 + 1/R2 + 1/R3",
            explanation = "Network impedance reduction in parallel vs cumulative addition in series."
        )
    }

    fun calcRcCircuit(resistanceOhms: Double, capacitanceMicroFarads: Double): EngineeringCalculationResult {
        val cFarads = capacitanceMicroFarads * 1e-6
        val tauSeconds = resistanceOhms * cFarads
        val cutoffFreqHz = if (tauSeconds > 0) 1.0 / (2 * PI * tauSeconds) else 0.0

        return EngineeringCalculationResult(
            primaryLabel = "Cutoff Frequency (-3dB)",
            primaryValue = "${formatNumber(cutoffFreqHz)} Hz",
            secondaryResults = listOf(
                "Time Constant (τ = RC)" to "${formatNumber(tauSeconds * 1000)} ms (${formatNumber(tauSeconds)} s)",
                "5τ Full Charge Time (99.3%)" to "${formatNumber(5 * tauSeconds * 1000)} ms",
                "Capacitor Value" to "$capacitanceMicroFarads µF",
                "Resistor Value" to "$resistanceOhms Ω"
            ),
            formulaUsed = "f_c = 1 / (2π × R × C)  |  τ = R × C",
            explanation = "Corner frequency where output drops by 3dB (70.7% amplitude) in first-order passive low/high pass filter."
        )
    }

    // --- 3. Mechanics: Force, Torque, Energy ---
    fun calcNewtonForce(massKg: Double, accelMps2: Double): EngineeringCalculationResult {
        val forceN = massKg * accelMps2
        val forceLbf = forceN * 0.224809
        val weightN = massKg * 9.80665

        return EngineeringCalculationResult(
            primaryLabel = "Net Force (F)",
            primaryValue = "${formatNumber(forceN)} Newtons (N)",
            secondaryResults = listOf(
                "Force in Pound-force (lbf)" to "${formatNumber(forceLbf)} lbf",
                "Static Earth Weight (W = mg)" to "${formatNumber(weightN)} N",
                "Mass (m)" to "$massKg kg",
                "Acceleration (a)" to "$accelMps2 m/s²"
            ),
            formulaUsed = "F = m × a  |  W = m × g",
            explanation = "Newton's 2nd Law of Motion: Unbalanced force accelerates mass."
        )
    }

    fun calcTorquePower(forceN: Double, radiusMeters: Double, rpm: Double = 0.0): EngineeringCalculationResult {
        val torqueNm = forceN * radiusMeters
        val torqueFtLb = torqueNm * 0.737562
        val powerWatts = if (rpm > 0) (2 * PI * rpm * torqueNm) / 60.0 else 0.0
        val powerHp = powerWatts / 745.7

        return EngineeringCalculationResult(
            primaryLabel = "Torque (τ)",
            primaryValue = "${formatNumber(torqueNm)} N·m",
            secondaryResults = listOf(
                "Torque (ft-lb)" to "${formatNumber(torqueFtLb)} lbf·ft",
                "Rotational Power (P)" to if (rpm > 0) "${formatNumber(powerWatts)} W (${formatNumber(powerHp)} HP)" else "Enter RPM",
                "Speed (N)" to if (rpm > 0) "$rpm RPM" else "Static",
                "Moment Arm (r)" to "$radiusMeters m"
            ),
            formulaUsed = "τ = F × r  |  P = (2π × N × τ) / 60",
            explanation = "Rotational moment of force and continuous mechanical output power."
        )
    }

    fun calcKineticPotentialEnergy(massKg: Double, velocityMps: Double, heightMeters: Double): EngineeringCalculationResult {
        val ke = 0.5 * massKg * velocityMps.pow(2)
        val pe = massKg * 9.80665 * heightMeters
        val totalE = ke + pe

        return EngineeringCalculationResult(
            primaryLabel = "Total Mechanical Energy",
            primaryValue = "${formatNumber(totalE)} Joules (J)",
            secondaryResults = listOf(
                "Kinetic Energy (KE)" to "${formatNumber(ke)} J (${formatNumber(ke / 1000)} kJ)",
                "Potential Energy (PE)" to "${formatNumber(pe)} J (${formatNumber(pe / 1000)} kJ)",
                "Velocity" to "$velocityMps m/s (${formatNumber(velocityMps * 3.6)} km/h)",
                "Height" to "$heightMeters m"
            ),
            formulaUsed = "KE = ½ × m × v²  |  PE = m × g × h",
            explanation = "Conservation of mechanical energy during kinematic and gravitational motion."
        )
    }

    // --- 4. Stress, Strain & Young's Modulus ---
    fun calcStressStrain(forceN: Double, areaMm2: Double, origLengthMm: Double = 1000.0, changeLengthMm: Double = 1.0): EngineeringCalculationResult {
        val areaM2 = areaMm2 * 1e-6
        val stressPa = if (areaM2 > 0) forceN / areaM2 else 0.0
        val stressMpa = stressPa / 1e6
        val strain = if (origLengthMm > 0) changeLengthMm / origLengthMm else 0.0
        val youngsModulusGpa = if (strain > 0) (stressPa / strain) / 1e9 else 0.0

        return EngineeringCalculationResult(
            primaryLabel = "Tensile / Compressive Stress (σ)",
            primaryValue = "${formatNumber(stressMpa)} MPa (N/mm²)",
            secondaryResults = listOf(
                "Engineering Strain (ε)" to "${formatNumber(strain)} (or ${formatNumber(strain * 100)}%)",
                "Young's Modulus (E)" to "${formatNumber(youngsModulusGpa)} GPa",
                "Stress in PSI" to "${formatNumber(stressMpa * 145.038)} psi",
                "Applied Load (F)" to "$forceN N"
            ),
            formulaUsed = "σ = F / A  |  ε = ΔL / L₀  |  E = σ / ε",
            explanation = "Internal resistive force per unit cross-sectional area under structural tension or compression."
        )
    }

    // --- 5. Fluids & Thermal ---
    fun calcHydraulicPressure(forceN: Double, areaCm2: Double, depthMeters: Double = 0.0, fluidDensityKgM3: Double = 1000.0): EngineeringCalculationResult {
        val areaM2 = areaCm2 * 1e-4
        val appliedPressurePa = if (areaM2 > 0) forceN / areaM2 else 0.0
        val hydroPressurePa = fluidDensityKgM3 * 9.80665 * depthMeters
        val totalPressureBar = (appliedPressurePa + hydroPressurePa) / 100000.0

        return EngineeringCalculationResult(
            primaryLabel = "Total Fluid Pressure",
            primaryValue = "${formatNumber(totalPressureBar)} Bar (${formatNumber((appliedPressurePa + hydroPressurePa) / 1000)} kPa)",
            secondaryResults = listOf(
                "Applied Surface Pressure" to "${formatNumber(appliedPressurePa / 1000)} kPa (${formatNumber(appliedPressurePa * 0.000145038)} psi)",
                "Hydrostatic Depth Pressure" to "${formatNumber(hydroPressurePa / 1000)} kPa (${formatNumber(depthMeters)} m depth)",
                "Fluid Density" to "$fluidDensityKgM3 kg/m³",
                "Piston Area" to "$areaCm2 cm²"
            ),
            formulaUsed = "P = F / A + ρ × g × h",
            explanation = "Combined hydrostatic head pressure and mechanical piston actuation pressure in hydraulic systems."
        )
    }

    fun calcThermalHeatTransfer(massKg: Double, specificHeatJoulePerKgC: Double, tempDeltaC: Double): EngineeringCalculationResult {
        val heatJoules = massKg * specificHeatJoulePerKgC * tempDeltaC
        val heatKj = heatJoules / 1000.0
        val heatKcal = heatJoules / 4184.0
        val heatBtu = heatJoules * 0.000947817

        return EngineeringCalculationResult(
            primaryLabel = "Thermal Heat Energy (Q)",
            primaryValue = "${formatNumber(heatKj)} kJ",
            secondaryResults = listOf(
                "Heat in Kilocalories (kcal)" to "${formatNumber(heatKcal)} kcal",
                "Heat in BTU" to "${formatNumber(heatBtu)} BTU",
                "Mass (m)" to "$massKg kg",
                "Specific Heat (c)" to "$specificHeatJoulePerKgC J/(kg·°C)",
                "Temperature Rise (ΔT)" to "$tempDeltaC °C"
            ),
            formulaUsed = "Q = m × c × ΔT",
            explanation = "Calorimetric heat energy required to change mass temperature under constant pressure."
        )
    }

    // --- 6. Beam Bending & Structural ---
    fun calcBeamBending(
        loadKn: Double,
        lengthMeters: Double,
        isDistributedLoad: Boolean,
        modulusGpa: Double = 200.0,
        momentOfInertiaCm4: Double = 5000.0
    ): EngineeringCalculationResult {
        val loadN = loadKn * 1000.0
        val ePa = modulusGpa * 1e9
        val iM4 = momentOfInertiaCm4 * 1e-8

        val maxMomentNm = if (isDistributedLoad) {
            (loadN * lengthMeters.pow(2)) / 8.0
        } else {
            (loadN * lengthMeters) / 4.0
        }

        val maxDeflectionMm = if (isDistributedLoad) {
            val defM = (5.0 * loadN * lengthMeters.pow(4)) / (384.0 * ePa * iM4)
            defM * 1000.0
        } else {
            val defM = (loadN * lengthMeters.pow(3)) / (48.0 * ePa * iM4)
            defM * 1000.0
        }

        return EngineeringCalculationResult(
            primaryLabel = "Max Bending Moment (M_max)",
            primaryValue = "${formatNumber(maxMomentNm / 1000.0)} kN·m",
            secondaryResults = listOf(
                "Max Mid-Span Deflection (δ_max)" to "${formatNumber(maxDeflectionMm)} mm",
                "Load Configuration" to if (isDistributedLoad) "Uniformly Distributed (UDL w = $loadKn kN/m)" else "Point Load at Midspan (P = $loadKn kN)",
                "Span Length (L)" to "$lengthMeters meters",
                "Young's Modulus (E)" to "$modulusGpa GPa",
                "Second Moment of Area (I)" to "$momentOfInertiaCm4 cm⁴"
            ),
            formulaUsed = if (isDistributedLoad) "M = wL²/8  |  δ = 5wL⁴ / 384EI" else "M = PL/4  |  δ = PL³ / 48EI",
            explanation = "Simply-supported horizontal structural beam under transverse static loading."
        )
    }
}
