package com.khmer.calendar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.R
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.theme.HolidayRed
import com.khmer.calendar.ui.theme.background
import java.time.LocalDate

@Composable
fun MonthGridView(
    monthDays: List<KhmerDate>,
    selectedDate: KhmerDate?,
    onSelectDate: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier
) {
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
            KhmerUtils.KHMER_WEEKDAYS.forEachIndexed { index, weekday ->
                val isSelectedDay = index == currentDayOfWeekIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelectedDay) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
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
            // Buddha Day Legend
            Image(
                painter = painterResource(id = R.drawable.ic_sil_day),
                contentDescription = "Buddha Day",
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ថ្ងៃសីល",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.width(24.dp))

            // Holiday Legend
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(HolidayRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ថ្ងៃឈប់សម្រាក",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Holiday Content for Current Month
        val monthHolidays = remember(monthDays) {
            monthDays.filter { it.isCurrentMonth && it.isHoliday && it.holiday != null }
        }

        if (monthHolidays.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "ថ្ងៃឈប់សម្រាកប្រចាំខែ",
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
                        onClick = { onSelectDate(khmerDate) }
                    )
                }
            }
        }
    }
}
