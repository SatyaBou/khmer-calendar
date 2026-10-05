package com.khmer.calendar.ui

import android.R
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.khmer.calendar.ui.components.DayView
import com.khmer.calendar.ui.components.LiquidGlassTabBar
import com.khmer.calendar.ui.components.MonthGridView
import com.khmer.calendar.ui.components.WeekView
import com.khmer.calendar.ui.components.YearGridView
import com.khmer.calendar.ui.model.CalendarEffect
import com.khmer.calendar.ui.model.CalendarIntent
import com.khmer.calendar.ui.theme.KhmerGold
import com.khmer.calendar.ui.viewmodel.CalendarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedViewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    val coroutineScope = rememberCoroutineScope()

    val initialPage = remember {
        (state.currentYear - 2000) * 12 + (state.currentMonth - 1)
    }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 2400 }
    )

    var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }
    var slideDirection by remember { mutableIntStateOf(1) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage > previousPage) {
            slideDirection = 1
        } else if (pagerState.currentPage < previousPage) {
            slideDirection = -1
        }
        previousPage = pagerState.currentPage
    }

    // Listen for Side Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CalendarEffect.ShowToast -> {
               //     Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is CalendarEffect.ScrollToDate -> {
                    val targetPage = (effect.khmerDate.date.year - 2000) * 12 + (effect.khmerDate.date.monthValue - 1)
                    if (targetPage in 0..2399) {
                        smoothScrollToPage(pagerState, targetPage)
                    }
                }
                is CalendarEffect.ScrollToMonth -> {
                    val targetPage = (effect.year - 2000) * 12 + (effect.month - 1)
                    if (targetPage in 0..2399) {
                        smoothScrollToPage(pagerState, targetPage)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
               // .statusBarsPadding()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top App Header showing App Title & Current Day and Month Badge
                    TopHeaderRow(
                        onGoToToday = {
                            selectedViewMode = CalendarViewMode.MONTH
                            viewModel.processIntent(CalendarIntent.GoToToday)
                        }
                    )

                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = selectedViewMode != CalendarViewMode.YEAR,
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapAnimationSpec = tween(
                                durationMillis = 350,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        val year = 2000 + page / 12
                        val month = (page % 12) + 1

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Header with Navigation
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavigationIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetPage = if (selectedViewMode == CalendarViewMode.YEAR) {
                                                ((year - 1 - 2000) * 12 + (month - 1)).coerceAtLeast(0)
                                            } else {
                                                (pagerState.currentPage - 1).coerceAtLeast(0)
                                            }
                                            smoothScrollToPage(pagerState, targetPage)
                                        }
                                    },
                                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous"
                                )

                                val titleText = if (selectedViewMode == CalendarViewMode.YEAR) {
                                    "ឆ្នាំ${KhmerUtils.toKhmerNumeral(year)}"
                                } else {
                                    "ខែ${KhmerUtils.getSolarMonthKhmer(month)} ឆ្នាំ${KhmerUtils.toKhmerNumeral(year)}"
                                }

                                AnimatedContent(
                                    targetState = titleText,
                                    transitionSpec = {
                                        if (slideDirection > 0) {
                                            (slideInHorizontally { width -> width / 2 } + fadeIn(tween(250)))
                                                .togetherWith(slideOutHorizontally { width -> -width / 2 } + fadeOut(tween(200)))
                                        } else {
                                            (slideInHorizontally { width -> -width / 2 } + fadeIn(tween(250)))
                                                .togetherWith(slideOutHorizontally { width -> width / 2 } + fadeOut(tween(200)))
                                        }
                                    },
                                    label = "HeaderTitleTransition"
                                ) { text ->
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                NavigationIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val targetPage = if (selectedViewMode == CalendarViewMode.YEAR) {
                                                ((year + 1 - 2000) * 12 + (month - 1)).coerceAtMost(2399)
                                            } else {
                                                (pagerState.currentPage + 1).coerceAtMost(2399)
                                            }
                                            smoothScrollToPage(pagerState, targetPage)
                                        }
                                    },
                                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next"
                                )
                            }

                            // Custom Tab Bar
                            LiquidGlassTabBar(
                                selectedMode = selectedViewMode,
                                onModeSelected = { selectedViewMode = it }
                            )

                            // Mode Content with smooth transition (scaleIn 0.92f + fadeIn)
                            AnimatedContent(
                                targetState = selectedViewMode,
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300)))
                                        .togetherWith(fadeOut(animationSpec = tween(200)))
                                },
                                label = "ViewModeTransition"
                            ) { mode ->
                                AnimatedContent(
                                    targetState = page,
                                    transitionSpec = {
                                        if (slideDirection > 0) {
                                            (slideInHorizontally { width -> width / 2 } + fadeIn(tween(250)))
                                                .togetherWith(slideOutHorizontally { width -> -width / 2 } + fadeOut(tween(200)))
                                        } else {
                                            (slideInHorizontally { width -> -width / 2 } + fadeIn(tween(250)))
                                                .togetherWith(slideOutHorizontally { width -> width / 2 } + fadeOut(tween(200)))
                                        }
                                    },
                                    label = "PageSlideTransition"
                                ) { targetPage ->
                                    val targetYear = 2000 + targetPage / 12
                                    val targetMonth = (targetPage % 12) + 1
                                    val targetMonthDays = remember(targetPage) {
                                        KhmerCalendarCalculator.getMonthGridDays(targetYear, targetMonth)
                                    }

                                    when (mode) {
                                        CalendarViewMode.DAY -> {
                                            val dayToDisplay = state.selectedDate
                                                ?: targetMonthDays.find { it.isToday }
                                                ?: targetMonthDays.firstOrNull()
                                            DayView(selectedDate = dayToDisplay)
                                        }
                                        CalendarViewMode.WEEK -> {
                                            WeekView(
                                                monthDays = targetMonthDays,
                                                selectedDate = state.selectedDate,
                                                onSelectDate = { khmerDate ->
                                                    viewModel.processIntent(CalendarIntent.SelectDate(khmerDate))
                                                }
                                            )
                                        }
                                        CalendarViewMode.MONTH -> {
                                            MonthGridView(
                                                monthDays = targetMonthDays,
                                                selectedDate = state.selectedDate,
                                                onSelectDate = { khmerDate ->
                                                    viewModel.processIntent(CalendarIntent.SelectDate(khmerDate))
                                                }
                                            )
                                        }
                                        CalendarViewMode.YEAR -> {
                                            YearGridView(
                                                year = targetYear,
                                                selectedDate = state.selectedDate,
                                                onSelectDate = { khmerDate ->
                                                    viewModel.processIntent(CalendarIntent.SelectDate(khmerDate))
                                                },
                                                onSelectMonth = { targetMonth ->
                                                    val newPage = (targetYear - 2000) * 12 + (targetMonth - 1)
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage(newPage)
                                                        selectedViewMode = CalendarViewMode.MONTH
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
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

@Composable
private fun TopHeaderRow(
    onGoToToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val dayOfWeek = remember(today) { KhmerUtils.getDayOfWeekKhmer(today) }
    val dayNumeral = remember(today) { KhmerUtils.toKhmerNumeral(today.dayOfMonth) }
    val monthName = remember(today) { KhmerUtils.getSolarMonthKhmer(today.monthValue) }

    val todayFormatted = "ថ្ងៃ$dayOfWeek ទី$dayNumeral $monthName"

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "todayButtonScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ប្រតិទិនខ្មែរ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            onClick = {
                isPressed = true
                onGoToToday()
            },
            shape = RoundedCornerShape(20.dp),
            color = KhmerGold.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, KhmerGold.copy(alpha = 0.6f)),
            tonalElevation = 2.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(120)
                    isPressed = false
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Go to Today",
                    modifier = Modifier.size(16.dp),
                    tint = KhmerGold
                )
                Text(
                    text = todayFormatted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KhmerGold
                )
            }
        }
    }
}

@Composable
private fun NavigationIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    IconButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .size(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(38.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

private suspend fun smoothScrollToPage(
    pagerState: PagerState,
    targetPage: Int
) {
    if (pagerState.currentPage == targetPage) return
    pagerState.scrollToPage(targetPage)
}
