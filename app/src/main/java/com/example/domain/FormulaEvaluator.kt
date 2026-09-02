package com.example.domain

import java.util.Locale
import kotlin.math.*

object FormulaEvaluator {

    /**
     * Evaluates a mathematical expression string with given variable values.
     * e.g., "(raw_material + labour + electricity + overhead) * (1 + profit_margin / 100) / units"
     */
    fun evaluate(expression: String, variables: Map<String, Double>): Double {
        if (expression.isBlank()) return 0.0
        try {
            var processed = expression.trim().lowercase(Locale.ROOT)

            // Sort variables by length descending so longer variable names are replaced first
            val sortedVars = variables.entries.sortedByDescending { it.key.length }
            for ((key, value) in sortedVars) {
                val cleanKey = key.lowercase(Locale.ROOT).trim()
                if (cleanKey.isNotEmpty()) {
                    val regex = Regex("\\b${Regex.escape(cleanKey)}\\b")
                    val formattedVal = if (value.isNaN() || value.isInfinite()) "0.0" else value.toString()
                    processed = processed.replace(regex, "($formattedVal)")
                }
            }

            // Also support {prev} placeholder
            if (variables.containsKey("prev")) {
                val prevVal = variables["prev"] ?: 0.0
                processed = processed.replace("{prev}", "($prevVal)")
                processed = processed.replace("prev", "($prevVal)")
            }

            return parseExpression(processed)
        } catch (e: Exception) {
            return 0.0
        }
    }

    private fun parseExpression(expr: String): Double {
        val tokens = tokenize(expr)
        val parser = ExpressionParser(tokens)
        return parser.parse()
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val clean = expr.replace(" ", "")

        while (i < clean.length) {
            val c = clean[i]
            when {
                c in "+-*/^%()," -> {
                    tokens.add(c.toString())
                    i++
                }
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < clean.length && (clean[i].isDigit() || clean[i] == '.' || clean[i] == 'e' || clean[i] == 'E')) {
                        if ((clean[i] == 'e' || clean[i] == 'E') && i + 1 < clean.length && (clean[i + 1] == '+' || clean[i + 1] == '-')) {
                            sb.append(clean[i])
                            sb.append(clean[i + 1])
                            i += 2
                        } else {
                            sb.append(clean[i])
                            i++
                        }
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() || c == '_' -> {
                    val sb = StringBuilder()
                    while (i < clean.length && (clean[i].isLetterOrDigit() || clean[i] == '_')) {
                        sb.append(clean[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                else -> i++
            }
        }
        return tokens
    }

    private class ExpressionParser(private val tokens: List<String>) {
        private var pos = 0

        fun parse(): Double {
            if (tokens.isEmpty()) return 0.0
            val res = parseAdditive()
            return if (res.isNaN()) 0.0 else res
        }

        private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): String = tokens[pos++]

        private fun parseAdditive(): Double {
            var left = parseMultiplicative()
            while (peek() == "+" || peek() == "-") {
                val op = consume()
                val right = parseMultiplicative()
                left = if (op == "+") left + right else left - right
            }
            return left
        }

        private fun parseMultiplicative(): Double {
            var left = parseExponential()
            while (peek() == "*" || peek() == "/" || peek() == "%") {
                val op = consume()
                val right = parseExponential()
                left = when (op) {
                    "*" -> left * right
                    "/" -> if (right != 0.0) left / right else 0.0
                    "%" -> if (right != 0.0) left % right else 0.0
                    else -> left
                }
            }
            return left
        }

        private fun parseExponential(): Double {
            var left = parseUnary()
            while (peek() == "^") {
                consume()
                val right = parseUnary()
                left = left.pow(right)
            }
            return left
        }

        private fun parseUnary(): Double {
            if (peek() == "+") {
                consume()
                return parseUnary()
            }
            if (peek() == "-") {
                consume()
                return -parseUnary()
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: return 0.0

            if (token == "(") {
                consume() // (
                val expr = parseAdditive()
                if (peek() == ")") consume() // )
                return expr
            }

            // Function calls
            if (token in listOf("sqrt", "abs", "round", "ceil", "floor", "log", "ln", "sin", "cos", "tan", "min", "max", "pow")) {
                val funcName = consume()
                if (peek() == "(") {
                    consume() // (
                    val arg1 = parseAdditive()
                    var arg2 = 0.0
                    if (peek() == ",") {
                        consume()
                        arg2 = parseAdditive()
                    }
                    if (peek() == ")") consume() // )

                    return when (funcName) {
                        "sqrt" -> if (arg1 >= 0) sqrt(arg1) else 0.0
                        "abs" -> abs(arg1)
                        "round" -> round(arg1)
                        "ceil" -> ceil(arg1)
                        "floor" -> floor(arg1)
                        "log", "ln" -> if (arg1 > 0) ln(arg1) else 0.0
                        "sin" -> sin(arg1)
                        "cos" -> cos(arg1)
                        "tan" -> tan(arg1)
                        "min" -> min(arg1, arg2)
                        "max" -> max(arg1, arg2)
                        "pow" -> arg1.pow(arg2)
                        else -> arg1
                    }
                }
            }

            // Number
            consume()
            return token.toDoubleOrNull() ?: 0.0
        }
    }
}
