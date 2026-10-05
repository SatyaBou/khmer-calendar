package com.khmer.calendar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerUtils
import java.time.LocalDate

@Composable
fun WeekView(
    monthDays: List<KhmerDate>,
    selectedDate: KhmerDate?,
    onSelectDate: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDate = selectedDate?.date ?: LocalDate.now()

    // Find the week containing activeDate
    val currentWeekDays = remember(monthDays, activeDate) {
        val dateInList = monthDays.find { it.date == activeDate } ?: monthDays.firstOrNull()
        if (dateInList != null) {
            val index = monthDays.indexOf(dateInList)
            val weekStart = (index / 7) * 7
            monthDays.subList(weekStart, (weekStart + 7).coerceAtMost(monthDays.size))
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Week Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            KhmerUtils.KHMER_WEEKDAYS.forEach { weekday ->
                Text(
                    text = weekday,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Days of Week Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            currentWeekDays.forEach { khmerDate ->
                val isSelected = (selectedDate?.date == khmerDate.date) || (selectedDate == null && khmerDate.isToday)
                DayCellView(
                    khmerDate = khmerDate,
                    isSelected = isSelected,
                    onSelect = onSelectDate,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Day Details
        val dayToDisplay = selectedDate ?: currentWeekDays.find { it.isToday } ?: currentWeekDays.firstOrNull()
        if (dayToDisplay != null) {
            DayView(selectedDate = dayToDisplay)
        }
    }
}
