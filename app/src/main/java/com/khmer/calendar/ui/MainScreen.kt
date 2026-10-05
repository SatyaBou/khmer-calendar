package com.khmer.calendar.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khmer.calendar.data.util.KhmerCalendarCalculator
import com.khmer.calendar.data.util.KhmerUtils
import com.khmer.calendar.ui.components.CalendarViewMode
import com.khmer.calendar.ui.components.DayDetailSheet
import com.khmer.calendar.ui.components.LiquidGlassTabBar
import com.khmer.calendar.ui.components.MonthGridView
import com.khmer.calendar.ui.model.CalendarEffect
import com.khmer.calendar.ui.model.CalendarIntent
import com.khmer.calendar.ui.viewmodel.CalendarViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedViewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }

    // Listen for Side Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CalendarEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is CalendarEffect.ScrollToDate -> {
                    // Scroll handling if needed
                }
            }
        }
    }

    val initialPage = remember {
        (state.currentYear - 2000) * 12 + (state.currentMonth - 1)
    }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 2400 }
    )

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val year = 2000 + page / 12
                    val month = (page % 12) + 1

                    val monthDays = remember(page) {
                        KhmerCalendarCalculator.getMonthGridDays(year, month)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Month & Year Header
                        Text(
                            text = "ខែ${KhmerUtils.getSolarMonthKhmer(month)} ឆ្នាំ${KhmerUtils.toKhmerNumeral(year)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )

                        // Custom Tab Bar
                        LiquidGlassTabBar(
                            selectedMode = selectedViewMode,
                            onModeSelected = { selectedViewMode = it }
                        )

                        MonthGridView(
                            monthDays = monthDays,
                            selectedDate = state.selectedDate,
                            onSelectDate = { khmerDate ->
                                viewModel.processIntent(CalendarIntent.SelectDate(khmerDate))
                            }
                        )
                    }
                }
            }

            // Day Details Sheet
            if (state.isDayDetailSheetVisible) {
                DayDetailSheet(
                    khmerDate = state.selectedDate,
                    onDismiss = {
                        viewModel.processIntent(CalendarIntent.DismissDayDetailSheet)
                    }
                )
            }
        }
    }
}
