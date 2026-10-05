package com.khmer.calendar.data.repository

import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerCalendarCalculator
import java.time.LocalDate

interface KhmerCalendarRepository {
    fun getMonthDays(year: Int, month: Int): List<KhmerDate>
    fun getKhmerDate(date: LocalDate): KhmerDate
    fun getHolidays(year: Int): List<KhmerDate>
    fun getBuddhaDays(year: Int): List<KhmerDate>
}

class KhmerCalendarRepositoryImpl : KhmerCalendarRepository {
    override fun getMonthDays(year: Int, month: Int): List<KhmerDate> {
        return KhmerCalendarCalculator.getMonthGridDays(year, month)
    }

    override fun getKhmerDate(date: LocalDate): KhmerDate {
        return KhmerCalendarCalculator.getKhmerDate(date)
    }

    override fun getHolidays(year: Int): List<KhmerDate> {
        return KhmerCalendarCalculator.getHolidaysForYear(year)
    }

    override fun getBuddhaDays(year: Int): List<KhmerDate> {
        return KhmerCalendarCalculator.getBuddhaDaysForYear(year)
    }
}
