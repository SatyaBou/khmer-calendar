package com.khmer.calendar.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khmer.calendar.R
import com.khmer.calendar.ui.theme.KhmerCalendarTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val SplashBgColor = Color(0xFF3B111C)
val SplashGoldColor = Color(0xFFE5C178)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    val alphaAnim = remember { Animatable(if (isPreview) 1f else 0f) }
    val scaleAnim = remember { Animatable(if (isPreview) 1f else 0.85f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }

        delay(2200L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBgColor)
    ) {
        // Center Content with Scale and Alpha Entrance Animation
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon Card
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Khmer Calendar Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Title Khmer
            Text(
                text = "ប្រតិទិនខ្មែរ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle English
            Text(
                text = "KHMER CALENDAR",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = SplashGoldColor,
                textAlign = TextAlign.Center
            )
        }

        // Bottom Content with Alpha Fade Entrance Animation
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flower),
                contentDescription = null,
                tint = SplashGoldColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ប្រពៃណី និងពេលវេលា នៅជិតអ្នក",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = SplashGoldColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    KhmerCalendarTheme {
        SplashScreen()
    }
}
