package com.khmer.calendar.ui.model

import com.khmer.calendar.data.model.KhmerDate

sealed interface CalendarEffect {
    data class ShowToast(val message: String) : CalendarEffect
    data class ScrollToDate(val khmerDate: KhmerDate) : CalendarEffect
    data class ScrollToMonth(val year: Int, val month: Int) : CalendarEffect
}
