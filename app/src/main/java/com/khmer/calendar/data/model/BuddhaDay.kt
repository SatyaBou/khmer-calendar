package com.khmer.calendar.data.model

import java.time.LocalDate

data class BuddhaDay(
    val title: String,
    val lunarPhaseName: String,
    val dateFormatted: String,
    val date: LocalDate
)
