package com.khmer.calendar.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Expected values come from the official Cambodian public-holiday lists for 2023-2025. */
class KhmerLunarEngineTest {
    private val PISAKH = 5
    private val PHOTRABOT = 9
    private val ASSOCH = 10
    private val KADEUK = 11

    private fun at(s: String) = KhmerLunarEngine.lunarOf(LocalDate.parse(s))

    private fun assertLunar(date: String, month: Int, waxing: Boolean, day: Int) {
        val l = at(date)
        assertEquals("$date month", month, l.monthIndex)
        assertEquals("$date waxing", waxing, l.isWaxing)
        assertEquals("$date day", day, l.day)
    }

    @Test fun visakBochea() {            // 15 Koeut Pisakh
        assertLunar("2023-05-04", PISAKH, true, 15)
        assertLunar("2024-05-22", PISAKH, true, 15)
        assertLunar("2025-05-11", PISAKH, true, 15)
    }

    @Test fun royalPloughing() {         // 4 Roach Pisakh
        assertLunar("2023-05-08", PISAKH, false, 4)
        assertLunar("2024-05-26", PISAKH, false, 4)
        assertLunar("2025-05-15", PISAKH, false, 4)
    }

    @Test fun pchumBen() {               // 14 Roach, 15 Roach Photrobot, 1 Koeut Assoch
        assertLunar("2023-10-13", PHOTRABOT, false, 14)
        assertLunar("2023-10-15", ASSOCH, true, 1)
        assertLunar("2024-10-01", PHOTRABOT, false, 14)
        assertLunar("2024-10-03", ASSOCH, true, 1)
    }

    @Test fun waterFestivalFullMoon() {  // 15 Koeut Kadeuk
        assertLunar("2023-11-27", KADEUK, true, 15)
        assertLunar("2024-11-15", KADEUK, true, 15)
        assertLunar("2025-11-05", KADEUK, true, 15)
    }

    @Test fun silDays() {
        assertTrue(at("2024-05-22").isSilDay)       // 15 Koeut
        assertTrue(at("2024-05-30").isSilDay)       // 8 Roach (15 Koeut + 8)
        assertTrue(!at("2024-05-23").isSilDay)
    }

    @Test fun buddhistEraChangesOnVisak() {
        assertEquals(2567, at("2024-05-21").buddhistEra)
        assertEquals(2568, at("2024-05-22").buddhistEra)
    }

    @Test fun zodiacYearAndSakNameChangeOnChaetr() {
        // Early 2024 (before 1 Koeut Chaetr): Rabbit, 5th Sak
        val early2024 = at("2024-01-01")
        assertEquals("ឆ្នាំថោះ", early2024.zodiacYear)
        assertEquals("បញ្ចស័ក", early2024.sakName)

        // April 2024 (Khmer New Year / Chaetr month): Dragon, 6th Sak
        val kne2024 = at("2024-04-14")
        assertEquals("ឆ្នាំរោង", kne2024.zodiacYear)
        assertEquals("ឆស័ក", kne2024.sakName)

        // Early 2025 (before 1 Koeut Chaetr): Dragon, 6th Sak
        val early2025 = at("2025-01-01")
        assertEquals("ឆ្នាំរោង", early2025.zodiacYear)
        assertEquals("ឆស័ក", early2025.sakName)

        // April 2025 (Khmer New Year / Chaetr month): Snake, 7th Sak
        val kne2025 = at("2025-04-14")
        assertEquals("ឆ្នាំម្សាញ់", kne2025.zodiacYear)
        assertEquals("សប្តស័ក", kne2025.sakName)

        // April 2026 (Khmer New Year / Chaetr month): Horse, 8th Sak
        val kne2026 = at("2026-04-14")
        assertEquals("ឆ្នាំមមី", kne2026.zodiacYear)
        assertEquals("អដ្ឋស័ក", kne2026.sakName)
    }

    @Test fun everyDayConvertsAndSilDaysAreRoughlyFourPerMonth() {
        var d = LocalDate.of(2026, 1, 1)
        var sil = 0
        repeat(365) { if (KhmerLunarEngine.lunarOf(d).isSilDay) sil++; d = d.plusDays(1) }
        assertTrue("sil days in a year: $sil", sil in 46..52)
    }
}
