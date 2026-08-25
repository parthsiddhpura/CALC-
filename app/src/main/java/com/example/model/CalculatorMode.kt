package com.example.model

enum class CalculatorMode(val title: String, val shortName: String) {
    STANDARD("Standard", "Basic"),
    SCIENTIFIC("Scientific", "Sci"),
    PROGRAMMER("Programmer", "Prog"),
    UNIT_CONVERTER("Unit Converter", "Convert"),
    TIP_SPLIT("Tip & Split", "Tip")
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
