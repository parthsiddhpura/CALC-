package com.example.domain

import com.example.model.NumberBase
import java.util.Locale

enum class WordSize(val bits: Int, val displayName: String, val mask: Long) {
    BYTE(8, "BYTE (8-bit)", 0xFFL),
    WORD(16, "WORD (16-bit)", 0xFFFFL),
    DWORD(32, "DWORD (32-bit)", 0xFFFFFFFFL),
    QWORD(64, "QWORD (64-bit)", -1L)
}

object ProgrammerEngine {

    fun parseValue(input: String, base: NumberBase, wordSize: WordSize): Long {
        if (input.isBlank() || input == "-" || input == "0") return 0L
        return try {
            val clean = input.replace(" ", "").replace("_", "")
            val v = when (base) {
                NumberBase.HEX -> clean.toLong(16)
                NumberBase.DEC -> clean.toLong(10)
                NumberBase.OCT -> clean.toLong(8)
                NumberBase.BIN -> clean.toLong(2)
            }
            applyWordSizeMask(v, wordSize)
        } catch (e: Exception) {
            0L
        }
    }

    fun formatInBase(value: Long, base: NumberBase, wordSize: WordSize): String {
        val masked = applyWordSizeMask(value, wordSize)
        return when (base) {
            NumberBase.HEX -> {
                val hexStr = java.lang.Long.toHexString(masked).uppercase(Locale.US)
                hexStr.ifEmpty { "0" }
            }
            NumberBase.DEC -> {
                // If top bit is 1 in sub-64bit mode and we treat as signed or unsigned
                masked.toString()
            }
            NumberBase.OCT -> {
                java.lang.Long.toOctalString(masked).ifEmpty { "0" }
            }
            NumberBase.BIN -> {
                val bin = java.lang.Long.toBinaryString(masked)
                formatBinaryWithSpaces(bin, wordSize.bits)
            }
        }
    }

    fun applyWordSizeMask(value: Long, wordSize: WordSize): Long {
        return if (wordSize == WordSize.QWORD) value else (value and wordSize.mask)
    }

    private fun formatBinaryWithSpaces(bin: String, totalBits: Int): String {
        val padded = bin.padStart(totalBits, '0')
        return padded.chunked(4).joinToString(" ")
    }

    fun toggleBit(value: Long, bitIndex: Int, wordSize: WordSize): Long {
        if (bitIndex < 0 || bitIndex >= wordSize.bits) return value
        val mask = 1L shl bitIndex
        val toggled = value xor mask
        return applyWordSizeMask(toggled, wordSize)
    }

    fun performBitwise(a: Long, b: Long, op: String, wordSize: WordSize): Long {
        val res = when (op) {
            "AND" -> a and b
            "OR" -> a or b
            "XOR" -> a xor b
            "LSH", "<<" -> a shl b.toInt()
            "RSH", ">>" -> a ushr b.toInt()
            "MOD" -> if (b != 0L) a % b else a
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0L) a / b else a
            else -> a
        }
        return applyWordSizeMask(res, wordSize)
    }

    fun performNot(a: Long, wordSize: WordSize): Long {
        return applyWordSizeMask(a.inv(), wordSize)
    }
}
