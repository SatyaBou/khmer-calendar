package com.khmer.calendar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CalendarViewMode(val labelKm: String, val labelEn: String) {
    DAY("ថ្ងៃ", "Day"),
    WEEK("សប្តាហ៍", "Week"),
    MONTH("ខែ", "Month"),
    YEAR("ឆ្នាំ", "Year");

    fun getLabel(isKhmer: Boolean): String = if (isKhmer) labelKm else labelEn
}

@Composable
fun LiquidGlassTabBar(
    selectedMode: CalendarViewMode,
    onModeSelected: (CalendarViewMode) -> Unit,
    modifier: Modifier = Modifier,
    isKhmer: Boolean = true
) {
    val modes = CalendarViewMode.entries
    val tabCount = modes.size
    val selectedIndex = selectedMode.ordinal

    val targetBias = if (tabCount > 1) {
        -1f + (2f * selectedIndex / (tabCount - 1))
    } else 0f

    val indicatorBias by animateFloatAsState(
        targetValue = targetBias,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "indicatorBias"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                shape = RoundedCornerShape(28.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(4.dp)
    ) {
        // Animated sliding selection background indicator
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f / tabCount)
                    .align(BiasAlignment(horizontalBias = indicatorBias, verticalBias = 0f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            modes.forEach { mode ->
                val isSelected = mode == selectedMode
                val interactionSource = remember { MutableInteractionSource() }

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.85f
                    ),
                    animationSpec = tween(durationMillis = 200),
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onModeSelected(mode)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.getLabel(isKhmer),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
