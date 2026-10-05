package com.khmer.calendar.data.util

import java.time.LocalDate

object KhmerUtils {

    private val KHMER_DIGITS = charArrayOf('០', '១', '២', '៣', '៤', '៥', '៦', '៧', '៨', '៩')

    fun toKhmerNumeral(number: Int): String {
        return toKhmerNumeral(number.toString())
    }

    fun toKhmerNumeral(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(KHMER_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    val KHMER_WEEKDAYS = arrayOf(
        "អាទិត្យ",   // Sunday
        "ច័ន្ទ",      // Monday
        "អង្គារ",     // Tuesday
        "ពុធ",       // Wednesday
        "ព្រហស្បតិ៍", // Thursday
        "សុក្រ",     // Friday
        "សៅរ៍"       // Saturday
    )

    val KHMER_WEEKDAYS_SHORT = arrayOf(
        "អា", "ច", "អ", "ព", "ព្រ", "សុ", "ស"
    )

    val KHMER_SOLAR_MONTHS = arrayOf(
        "មករា",    // Jan
        "កុម្ភៈ",    // Feb
        "មិនា",    // Mar
        "មេសា",    // Apr
        "ឧសភា",    // May
        "មិថុនា",   // Jun
        "កក្កដា",   // Jul
        "សីហា",    // Aug
        "កញ្ញា",    // Sep
        "តុលា",    // Oct
        "វិច្ឆិកា",   // Nov
        "ធ្នូ"      // Dec
    )

    val KHMER_LUNAR_MONTHS = arrayOf(
        "មិគសិរ", "បុស្ស", "មាឃ", "ផល្គុន", "ចែត្រ", "ពិសាខ",
        "ជេស្ឋ", "អាសាឍ", "ស្រាពណ៍", "ភទ្របទ", "អស្សុជ", "កក្ដិក"
    )

    val KHMER_ZODIAC_YEARS = arrayOf(
        "ឆ្នាំជូត", "ឆ្នាំឆ្លូវ", "ឆ្នាំខាល", "ឆ្នាំថោះ",
        "ឆ្នាំរោង", "ឆ្នាំម្សាញ់", "ឆ្នាំមមី", "ឆ្នាំមមែ",
        "ឆ្នាំវក", "ឆ្នាំរកា", "ឆ្នាំច", "ឆ្នាំកុរ"
    )

    val KHMER_SAK_NAMES = arrayOf(
        "ឯកស័ក", "ទោស័ក", "ត្រីស័ក", "ចត្វាស័ក", "បញ្ចស័ក",
        "ឆស័ក", "សប្តស័ក", "អដ្ឋស័ក", "នព្វស័ក", "សំរឹទ្ធិស័ក"
    )

    fun getDayOfWeekKhmer(date: LocalDate): String {
        val dayIndex = date.dayOfWeek.value % 7
        return KHMER_WEEKDAYS[dayIndex]
    }

    fun getSolarMonthKhmer(monthOneBased: Int): String {
        if (monthOneBased in 1..12) {
            return KHMER_SOLAR_MONTHS[monthOneBased - 1]
        }
        return ""
    }

    fun getZodiacYear(yearOrBe: Int): String {
        val index = if (yearOrBe > 2400) {
            (yearOrBe + 4) % 12
        } else {
            (yearOrBe + 8) % 12
        }
        val safeIndex = if (index < 0) index + 12 else index
        return KHMER_ZODIAC_YEARS[safeIndex]
    }

    fun getSakName(yearOrBe: Int): String {
        val index = if (yearOrBe > 2400) {
            (yearOrBe + 7) % 10
        } else {
            (yearOrBe + 1) % 10
        }
        val safeIndex = if (index < 0) index + 10 else index
        return KHMER_SAK_NAMES[safeIndex]
    }
}
