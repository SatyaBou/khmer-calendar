package com.khmer.calendar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.R
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.settings.AppLanguage
import com.khmer.calendar.data.settings.AppSettings
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.theme.HolidayRed
import java.time.LocalDate

@Composable
fun MonthGridView(
    monthDays: List<KhmerDate>,
    selectedDate: KhmerDate?,
    onSelectDate: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier,
    settings: AppSettings = AppSettings()
) {
    val isKhmer = settings.language == AppLanguage.KHMER
    val weekdays = if (isKhmer) KhmerUtils.KHMER_WEEKDAYS else KhmerUtils.ENGLISH_WEEKDAYS_SHORT

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Weekday Headers
        val currentDayOfWeekIndex = remember {
            LocalDate.now().dayOfWeek.value % 7
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekdays.forEachIndexed { index, weekday ->
                val isSelectedDay = index == currentDayOfWeekIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = weekday,
                        modifier = Modifier.basicMarquee(),
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelectedDay) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isSelectedDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
                }
            }
        }

        // Calendar Grid
        Column(modifier = Modifier.fillMaxWidth()) {
            monthDays.chunked(7).forEach { weekDays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weekDays.forEach { khmerDate ->
                        val isSelected = selectedDate?.date == khmerDate.date
                        DayCellView(
                            khmerDate = khmerDate,
                            isSelected = isSelected,
                            onSelect = onSelectDate,
                            isKhmer = isKhmer,
                            showLunarDate = settings.showLunarDate,
                            showBuddhaDays = settings.showBuddhaDays,
                            showHolidays = settings.showHolidays,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.85f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Indicator Legend Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (settings.showBuddhaDays) {
                // Buddha Day Legend
                Image(
                    painter = painterResource(id = R.drawable.ic_sil_day),
                    contentDescription = "Buddha Day",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isKhmer) "ថ្ងៃសីល" else "Buddha Day",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (settings.showHolidays) {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }

            if (settings.showHolidays) {
                // Holiday Legend
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HolidayRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isKhmer) "ថ្ងៃឈប់សម្រាក" else "Public Holiday",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Holiday Content for Current Month
        val monthHolidays = remember(monthDays, settings.showHolidays) {
            if (settings.showHolidays) {
                monthDays.filter { it.isCurrentMonth && it.isHoliday && it.holiday != null }
            } else {
                emptyList()
            }
        }

        if (monthHolidays.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = if (isKhmer) "ថ្ងៃឈប់សម្រាកប្រចាំខែ" else "Monthly Holidays",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )

                monthHolidays.forEach { khmerDate ->
                    val holiday = khmerDate.holiday ?: return@forEach
                    HolidayCard(
                        khmerDate = khmerDate,
                        holiday = holiday,
                        onClick = { onSelectDate(khmerDate) },
                        isKhmer = isKhmer
                    )
                }
            }
        }
    }
}
