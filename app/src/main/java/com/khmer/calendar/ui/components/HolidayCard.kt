package com.khmer.calendar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.data.model.Holiday
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.theme.HolidayRed

@Composable
fun HolidayCard(
    khmerDate: KhmerDate,
    holiday: Holiday,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isKhmer: Boolean = true
) {
    val title = if (isKhmer) holiday.nameKhmer else holiday.descriptionKhmer.ifBlank { holiday.nameKhmer }
    val description = if (isKhmer) {
        if (holiday.descriptionKhmer.isNotBlank() && holiday.descriptionKhmer != holiday.nameKhmer) {
            holiday.descriptionKhmer
        } else {
            "ពិធីបុណ្យ${holiday.nameKhmer}"
        }
    } else {
        holiday.nameKhmer
    }

    val dateStr = if (isKhmer) {
        "${khmerDate.monthNameKhmer} ${khmerDate.dayKhmerNumeral}"
    } else {
        "${KhmerUtils.getSolarMonth(khmerDate.month, false)} ${khmerDate.dayOfMonth}"
    }

    val tagText = if (isKhmer) {
        "ថ្ងៃឈប់សម្រាកសាធារណៈ • ${khmerDate.lunarDateFormatted}"
    } else {
        "Public Holiday • ${khmerDate.lunarDateFormatted}"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Event Image Container
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF6F4EE)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = holiday.imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Right Event Text Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top Date Header in Red
                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HolidayRed
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Event Title
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Subtitle / Description
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )

                // Tag line
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tagText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFC5A059)
                )
            }
        }
    }
}
