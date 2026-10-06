package com.khmer.calendar.ui.model

import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.settings.AppLanguage
import com.khmer.calendar.data.settings.AppThemeMode
import com.khmer.calendar.data.settings.FirstDayOfWeekPref

sealed interface CalendarIntent {
    data class SelectDate(val khmerDate: KhmerDate) : CalendarIntent
    object NextMonth : CalendarIntent
    object PreviousMonth : CalendarIntent
    object GoToToday : CalendarIntent
    data class SelectTab(val tab: CalendarTab) : CalendarIntent
    data class SearchQueryChanged(val query: String) : CalendarIntent
    object DismissDayDetailSheet : CalendarIntent
    data class SelectYear(val year: Int) : CalendarIntent

    // Settings Intents
    data class ToggleSettingsSheet(val show: Boolean) : CalendarIntent
    data class UpdateLanguage(val language: AppLanguage) : CalendarIntent
    data class UpdateThemeMode(val themeMode: AppThemeMode) : CalendarIntent
    data class UpdateFirstDayOfWeek(val firstDay: FirstDayOfWeekPref) : CalendarIntent
    data class UpdateShowLunarDate(val show: Boolean) : CalendarIntent
    data class UpdateShowBuddhaDays(val show: Boolean) : CalendarIntent
    data class UpdateShowHolidays(val show: Boolean) : CalendarIntent
    data class UpdateBuddhaDayReminders(val enable: Boolean) : CalendarIntent
    data class UpdateHolidayReminders(val enable: Boolean) : CalendarIntent
}
