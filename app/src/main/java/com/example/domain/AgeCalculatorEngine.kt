package com.example.domain

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    
    // Total counters
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val totalSeconds: Long,
    
    // Next birthday details
    val nextBirthdayMonths: Int,
    val nextBirthdayDays: Int,
    val nextBirthdayHours: Int,
    val nextBirthdayMinutes: Int,
    val nextBirthdaySeconds: Int,
    val nextBirthdayDayOfWeek: String,
    
    // Insights
    val dayOfWeekBorn: String,
    val zodiacSign: String,
    val zodiacSymbol: String
)

object AgeCalculatorEngine {

    fun calculateAge(
        birthDateTime: LocalDateTime,
        targetDateTime: LocalDateTime = LocalDateTime.now()
    ): AgeResult {
        if (birthDateTime.isAfter(targetDateTime)) {
            return AgeResult(
                years = 0, months = 0, days = 0, hours = 0, minutes = 0, seconds = 0,
                totalMonths = 0, totalWeeks = 0, totalDays = 0, totalHours = 0, totalMinutes = 0, totalSeconds = 0,
                nextBirthdayMonths = 0, nextBirthdayDays = 0, nextBirthdayHours = 0, nextBirthdayMinutes = 0, nextBirthdaySeconds = 0,
                nextBirthdayDayOfWeek = "N/A",
                dayOfWeekBorn = birthDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                zodiacSign = getZodiacSign(birthDateTime.monthValue, birthDateTime.dayOfMonth).first,
                zodiacSymbol = getZodiacSign(birthDateTime.monthValue, birthDateTime.dayOfMonth).second
            )
        }

        val birthDate = birthDateTime.toLocalDate()
        val targetDate = targetDateTime.toLocalDate()

        // 1. Calculate Years, Months, Days via Period
        val period = Period.between(birthDate, targetDate)
        var years = period.years
        var months = period.months
        var days = period.days

        // Adjust for time difference within the day
        val birthTimeOfDay = birthDateTime.toLocalTime()
        val targetTimeOfDay = targetDateTime.toLocalTime()

        var diffSeconds = targetTimeOfDay.toSecondOfDay() - birthTimeOfDay.toSecondOfDay()
        if (diffSeconds < 0) {
            diffSeconds += 86400 // 24 * 3600
            if (days > 0) {
                days -= 1
            } else if (months > 0) {
                months -= 1
                val prevMonthDate = targetDate.minusMonths(1)
                days = prevMonthDate.lengthOfMonth() - 1
            } else if (years > 0) {
                years -= 1
                months = 11
                val prevMonthDate = targetDate.minusMonths(1)
                days = prevMonthDate.lengthOfMonth() - 1
            }
        }

        val hours = diffSeconds / 3600
        val minutes = (diffSeconds % 3600) / 60
        val seconds = diffSeconds % 60

        // 2. Total Elapsed
        val totalSeconds = Duration.between(birthDateTime, targetDateTime).seconds
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val totalDays = ChronoUnit.DAYS.between(birthDate, targetDate)
        val totalWeeks = totalDays / 7
        val totalMonths = ChronoUnit.MONTHS.between(birthDate, targetDate)

        // 3. Next Birthday calculation
        var nextBirthdayDate = birthDate.withYear(targetDate.year)
        if (nextBirthdayDate.isBefore(targetDate) || nextBirthdayDate.isEqual(targetDate) && targetTimeOfDay.isAfter(birthTimeOfDay)) {
            nextBirthdayDate = birthDate.withYear(targetDate.year + 1)
        }

        val nextBirthdayDateTime = nextBirthdayDate.atTime(birthTimeOfDay)
        val nextDuration = Duration.between(targetDateTime, nextBirthdayDateTime)
        val nextTotalSec = nextDuration.seconds.coerceAtLeast(0)

        val nextBdayPeriod = Period.between(targetDate, nextBirthdayDate)
        val nextMonths = nextBdayPeriod.months
        val nextDays = nextBdayPeriod.days
        val nextHours = (nextTotalSec % 86400) / 3600
        val nextMins = (nextTotalSec % 3600) / 60
        val nextSecs = nextTotalSec % 60

        val nextBdayDayOfWeek = nextBirthdayDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val dayBorn = birthDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val (zodiac, symbol) = getZodiacSign(birthDate.monthValue, birthDate.dayOfMonth)

        return AgeResult(
            years = years,
            months = months,
            days = days,
            hours = hours.toInt(),
            minutes = minutes.toInt(),
            seconds = seconds.toInt(),
            totalMonths = totalMonths,
            totalWeeks = totalWeeks,
            totalDays = totalDays,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            totalSeconds = totalSeconds,
            nextBirthdayMonths = nextMonths,
            nextBirthdayDays = nextDays,
            nextBirthdayHours = nextHours.toInt(),
            nextBirthdayMinutes = nextMins.toInt(),
            nextBirthdaySeconds = nextSecs.toInt(),
            nextBirthdayDayOfWeek = nextBdayDayOfWeek,
            dayOfWeekBorn = dayBorn,
            zodiacSign = zodiac,
            zodiacSymbol = symbol
        )
    }

    private fun getZodiacSign(month: Int, day: Int): Pair<String, String> {
        return when (month) {
            1 -> if (day < 20) "Capricorn" to "♑" else "Aquarius" to "♒"
            2 -> if (day < 19) "Aquarius" to "♒" else "Pisces" to "♓"
            3 -> if (day < 21) "Pisces" to "♓" else "Aries" to "♈"
            4 -> if (day < 20) "Aries" to "♈" else "Taurus" to "♉"
            5 -> if (day < 21) "Taurus" to "♉" else "Gemini" to "♊"
            6 -> if (day < 21) "Gemini" to "♊" else "Cancer" to "♋"
            7 -> if (day < 23) "Cancer" to "♋" else "Leo" to "♌"
            8 -> if (day < 23) "Leo" to "♌" else "Virgo" to "♍"
            9 -> if (day < 23) "Virgo" to "♍" else "Libra" to "♎"
            10 -> if (day < 23) "Libra" to "♎" else "Scorpio" to "♏"
            11 -> if (day < 22) "Scorpio" to "♏" else "Sagittarius" to "♐"
            12 -> if (day < 22) "Sagittarius" to "♐" else "Capricorn" to "♑"
            else -> "Unknown" to "✨"
        }
    }
}
