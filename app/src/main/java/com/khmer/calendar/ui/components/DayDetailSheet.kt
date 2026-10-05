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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.R
import com.khmer.calendar.data.model.KhmerDate
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.theme.BuddhaDayGold
import com.khmer.calendar.ui.theme.HolidayRed
import com.khmer.calendar.ui.theme.KhmerRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    khmerDate: KhmerDate?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (khmerDate == null) return

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Gregorian Date Header
            Text(
                text = "ថ្ងៃ${khmerDate.dayOfWeekKhmer} ទី${khmerDate.dayKhmerNumeral} ខែ${khmerDate.monthNameKhmer} ឆ្នាំ${khmerDate.yearKhmerNumeral}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Full Khmer Lunar Date
            Text(
                text = khmerDate.fullKhmerDateFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = KhmerRed
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Buddha Day Card (if applicable)
            if (khmerDate.isBuddhaDay) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = BuddhaDayGold.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_sil_day),
                            modifier = Modifier.size(30.dp),
                            contentDescription = "ថ្ងៃសីល",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ថ្ងៃសីល",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuddhaDayGold
                            )
                            Text(
                                text = khmerDate.buddhaDayTitle ?: "ថ្ងៃសីល",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Holiday Card (if applicable)
            if (khmerDate.isHoliday && khmerDate.holiday != null) {
                val holiday = khmerDate.holiday
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HolidayRed.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(holiday.imageRes),
                            contentDescription = holiday.nameKhmer,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ថ្ងៃឈប់សម្រាក - ${holiday.dateFormatted}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HolidayRed
                            )
                            Text(
                                text = holiday.nameKhmer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (holiday.descriptionKhmer.isNotBlank()) {
                                Text(
                                    text = holiday.descriptionKhmer,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Additional Info Details
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        label = "ពុទ្ធសករាជ (BE):",
                        value = "ព.ស. ${KhmerUtils.toKhmerNumeral(khmerDate.buddhistEra)}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = "ឆ្នាំច៖",
                        value = "${khmerDate.zodiacYear} ${khmerDate.sakName}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "ចន្ទគតិខ្មែរ៖", value = khmerDate.lunarDateFormatted)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
