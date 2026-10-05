package com.khmer.calendar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.delay

@Composable
fun DayCellView(
    khmerDate: KhmerDate,
    isSelected: Boolean,
    onSelect: (KhmerDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = khmerDate.isToday
    val isCurrentMonth = khmerDate.isCurrentMonth

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "dayCellScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isToday -> KhmerGold
            isSelected -> KhmerGold.copy(alpha = 0.25f)
            khmerDate.isHoliday -> HolidayRed.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "dayCellBgColor"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isToday -> Color.White
            !isCurrentMonth -> Color.Gray.copy(alpha = 0.4f)
            khmerDate.isHoliday -> HolidayRed
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "dayCellTextColor"
    )

    val borderModifier = if (isToday && isSelected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
    } else if (isSelected) {
        Modifier.border(1.5.dp, KhmerGold, RoundedCornerShape(8.dp))
    } else {
        Modifier
    }

    val lunarPhaseText =
        "${KhmerUtils.toKhmerNumeral(khmerDate.lunarDay)}${if (khmerDate.isWaxing) "កើត" else "រោច"}"

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(120)
            isPressed = false
        }
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(borderModifier)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onSelect(khmerDate)
            },
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
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Khmer Lunar Phase Day Text
            Text(
                text = lunarPhaseText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = if (isToday) Color.White.copy(alpha = 0.9f) else textColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
