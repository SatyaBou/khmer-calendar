package com.khmer.calendar.data.util

import com.khmer.calendar.data.model.Holiday
import com.khmer.calendar.data.model.KhmerDate
import java.time.LocalDate

object KhmerCalendarCalculator {


    fun getKhmerDate(date: LocalDate, currentDisplayedMonth: Int = date.monthValue): KhmerDate {
        val lunar = KhmerLunarEngine.lunarOf(date)
        val lunarDay = lunar.day
        val isWaxing = lunar.isWaxing
        val lunarMonthIndex = lunar.monthIndex
        val lunarMonthName = when (lunar.leapOrdinal) {
            1 -> "បឋមាសាឍ"
            2 -> "ទុតិយាសាឍ"
            else -> KhmerUtils.KHMER_LUNAR_MONTHS[lunarMonthIndex]
        }

        // Buddhist Era changes on Visak Bochea (15 Koeut Pisakh)
        val buddhistEra = lunar.buddhistEra
        // Zodiac Year and Sak Name change on 1 Koeut Chaetr (monthIndex = 4)
        val zodiacYear = lunar.zodiacYear
        val sakName = lunar.sakName

        // Sil days: 8 and 15 waxing, 8 and the last day waning (14 or 15 depending on month length)
        val isBuddhaDay = lunar.isSilDay
        val buddhaDayTitle = when {
            isWaxing && lunarDay == 8 -> "ថ្ងៃសីល ៨កើត"
            isWaxing && lunarDay == 15 -> "ថ្ងៃសីល ពេញបូណ៌មី (១៥កើត)"
            !isWaxing && lunarDay == 8 -> "ថ្ងៃសីល ៨រោច"
            lunar.isLastWaningDay -> "ថ្ងៃសីល អមាវាសី (${KhmerUtils.toKhmerNumeral(lunarDay)}រោច)"
            else -> null
        }

        val holiday = getHolidayForDate(date, lunarMonthIndex, lunarDay, isWaxing)
        val isHoliday = holiday != null
        val isToday = date == LocalDate.now()
        val isCurrentMonth = date.monthValue == currentDisplayedMonth

        return KhmerDate(
            date = date,
            dayOfMonth = date.dayOfMonth,
            month = date.monthValue,
            year = date.year,
            dayOfWeekKhmer = KhmerUtils.getDayOfWeekKhmer(date),
            monthNameKhmer = KhmerUtils.getSolarMonthKhmer(date.monthValue),
            yearKhmerNumeral = KhmerUtils.toKhmerNumeral(date.year),
            dayKhmerNumeral = KhmerUtils.toKhmerNumeral(date.dayOfMonth),
            lunarDay = lunarDay,
            isWaxing = isWaxing,
            lunarMonthName = lunarMonthName,
            lunarMonthIndex = lunarMonthIndex,
            buddhistEra = buddhistEra,
            zodiacYear = zodiacYear,
            sakName = sakName,
            isBuddhaDay = isBuddhaDay,
            buddhaDayTitle = buddhaDayTitle,
            isHoliday = isHoliday,
            holiday = holiday,
            isToday = isToday,
            isCurrentMonth = isCurrentMonth
        )
    }
    private fun getHolidayForDate(
        date: LocalDate,
        lunarMonthIndex: Int,
        lunarDay: Int,
        isWaxing: Boolean
    ): Holiday? {
        // First try JSON Parser
        val jsonHoliday = HolidayJsonParser.getHoliday(date, lunarMonthIndex, lunarDay, isWaxing)
        if (jsonHoliday != null) {
            return jsonHoliday
        }

        val month = date.monthValue
        val day = date.dayOfMonth

        // Fallback Solar Holidays
        when {
            month == 1 && day == 1 -> return Holiday(
                id = "ny",
                nameKhmer = "ទិវាចូលឆ្នាំសកល",
                descriptionKhmer = "International New Year's Day",
                dateFormatted = "១ មករា"
            )
            month == 1 && day == 7 -> return Holiday(
                id = "genocide_day",
                nameKhmer = "ទិវាជ័យជម្នះលើរបបប្រល័យពូជសាសន៍",
                descriptionKhmer = "Victory over Genocide Day",
                dateFormatted = "៧ មករា"
            )
            month == 3 && day == 8 -> return Holiday(
                id = "womens_day",
                nameKhmer = "ទិវានារីអន្តរជាតិ",
                descriptionKhmer = "International Women's Day",
                dateFormatted = "៨ មិនា"
            )
            month == 4 && day in 13..16 -> return Holiday(
                id = "khmer_new_year",
                nameKhmer = "ពិធីបុណ្យចូលឆ្នាំថ្មី ប្រពៃណីជាតិ",
                descriptionKhmer = "Khmer New Year Festival",
                dateFormatted = "១៣-១៦ មេសា",
                imageName = "ic_pchum_ben"
            )
            month == 5 && day == 1 -> return Holiday(
                id = "labor_day",
                nameKhmer = "ទិវាពលកម្មអន្តរជាតិ",
                descriptionKhmer = "International Labor Day",
                dateFormatted = "១ ឧសភា"
            )
            month == 5 && day == 14 -> return Holiday(
                id = "king_birthday",
                nameKhmer = "ព្រះរាជពិធីបុណ្យចំរើនព្រះជន្ម ព្រះករុណា ព្រះបាទសម្តេចព្រះបរមនាថ នរោត្តម សីហមុនី",
                descriptionKhmer = "King Norodom Sihamoni's Birthday",
                dateFormatted = "១៤ ឧសភា"
            )
            month == 6 && day == 18 -> return Holiday(
                id = "queen_birthday",
                nameKhmer = "ព្រះរាជពិធីបុណ្យចំរើនព្រះជន្ម សម្តេចព្រះមហាក្សត្រី នរោត្តម មុនិនាថ សីហនុ",
                descriptionKhmer = "Queen Mother's Birthday",
                dateFormatted = "១៨ មិថុនា"
            )
            month == 9 && day == 24 -> return Holiday(
                id = "constitution_day",
                nameKhmer = "ទិវារដ្ឋធម្មនុញ្ញ",
                descriptionKhmer = "Constitutional Day",
                dateFormatted = "២៤ កញ្ញា"
            )
            month == 10 && day == 15 -> return Holiday(
                id = "king_father_day",
                nameKhmer = "ទិវាប្រារព្ធពិធីគោរពព្រះវិញ្ញាណក្ខន្ធ ព្រះករុណា ព្រះបាទសម្តេចព្រះ នរោត្តម សីហនុ",
                descriptionKhmer = "Commemoration Day of King Father Norodom Sihanouk",
                dateFormatted = "១៥ តុលា"
            )
            month == 10 && day == 29 -> return Holiday(
                id = "king_coronation_day",
                nameKhmer = "ព្រះរាជពិធីគ្រងព្រះបរមរាជសម្បត្តិ ព្រះករុណា ព្រះបាទសម្តេចព្រះបរមនាថ នរោត្តម សីហមុនី",
                descriptionKhmer = "King's Coronation Day",
                dateFormatted = "២៩ តុលា"
            )
            month == 11 && day == 9 -> return Holiday(
                id = "independence_day",
                nameKhmer = "ពិធីបុណ្យឯករាជ្យជាតិ",
                descriptionKhmer = "National Independence Day",
                dateFormatted = "៩ វិច្ឆិកា"
            )
        }

        // Fallback Lunar-based Holidays
        if (lunarMonthIndex == 2 && isWaxing && lunarDay == 15) {
            return Holiday(
                id = "meak_bochea",
                nameKhmer = "ពិធីបុណ្យមាឃបូជា",
                descriptionKhmer = "Meak Bochea Day",
                dateFormatted = "១៥កើត ខែមាឃ"
            )
        }

        if (lunarMonthIndex == 5 && isWaxing && lunarDay == 15) {
            return Holiday(
                id = "visak_bochea",
                nameKhmer = "ពិធីបុណ្យវិសាខបូជា",
                descriptionKhmer = "Visak Bochea Day",
                dateFormatted = "១៥កើត ខែពិសាខ"
            )
        }

        if (lunarMonthIndex == 5 && !isWaxing && lunarDay == 4) {
            return Holiday(
                id = "plowing_ceremony",
                nameKhmer = "ព្រះរាជពិធីច្រត់ព្រះនង្គ័ល",
                descriptionKhmer = "Royal Plowing Ceremony",
                dateFormatted = "៤រោច ខែពិសាខ"
            )
        }

        if (!HolidayJsonParser.hasExtrasForRule("pchum_ben", date.year) &&
            ((lunarMonthIndex == 9 && !isWaxing && (lunarDay == 14 || lunarDay == 15)) ||
             (lunarMonthIndex == 10 && isWaxing && lunarDay == 1))
        ) {
            return Holiday(
                id = "pchum_ben",
                nameKhmer = "ពិធីបុណ្យភ្ជុំបិណ្ឌ",
                descriptionKhmer = "Pchum Ben Day",
                dateFormatted = "១៤-១៥រោច ខែភទ្របទ - ១កើត ខែអស្សុជ",
                imageName = "ic_pchum_ben"
            )
        }

        if (lunarMonthIndex == 11 && ((isWaxing && (lunarDay == 14 || lunarDay == 15)) || (!isWaxing && lunarDay == 1))) {
            return Holiday(
                id = "water_festival",
                nameKhmer = "ព្រះរាជពិធីបុណ្យអុំទូក បណ្តែតប្រទីប និងសំពះព្រះខែ អកអំបុក",
                descriptionKhmer = "Water Festival (Bon Om Touk)",
                dateFormatted = "១៤-១៥កើត, ១រោច ខែកក្ដិក"
            )
        }

        return null
    }

    /**
     * Generates all days to display in a month calendar grid
     */
    fun getMonthGridDays(year: Int, month: Int): List<KhmerDate> {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val daysInMonth = firstDayOfMonth.lengthOfMonth()
        
        val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7

        val list = mutableListOf<KhmerDate>()

        val prevMonthFirstDay = firstDayOfMonth.minusMonths(1)
        val prevMonthDaysCount = prevMonthFirstDay.lengthOfMonth()
        for (i in (prevMonthDaysCount - firstDayOfWeekIndex + 1)..prevMonthDaysCount) {
            val paddingDate = LocalDate.of(prevMonthFirstDay.year, prevMonthFirstDay.monthValue, i)
            list.add(getKhmerDate(paddingDate, month))
        }

        for (i in 1..daysInMonth) {
            val date = LocalDate.of(year, month, i)
            list.add(getKhmerDate(date, month))
        }

        val totalCells = if (list.size > 35) 42 else 35
        val remainingCells = totalCells - list.size
        val nextMonth = firstDayOfMonth.plusMonths(1)
        for (i in 1..remainingCells) {
            val paddingDate = LocalDate.of(nextMonth.year, nextMonth.monthValue, i)
            list.add(getKhmerDate(paddingDate, month))
        }

        return list
    }

    /**
     * Returns all Buddha days for a specific year
     */
    fun getBuddhaDaysForYear(year: Int): List<KhmerDate> {
        val list = mutableListOf<KhmerDate>()
        var curr = LocalDate.of(year, 1, 1)
        val end = LocalDate.of(year, 12, 31)
        while (!curr.isAfter(end)) {
            val khmerDate = getKhmerDate(curr)
            if (khmerDate.isBuddhaDay) {
                list.add(khmerDate)
            }
            curr = curr.plusDays(1)
        }
        return list
    }

    /**
     * Returns all Khmer Holidays for a specific year
     */
    fun getHolidaysForYear(year: Int): List<KhmerDate> {
        val list = mutableListOf<KhmerDate>()
        var curr = LocalDate.of(year, 1, 1)
        val end = LocalDate.of(year, 12, 31)
        while (!curr.isAfter(end)) {
            val khmerDate = getKhmerDate(curr)
            if (khmerDate.isHoliday) {
                list.add(khmerDate)
            }
            curr = curr.plusDays(1)
        }
        return list
    }
}
