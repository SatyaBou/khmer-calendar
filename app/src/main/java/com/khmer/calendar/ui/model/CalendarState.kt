package com.khmer.calendar.ui.model

import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.settings.AppLanguage
import com.khmer.calendar.data.settings.AppSettings
import com.khmer.calendar.data.util.KhmerUtils
import java.time.LocalDate

enum class CalendarTab(val titleKhmer: String, val titleEnglish: String) {
    CALENDAR("ប្រតិទិន", "Calendar"),
    HOLIDAYS("ថ្ងៃឈប់សម្រាក", "Holidays"),
    BUDDHA_DAYS("ថ្ងៃសីល", "Buddha Days");

    fun getTitle(isKhmer: Boolean): String = if (isKhmer) titleKhmer else titleEnglish
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
    val isDayDetailSheetVisible: Boolean = false,
    val isSettingsSheetVisible: Boolean = false,
    val settings: AppSettings = AppSettings()
) {
    val isKhmerLanguage: Boolean
        get() = settings.language == AppLanguage.KHMER

    val displayedMonthKhmer: String
        get() = KhmerUtils.getSolarMonthKhmer(currentMonth)

    val displayedYearKhmer: String
        get() = KhmerUtils.toKhmerNumeral(currentYear)

    val displayedMonth: String
        get() = KhmerUtils.getSolarMonth(currentMonth, isKhmerLanguage)

    val displayedYear: String
        get() = KhmerUtils.formatNumber(currentYear, isKhmerLanguage)

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
