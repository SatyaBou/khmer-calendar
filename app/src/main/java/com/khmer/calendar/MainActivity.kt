package com.khmer.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.khmer.calendar.data.util.HolidayJsonParser
import com.khmer.calendar.ui.MainScreen
import com.khmer.calendar.ui.SplashScreen
import com.khmer.calendar.ui.theme.KhmerCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android SplashScreen API to seamlessly bypass default system icon
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Load holidays from assets/holidays.json
        HolidayJsonParser.loadHolidaysFromAssets(applicationContext)

        enableEdgeToEdge()
        setContent {
            KhmerCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var showSplashScreen by remember { mutableStateOf(true) }

                    Crossfade(
                        targetState = showSplashScreen,
                        animationSpec = tween(durationMillis = 600),
                        label = "SplashTransition"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = { showSplashScreen = false }
                            )
                        } else {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
