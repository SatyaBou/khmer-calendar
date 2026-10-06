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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    khmerDate: KhmerDate?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isKhmer: Boolean = true
) {
    if (khmerDate == null) return

    var allowDismiss by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { targetValue ->
            if (targetValue == SheetValue.Hidden) allowDismiss else true
        }
    )

    val headerText = if (isKhmer) {
        "ថ្ងៃ${khmerDate.dayOfWeekKhmer} ទី${khmerDate.dayKhmerNumeral} ខែ${khmerDate.monthNameKhmer} ឆ្នាំ${khmerDate.yearKhmerNumeral}"
    } else {
        "${KhmerUtils.getDayOfWeek(khmerDate.date, false)}, ${khmerDate.dayOfMonth} ${KhmerUtils.getSolarMonth(khmerDate.month, false)} ${khmerDate.year}"
    }

    ModalBottomSheet(
        onDismissRequest = { /* Disable dismiss on outside click / back press */ },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Gregorian & Khmer Lunar Date Header Row with Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = headerText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = khmerDate.fullKhmerDateFormatted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = KhmerRed
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        allowDismiss = true
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

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
                            contentDescription = if (isKhmer) "ថ្ងៃសីល" else "Buddha Day",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isKhmer) "ថ្ងៃសីល" else "Buddha Day",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuddhaDayGold
                            )
                            Text(
                                text = khmerDate.buddhaDayTitle ?: (if (isKhmer) "ថ្ងៃសីល" else "Buddha Day"),
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
                val holidayTitle = if (isKhmer) holiday.nameKhmer else holiday.descriptionKhmer.ifBlank { holiday.nameKhmer }
                val holidayDesc = if (isKhmer) holiday.descriptionKhmer else holiday.nameKhmer

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
                            contentDescription = holidayTitle,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isKhmer) "ថ្ងៃឈប់សម្រាក - ${holiday.dateFormatted}" else "Public Holiday - ${holiday.dateFormatted}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HolidayRed
                            )
                            Text(
                                text = holidayTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (holidayDesc.isNotBlank()) {
                                Text(
                                    text = holidayDesc,
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
                        label = if (isKhmer) "ពុទ្ធសករាជ (BE):" else "Buddhist Era (BE):",
                        value = "ព.ស. ${KhmerUtils.formatNumber(khmerDate.buddhistEra, isKhmer)}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = if (isKhmer) "ឆ្នាំច៖" else "Zodiac Year:",
                        value = "${khmerDate.zodiacYear} ${khmerDate.sakName}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = if (isKhmer) "ចន្ទគតិខ្មែរ៖" else "Khmer Lunar Date:",
                        value = khmerDate.lunarDateFormatted
                    )
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
