package com.khmer.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerCalendarCalculator
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.theme.HolidayRed
import com.khmer.calendar.ui.theme.KhmerGold

@Composable
fun YearGridView(
    year: Int,
    selectedDate: KhmerDate?,
    onSelectDate: (KhmerDate) -> Unit,
    onSelectMonth: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Render 12 months in 6 rows of 2 columns
        for (rowIndex in 0 until 6) {
            val month1 = rowIndex * 2 + 1
            val month2 = rowIndex * 2 + 2

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // First Month Card
                Box(modifier = Modifier.weight(1f)) {
                    SmallMonthCard(
                        year = year,
                        month = month1,
                        selectedDate = selectedDate,
                        onSelectDate = onSelectDate,
                        onSelectMonth = { onSelectMonth(month1) }
                    )
                }

                // Second Month Card
                Box(modifier = Modifier.weight(1f)) {
                    SmallMonthCard(
                        year = year,
                        month = month2,
                        selectedDate = selectedDate,
                        onSelectDate = onSelectDate,
                        onSelectMonth = { onSelectMonth(month2) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallMonthCard(
    year: Int,
    month: Int,
    selectedDate: KhmerDate?,
    onSelectDate: (KhmerDate) -> Unit,
    onSelectMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthDays = remember(year, month) {
        KhmerCalendarCalculator.getMonthGridDays(year, month)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // Month Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSelectMonth() }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ខែ${KhmerUtils.getSolarMonthKhmer(month)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Month",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Weekdays Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                KhmerUtils.KHMER_WEEKDAYS_SHORT.forEach { weekday ->
                    Text(
                        text = weekday,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Days Grid
            Column(modifier = Modifier.fillMaxWidth()) {
                monthDays.chunked(7).forEach { weekDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEach { khmerDate ->
                            SmallDayCell(
                                khmerDate = khmerDate,
                                isSelected = selectedDate?.date == khmerDate.date,
                                onSelectDate = onSelectDate,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallDayCell(
    khmerDate: KhmerDate,
    isSelected: Boolean,
    onSelectDate: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!khmerDate.isCurrentMonth) {
        Box(modifier = modifier.height(22.dp))
        return
    }

    val isToday = khmerDate.isToday

    val backgroundColor = when {
        isToday -> KhmerGold
        isSelected -> KhmerGold.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val textColor = when {
        isToday -> Color.White
        khmerDate.isHoliday -> HolidayRed
        khmerDate.isBuddhaDay -> Color(0xFFFF8C00)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .height(22.dp)
            .clickable { onSelectDate(khmerDate) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = khmerDate.dayKhmerNumeral,
                fontSize = 9.sp,
                fontWeight = if (isToday || isSelected || khmerDate.isHoliday || khmerDate.isBuddhaDay) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
