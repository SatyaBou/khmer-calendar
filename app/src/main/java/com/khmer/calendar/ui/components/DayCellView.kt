package com.khmer.calendar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.khmer.calendar.ui.theme.KhmerGold

@Composable
fun DayCellView(
    khmerDate: KhmerDate,
    isSelected: Boolean,
    onSelect: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = khmerDate.isToday
    val isCurrentMonth = khmerDate.isCurrentMonth

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        isToday -> KhmerGold.copy(alpha = 0.5f)
        khmerDate.isHoliday -> HolidayRed.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    val textColor = when {
        !isCurrentMonth -> Color.Gray.copy(alpha = 0.4f)
        khmerDate.isHoliday -> HolidayRed
        else -> MaterialTheme.colorScheme.onSurface
    }

    val lunarPhaseText =
        "${KhmerUtils.toKhmerNumeral(khmerDate.lunarDay)}${if (khmerDate.isWaxing) "កើត" else "រោច"}"

    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onSelect(khmerDate) },
        contentAlignment = Alignment.Center
    ) {
        if (khmerDate.isBuddhaDay && isCurrentMonth) {
            Image(
                painter = painterResource(id = R.drawable.ic_sil_day),
                contentDescription = "Buddha Day",
                modifier = Modifier
                    .size(20.dp)
                    .padding(4.dp),
                alpha = 0.80f,
                contentScale = ContentScale.Fit
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Solar Day Numeral
            Text(
                text = khmerDate.dayKhmerNumeral,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Khmer Lunar Phase Day Text
            Text(
                text = lunarPhaseText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))


        }
    }
}
