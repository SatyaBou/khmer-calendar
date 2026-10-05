package com.khmer.calendar.ui.model

import com.khmer.calendar.data.model.KhmerDate

sealed interface CalendarIntent {
    data class SelectDate(val khmerDate: KhmerDate) : CalendarIntent
    object NextMonth : CalendarIntent
    object PreviousMonth : CalendarIntent
    object GoToToday : CalendarIntent
    data class SelectTab(val tab: CalendarTab) : CalendarIntent
    data class SearchQueryChanged(val query: String) : CalendarIntent
    object DismissDayDetailSheet : CalendarIntent
    data class SelectYear(val year: Int) : CalendarIntent
}
