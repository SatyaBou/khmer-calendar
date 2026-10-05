package com.khmer.calendar.data.model

import com.khmer.calendar.data.util.KhmerUtils
import java.time.LocalDate

data class KhmerDate(
    val date: LocalDate,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val dayOfWeekKhmer: String,
    val monthNameKhmer: String,
    val yearKhmerNumeral: String,
    val dayKhmerNumeral: String,

    // Lunar Calendar Details
    val lunarDay: Int,             // 1..15
    val isWaxing: Boolean,         // true = កើត, false = រោច
    val lunarMonthName: String,    // មិគសិរ, បុស្ស, មាឃ...
    val lunarMonthIndex: Int,      // 0..11
    val buddhistEra: Int,          // ព.ស.
    val zodiacYear: String,        // ឆ្នាំរោង, ឆ្នាំម្សាញ់...
    val sakName: String,           // ឆស័ក, សប្តស័ក...

    // Special Info
    val isBuddhaDay: Boolean = false,
    val buddhaDayTitle: String? = null,
    val isHoliday: Boolean = false,
    val holiday: Holiday? = null,

    val isToday: Boolean = false,
    val isCurrentMonth: Boolean = true
) {
    val lunarDateFormatted: String
        get() {
            val phase = if (isWaxing) "កើត" else "រោច"
            val dayStr = KhmerUtils.toKhmerNumeral(lunarDay)
            return "ថ្ងៃ $dayStr$phase ខែ$lunarMonthName"
        }

    val fullKhmerDateFormatted: String
        get() {
            val phase = if (isWaxing) "កើត" else "រោច"
            val dayStr = KhmerUtils.toKhmerNumeral(lunarDay)
            val beStr = KhmerUtils.toKhmerNumeral(buddhistEra)
            return "ថ្ងៃ$dayOfWeekKhmer ទី${KhmerUtils.toKhmerNumeral(dayOfMonth)} ខែ$monthNameKhmer - $dayStr$phase ខែ$lunarMonthName $zodiacYear $sakName ព.ស. $beStr"
        }
}
