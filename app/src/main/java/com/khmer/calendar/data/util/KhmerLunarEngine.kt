package com.khmer.calendar.data.util

import java.time.LocalDate

/**
 * Rule-based Khmer lunar (Chhankitek) engine, valid for 1800..2199.
 *
 * Year type (leap month / leap day) comes from the traditional Aharkun/Avoman/Bodithey
 * formulas. Day counting then walks real month lengths (29/30, Jesth +1 in a leap-day
 * year, Asath doubled in a leap-month year), so waxing/waning days and Sil days follow
 * the printed calendar instead of an average moon cycle.
 *
 * Anchor: 1 Koeut Migasir of Khmer year 2024 = 2023-12-13 (checked against the official
 * Visak Bochea / Royal Ploughing / Pchum Ben / Water Festival dates for 2023-2025).
 */
object KhmerLunarEngine {
    const val MIN_YEAR = 1800
    const val MAX_YEAR = 2200
    private const val ANCHOR_YEAR = 2024
    private val ANCHOR_START: Long = LocalDate.of(2023, 12, 13).toEpochDay()
    private val BASE_MONTH_LENGTHS = intArrayOf(29, 30, 29, 30, 29, 30, 29, 30, 29, 30, 29, 30)

    data class LunarInfo(
        /** Khmer year label (the Gregorian year containing Songkran). */
        val khmerYear: Int,
        /** 0 = Migasir ... 11 = Kadeuk. Both leap Asath months use 7. */
        val monthIndex: Int,
        /** 0 = normal month, 1 = Pathamasadh, 2 = Tutiyasadh. */
        val leapOrdinal: Int,
        val isWaxing: Boolean,
        /** 1..15 */
        val day: Int,
        /** 29 or 30 */
        val monthLength: Int,
        val buddhistEra: Int,
    ) {
        val isLastWaningDay: Boolean get() = !isWaxing && day == monthLength - 15
        val isSilDay: Boolean
            get() = day == 8 || (isWaxing && day == 15) || isLastWaningDay

        /**
         * The effective Khmer Zodiac year changes on 1 Koeut Chaetr (monthIndex = 4).
         * Prior to Chaetr (months Migasir, Bous, Meak, Phalgun - index 0..3),
         * it belongs to the previous Zodiac year.
         */
        val effectiveKhmerYear: Int
            get() = if (monthIndex >= 4) khmerYear else khmerYear - 1

        val zodiacYear: String
            get() = KhmerUtils.getZodiacYear(effectiveKhmerYear)

        val sakName: String
            get() = KhmerUtils.getSakName(effectiveKhmerYear)
    }

    // ---- year type -------------------------------------------------------

    private class Raw(val kromathopol: Int, val avoman: Int, val bodithey: Int)

    private fun raw(year: Int): Raw {
        val js = (year - 638).toLong()                 // Jolak Sakaraj
        val t = 292207L * js + 373L
        val harkun = t / 800 + 1
        val kromathopol = (800 - (t % 800)).toInt()
        val avoman = ((11 * harkun + 650) % 692).toInt()
        val bodithey = ((harkun + (11 * harkun + 650) / 692) % 30).toInt()
        return Raw(kromathopol, avoman, bodithey)
    }

    private fun hasLeapMonth(year: Int): Boolean {
        val b = raw(year).bodithey
        val next = raw(year + 1).bodithey
        if (b == 24 && next == 6) return true
        if (b == 25 && next == 5) return false
        return b > 24 || b < 6
    }

    private fun rawLeapDay(year: Int): Boolean {
        val r = raw(year)
        if (r.avoman == 137 && raw(year + 1).avoman == 0) return false
        return if (r.kromathopol <= 207) r.avoman < 127 else r.avoman < 138
    }

    /** A leap day that lands in a leap-month year moves to the next year. */
    private fun hasLeapDay(year: Int): Boolean {
        if (hasLeapMonth(year)) return false
        return rawLeapDay(year) || (rawLeapDay(year - 1) && hasLeapMonth(year - 1))
    }

    private fun yearLength(year: Int): Int = when {
        hasLeapMonth(year) -> 384
        hasLeapDay(year) -> 355
        else -> 354
    }

    // ---- year start table (epoch day of 1 Koeut Migasir) -------------------

    private val starts: LongArray = LongArray(MAX_YEAR - MIN_YEAR + 2).also { s ->
        s[ANCHOR_YEAR - MIN_YEAR] = ANCHOR_START
        for (y in ANCHOR_YEAR + 1..MAX_YEAR + 1) s[y - MIN_YEAR] = s[y - 1 - MIN_YEAR] + yearLength(y - 1)
        for (y in ANCHOR_YEAR - 1 downTo MIN_YEAR) s[y - MIN_YEAR] = s[y + 1 - MIN_YEAR] - yearLength(y)
    }

    private class Month(val index: Int, val length: Int, val leapOrdinal: Int)

    private fun months(year: Int): List<Month> {
        val leapMonth = hasLeapMonth(year)
        val leapDay = hasLeapDay(year)
        val out = ArrayList<Month>(13)
        for (m in 0 until 12) {
            if (m == 7 && leapMonth) {
                out.add(Month(7, 30, 1))
                out.add(Month(7, 30, 2))
            } else {
                out.add(Month(m, if (m == 6 && leapDay) 30 else BASE_MONTH_LENGTHS[m], 0))
            }
        }
        return out
    }

    // ---- public API --------------------------------------------------------

    fun isSupported(date: LocalDate): Boolean {
        val e = date.toEpochDay()
        return e >= starts[0] && e < starts[starts.size - 1]
    }

    fun lunarOf(date: LocalDate): LunarInfo {
        val e = date.toEpochDay()
        require(isSupported(date)) { "Date outside supported range $MIN_YEAR..${MAX_YEAR - 1}: $date" }

        var year = minOf(date.year + 1, MAX_YEAR)
        while (year > MIN_YEAR && starts[year - MIN_YEAR] > e) year--
        while (year < MAX_YEAR && starts[year + 1 - MIN_YEAR] <= e) year++

        var n = (e - starts[year - MIN_YEAR]).toInt()
        for (m in months(year)) {
            if (n < m.length) {
                val d = n + 1
                val waxing = d <= 15
                val dayInPhase = if (waxing) d else d - 15
                val afterVisak = m.index > 5 || (m.index == 5 && (!waxing || dayInPhase >= 15))
                return LunarInfo(
                    khmerYear = year,
                    monthIndex = m.index,
                    leapOrdinal = m.leapOrdinal,
                    isWaxing = waxing,
                    day = dayInPhase,
                    monthLength = m.length,
                    buddhistEra = if (afterVisak) year + 544 else year + 543,
                )
            }
            n -= m.length
        }
        error("unreachable: day offset beyond year length")
    }
}
