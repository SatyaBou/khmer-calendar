package com.khmer.calendar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.R
import com.khmer.calendar.data.settings.AppLanguage
import com.khmer.calendar.data.settings.AppSettings
import com.khmer.calendar.data.settings.AppThemeMode
import com.khmer.calendar.data.settings.FirstDayOfWeekPref
import com.khmer.calendar.ui.theme.KhmerGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onFirstDayChange: (FirstDayOfWeekPref) -> Unit,
    onShowLunarChange: (Boolean) -> Unit,
    onShowBuddhaDaysChange: (Boolean) -> Unit,
    onShowHolidaysChange: (Boolean) -> Unit,
    onBuddhaDayRemindersChange: (Boolean) -> Unit,
    onHolidayRemindersChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var allowDismiss by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { targetValue ->
            if (targetValue == SheetValue.Hidden) allowDismiss else true
        }
    )
    val isKhmer = settings.language == AppLanguage.KHMER

    ModalBottomSheet(
        onDismissRequest = { /* Disable dismiss on outside click / back press */ },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isKhmer) "ការកំណត់" else "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                       // .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Language Preference
            SectionHeader(title = if (isKhmer) "ភាសា / Language" else "Language / ភាសា")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = settings.language == lang
                        SelectableCardOption(
                            text = "${lang.flagEmoji} ${if (isKhmer) lang.displayNameKm else lang.displayNameEn}",
                            isSelected = isSelected,
                            onClick = { onLanguageChange(lang) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Theme Preference
            SectionHeader(title = if (isKhmer) "ស្បែក និង រូបរាង" else "Theme & Appearance")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        val label = if (isKhmer) mode.displayNameKm else mode.displayNameEn
                        SelectableCardOption(
                            text = label,
                            isSelected = isSelected,
                            onClick = { onThemeChange(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. First Day of Week
            SectionHeader(title = if (isKhmer) "ថ្ងៃចាប់ផ្តើមសប្តាហ៍" else "First Day of Week")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FirstDayOfWeekPref.entries.forEach { pref ->
                        val isSelected = settings.firstDayOfWeek == pref
                        val label = if (isKhmer) pref.displayNameKm else pref.displayNameEn
                        SelectableCardOption(
                            text = label,
                            isSelected = isSelected,
                            onClick = { onFirstDayChange(pref) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Display Options
            SectionHeader(title = if (isKhmer) "ជម្រើសបង្ហាញ" else "Display Options")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SettingSwitchRow(
                        title = if (isKhmer) "បង្ហាញថ្ងៃចន្ទគតិ" else "Show Lunar Date",
                        subtitle = if (isKhmer) "បង្ហាញថ្ងៃកើត ឬ ថ្ងៃរោចនៅលើក្រឡា" else "Show lunar phase days on grid",
                        checked = settings.showLunarDate,
                        onCheckedChange = onShowLunarChange
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    SettingSwitchRow(
                        title = if (isKhmer) "បង្ហាញថ្ងៃសីល" else "Show Buddha Days",
                        subtitle = if (isKhmer) "បង្ហាញរូបសញ្ញាថ្ងៃសីលនៅលើក្រឡា" else "Show Buddha day icons on grid",
                        checked = settings.showBuddhaDays,
                        onCheckedChange = onShowBuddhaDaysChange
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    SettingSwitchRow(
                        title = if (isKhmer) "បង្ហាញថ្ងៃឈប់សម្រាក" else "Show Public Holidays",
                        subtitle = if (isKhmer) "បង្ហាញព័ត៌មានថ្ងៃឈប់សម្រាក" else "Show holiday markers on calendar",
                        checked = settings.showHolidays,
                        onCheckedChange = onShowHolidaysChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Reminders & Notifications
            SectionHeader(title = if (isKhmer) "ការរំលឹក" else "Reminders")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SettingSwitchRow(
                        title = if (isKhmer) "ការរំលឹកថ្ងៃសីល" else "Buddha Day Reminders",
                        subtitle = if (isKhmer) "ទទួលការជូនដំណឹងនៅពេលជិតដល់ថ្ងៃសីល" else "Get notified before Buddha days",
                        checked = settings.enableBuddhaDayReminders,
                        onCheckedChange = onBuddhaDayRemindersChange
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    SettingSwitchRow(
                        title = if (isKhmer) "ការរំលឹកថ្ងៃឈប់សម្រាក" else "Holiday Reminders",
                        subtitle = if (isKhmer) "ទទួលការជូនដំណឹងនៅពេលដល់ថ្ងៃឈប់សម្រាក" else "Get notified for public holidays",
                        checked = settings.enableHolidayReminders,
                        onCheckedChange = onHolidayRemindersChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. About App Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isKhmer) "ប្រតិទិនខ្មែរ" else "Khmer Calendar",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isKhmer) "កំណែ ១.០.០ (1.0.0)" else "Version 1.0.0",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isKhmer)
                                "កម្មវិធីប្រតិទិនចន្ទគតិ និង សូរិយគតិខ្មែរ សម្រាប់មើលថ្ងៃសីល និង ថ្ងៃឈប់សម្រាកសាធារណៈ។"
                            else
                                "Khmer Solar and Lunar Calendar application with holidays and Buddha days.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SelectableCardOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) KhmerGold else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(200),
        label = "textColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) KhmerGold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = KhmerGold
            )
        )
    }
}
