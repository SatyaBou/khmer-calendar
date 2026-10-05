package com.khmer.calendar

import com.khmer.calendar.data.util.HolidayJsonParser
import com.khmer.calendar.data.util.KhmerCalendarCalculator
import com.khmer.calendar.data.util.KhmerUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class KhmerCalendarCalculatorTest {

    @Test
    fun testKhmerNumeralConversion() {
        assertEquals("០", KhmerUtils.toKhmerNumeral(0))
        assertEquals("១២៣៤៥៦៧៨៩០", KhmerUtils.toKhmerNumeral("1234567890"))
        assertEquals("២០២៥", KhmerUtils.toKhmerNumeral(2025))
    }

    @Test
    fun testVisakBocheaDateCalculation() {
        // May 22, 2024 was Visak Bochea (15កើត ខែពិសាខ)
        val date = LocalDate.of(2024, 5, 22)
        val khmerDate = KhmerCalendarCalculator.getKhmerDate(date)

        assertEquals(15, khmerDate.lunarDay)
        assertTrue(khmerDate.isWaxing)
        assertEquals("ពិសាខ", khmerDate.lunarMonthName)
        assertTrue(khmerDate.isBuddhaDay)
        assertEquals("ថ្ងៃសីល ពេញបូណ៌មី (១៥កើត)", khmerDate.buddhaDayTitle)
        assertTrue(khmerDate.isHoliday)
        assertEquals("ពិធីបុណ្យវិសាខបូជា", khmerDate.holiday?.nameKhmer)
    }

    @Test
    fun testSolarHolidayNewYear() {
        // Jan 1 is International New Year
        val date = LocalDate.of(2025, 1, 1)
        val khmerDate = KhmerCalendarCalculator.getKhmerDate(date)

        assertTrue(khmerDate.isHoliday)
        assertEquals("ទិវាចូលឆ្នាំសកល", khmerDate.holiday?.nameKhmer)
    }

    @Test
    fun testMonthGridDaysCount() {
        val days = KhmerCalendarCalculator.getMonthGridDays(2025, 1)
        // A month grid has either 35 or 42 cells
        assertTrue(days.size == 35 || days.size == 42)
    }

    @Test
    fun testBuddhaDaysInYear() {
        val buddhaDays = KhmerCalendarCalculator.getBuddhaDaysForYear(2025)
        // A year usually contains ~48-50 Buddha Days (4 per lunar month)
        assertTrue(buddhaDays.size in 45..52)

        // Ensure no two consecutive calendar days are Buddha days
        for (i in 0 until buddhaDays.size - 1) {
            val d1 = buddhaDays[i].date
            val d2 = buddhaDays[i + 1].date
            org.junit.Assert.assertNotEquals("Buddha days should not be on consecutive dates", d1.plusDays(1), d2)
        }

        // Ensure title is non-null, valid, and contains no corrupt Latin letters
        for (buddhaDay in buddhaDays) {
            assertNotNull(buddhaDay.buddhaDayTitle)
            org.junit.Assert.assertFalse(
                "Title should not contain corrupt 'āv': ${buddhaDay.buddhaDayTitle}",
                buddhaDay.buddhaDayTitle?.contains("āv") == true
            )
        }
    }

    @Test
    fun testHolidayJsonParsing() {
        val json = """
            {
              "version": 1,
              "rules": [
                { "id": "intl_new_year", "nameEn": "International New Year", "type": "fixed", "month": 1, "day": 1 },
                { "id": "meak_bochea", "nameEn": "Meak Bochea", "type": "lunar", "lunarMonth": "MEAK", "phase": "KERT", "lunarDay": 15 },
                { "id": "bon_om_touk", "nameEn": "Water Festival (Bon Om Touk)", "type": "lunar", "lunarMonth": "KADEUK", "phase": "KERT", "lunarDay": 14, "days": 3 }
              ],
              "extras": [
                { "date": "2027-04-14", "nameEn": "Khmer New Year Day 1" }
              ]
            }
        """.trimIndent()

        HolidayJsonParser.parseJson(json)

        val jan1 = LocalDate.of(2025, 1, 1)
        val holidayJan1 = HolidayJsonParser.getHoliday(jan1, 1, 1, true)
        assertNotNull(holidayJan1)
        assertEquals("ទិវាចូលឆ្នាំសកល", holidayJan1?.nameKhmer)

        val extraDate = LocalDate.of(2027, 4, 14)
        val holidayExtra = HolidayJsonParser.getHoliday(extraDate, 4, 14, true)
        assertNotNull(holidayExtra)
        assertEquals("Khmer New Year Day 1", holidayExtra?.nameKhmer)
    }

    @Test
    fun testPchumBenDates() {
        val json = """
            {
              "rules": [
                { "id": "pchum_ben", "nameEn": "Pchum Ben Day", "nameKm": "ពិធីបុណ្យភ្ជុំបិណ្ឌ", "type": "lunar", "lunarMonth": "BHAPROBOT", "phase": "ROCH", "lunarDay": 14, "days": 3 }
              ],
              "extras": [
                { "id": "pchum_ben", "date": "2025-10-10", "nameEn": "Pchum Ben Day 1", "nameKm": "ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី១" },
                { "id": "pchum_ben", "date": "2025-10-11", "nameEn": "Pchum Ben Day 2", "nameKm": "ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី២" },
                { "id": "pchum_ben", "date": "2025-10-12", "nameEn": "Pchum Ben Day 3", "nameKm": "ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី៣" }
              ]
            }
        """.trimIndent()

        HolidayJsonParser.parseJson(json)

        // Test September 2025 dates (e.g. Sept 1 to Sept 30) - should NOT be Pchum Ben
        for (day in 1..30) {
            val date = LocalDate.of(2025, 9, day)
            val khmerDate = KhmerCalendarCalculator.getKhmerDate(date)
            if (khmerDate.holiday?.id?.contains("pchum_ben") == true) {
                println("FOUND PCHUM BEN IN SEP: $date -> ${khmerDate.holiday}")
            }
            org.junit.Assert.assertFalse(
                "September $day should not be Pchum Ben",
                khmerDate.holiday?.id?.contains("pchum_ben") == true || khmerDate.holiday?.nameKhmer?.contains("ភ្ជុំបិណ្ឌ") == true
            )
        }

        val oct10 = LocalDate.of(2025, 10, 10)
        val khmerOct10 = KhmerCalendarCalculator.getKhmerDate(oct10)
        assertNotNull(khmerOct10.holiday)
        assertEquals("ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី១", khmerOct10.holiday?.nameKhmer)

        val oct11 = LocalDate.of(2025, 10, 11)
        val khmerOct11 = KhmerCalendarCalculator.getKhmerDate(oct11)
        assertNotNull(khmerOct11.holiday)
        assertEquals("ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី២", khmerOct11.holiday?.nameKhmer)

        val oct12 = LocalDate.of(2025, 10, 12)
        val khmerOct12 = KhmerCalendarCalculator.getKhmerDate(oct12)
        assertNotNull(khmerOct12.holiday)
        assertEquals("ពិធីបុណ្យភ្ជុំបិណ្ឌ ថ្ងៃទី៣", khmerOct12.holiday?.nameKhmer)
    }

    @Test
    fun testFullAssetHolidaysJson() {
        val file = java.io.File("src/main/assets/holidays.json")
        assertTrue("Asset file should exist", file.exists())
        val jsonText = file.readText()
        HolidayJsonParser.parseJson(jsonText)

        // Test every day in 2024 and 2025 to see when Pchum Ben appears!
        for (year in listOf(2024, 2025, 2026)) {
            var curr = LocalDate.of(year, 1, 1)
            val end = LocalDate.of(year, 12, 31)
            val pchumBenDates = mutableListOf<LocalDate>()
            while (!curr.isAfter(end)) {
                val khmerDate = KhmerCalendarCalculator.getKhmerDate(curr)
                if (khmerDate.holiday?.nameKhmer?.contains("ភ្ជុំបិណ្ឌ") == true || khmerDate.holiday?.id?.contains("pchum_ben") == true) {
                    pchumBenDates.add(curr)
                }
                curr = curr.plusDays(1)
            }
            println("PCHUM BEN DATES FOR $year: $pchumBenDates")
            for (date in pchumBenDates) {
                org.junit.Assert.assertNotEquals(
                    "Pchum Ben in $year should not be in September! Found on $date",
                    9,
                    date.monthValue
                )
            }
        }
    }
}
