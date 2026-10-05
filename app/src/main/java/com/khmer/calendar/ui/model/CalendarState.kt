package com.khmer.calendar.ui.model

import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerUtils
import java.time.LocalDate

enum class CalendarTab(val titleKhmer: String) {
    CALENDAR("ប្រតិទិន"),
    HOLIDAYS("ថ្ងៃឈប់សម្រាក"),
    BUDDHA_DAYS("ថ្ងៃសីល")
}

data class CalendarState(
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val selectedDate: KhmerDate? = null,
    val monthDays: List<KhmerDate> = emptyList(),
    val yearHolidays: List<KhmerDate> = emptyList(),
    val yearBuddhaDays: List<KhmerDate> = emptyList(),
    val currentTab: CalendarTab = CalendarTab.CALENDAR,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isDayDetailSheetVisible: Boolean = false
) {
    val displayedMonthKhmer: String
        get() = KhmerUtils.getSolarMonthKhmer(currentMonth)

    val displayedYearKhmer: String
        get() = KhmerUtils.toKhmerNumeral(currentYear)

    val filteredHolidays: List<KhmerDate>
        get() = if (searchQuery.isBlank()) {
            yearHolidays
        } else {
            yearHolidays.filter {
                it.holiday?.nameKhmer?.contains(searchQuery, ignoreCase = true) == true ||
                        it.holiday?.descriptionKhmer?.contains(searchQuery, ignoreCase = true) == true
            }
        }
}
