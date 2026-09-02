package com.example.model

enum class CalculatorMode(val title: String, val shortName: String) {
    STANDARD("Standard", "Basic"),
    GST_CALCULATOR("GST Calc", "GST"),
    CURRENCY_CONVERTER("Currency & ₹", "Forex"),
    SCIENTIFIC("Scientific", "Sci"),
    AGE_CALCULATOR("Age & Rashi", "Age"),
    BMI_CALCULATOR("BMI Health", "BMI"),
    EMI_LOAN("EMI / Loan", "EMI"),
    PROGRAMMER("Programmer", "Prog"),
    UNIT_CONVERTER("Unit Converter", "Convert"),
    TIP_SPLIT("Tip & Split", "Tip"),
    ENGINEERING("Engineering", "Eng"),
    CUSTOM_BUILDER("Build a Calc", "Builder"),
    CALCULATION_CHAINS("Chains & Flow", "Chains"),
    AI_COPILOT("AI Math Copilot", "AI Copilot")
}

enum class AngleMode {
    DEG, RAD
}

enum class NumberBase(val radix: Int, val prefix: String) {
    HEX(16, "HEX"),
    DEC(10, "DEC"),
    OCT(8, "OCT"),
    BIN(2, "BIN")
}
