package com.khmer.calendar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.ui.theme.BuddhaDayGold
import com.khmer.calendar.ui.theme.HolidayRed

@Composable
fun DayView(
    selectedDate: KhmerDate?,
    modifier: Modifier = Modifier
) {
    val dateToShow = selectedDate ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Day Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ថ្ងៃ${dateToShow.dayOfWeekKhmer}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dateToShow.dayKhmerNumeral,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ខែ${dateToShow.monthNameKhmer} ឆ្នាំ${dateToShow.yearKhmerNumeral}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lunar Details Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "ព័ត៌មានប្រតិទិនចន្ទគតិ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "ថ្ងៃចន្ទគតិ", value = dateToShow.lunarDateFormatted)
                DetailRow(label = "ពុទ្ធសករាជ", value = "ព.ស. ${com.khmer.calendar.data.util.KhmerUtils.toKhmerNumeral(dateToShow.buddhistEra)}")
                DetailRow(label = "ឆ្នាំចល័ត", value = "${dateToShow.zodiacYear} ${dateToShow.sakName}")
            }
        }

        // Holiday Banner
        dateToShow.holiday?.let { holiday ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = HolidayRed.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(holiday.imageRes),
                        contentDescription = holiday.nameKhmer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = holiday.nameKhmer,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HolidayRed
                        )
                        if (holiday.descriptionKhmer.isNotBlank()) {
                            Text(
                                text = holiday.descriptionKhmer,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Buddha Day Banner
        if (dateToShow.isBuddhaDay) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BuddhaDayGold.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "☸️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = dateToShow.buddhaDayTitle ?: "ថ្ងៃសីល",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuddhaDayGold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
