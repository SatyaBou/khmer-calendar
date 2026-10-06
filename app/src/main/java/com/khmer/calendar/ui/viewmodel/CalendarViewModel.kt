package com.khmer.calendar.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.khmer.calendar.data.repository.KhmerCalendarRepository
import com.khmer.calendar.data.repository.KhmerCalendarRepositoryImpl
import com.khmer.calendar.data.settings.SettingsRepository
import com.khmer.calendar.ui.model.CalendarEffect
import com.khmer.calendar.ui.model.CalendarIntent
import com.khmer.calendar.ui.model.CalendarState
import com.khmer.calendar.ui.model.CalendarTab
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: KhmerCalendarRepository = KhmerCalendarRepositoryImpl(),
    private val settingsRepository: SettingsRepository = SettingsRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CalendarState())
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CalendarEffect>()
    val effect: SharedFlow<CalendarEffect> = _effect.asSharedFlow()

    init {
        loadDataForCurrentMonthAndYear()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { newSettings ->
                _uiState.update { it.copy(settings = newSettings) }
            }
        }
    }

    fun processIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.SelectDate -> {
                _uiState.update {
                    it.copy(
                        selectedDate = intent.khmerDate,
                        isDayDetailSheetVisible = true
                    )
                }
            }
            is CalendarIntent.NextMonth -> navigateMonth(1)
            is CalendarIntent.PreviousMonth -> navigateMonth(-1)
            is CalendarIntent.GoToToday -> goToToday()
            is CalendarIntent.SelectTab -> {
                _uiState.update { it.copy(currentTab = intent.tab) }
            }
            is CalendarIntent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
            }
            is CalendarIntent.DismissDayDetailSheet -> {
                _uiState.update { it.copy(isDayDetailSheetVisible = false) }
            }
            is CalendarIntent.SelectYear -> {
                _uiState.update { it.copy(currentYear = intent.year) }
                loadDataForCurrentMonthAndYear()
                viewModelScope.launch {
                    _effect.emit(CalendarEffect.ScrollToMonth(intent.year, _uiState.value.currentMonth))
                }
            }
            is CalendarIntent.ToggleSettingsSheet -> {
                _uiState.update { it.copy(isSettingsSheetVisible = intent.show) }
            }
            is CalendarIntent.UpdateLanguage -> {
                settingsRepository.updateLanguage(intent.language)
            }
            is CalendarIntent.UpdateThemeMode -> {
                settingsRepository.updateThemeMode(intent.themeMode)
            }
            is CalendarIntent.UpdateFirstDayOfWeek -> {
                settingsRepository.updateFirstDayOfWeek(intent.firstDay)
            }
            is CalendarIntent.UpdateShowLunarDate -> {
                settingsRepository.updateShowLunarDate(intent.show)
            }
            is CalendarIntent.UpdateShowBuddhaDays -> {
                settingsRepository.updateShowBuddhaDays(intent.show)
            }
            is CalendarIntent.UpdateShowHolidays -> {
                settingsRepository.updateShowHolidays(intent.show)
            }
            is CalendarIntent.UpdateBuddhaDayReminders -> {
                settingsRepository.updateBuddhaDayReminders(intent.enable)
            }
            is CalendarIntent.UpdateHolidayReminders -> {
                settingsRepository.updateHolidayReminders(intent.enable)
            }
        }
    }

    private fun navigateMonth(delta: Int) {
        val currentState = _uiState.value
        var newMonth = currentState.currentMonth + delta
        var newYear = currentState.currentYear

        if (newMonth > 12) {
            newMonth = 1
            newYear++
        } else if (newMonth < 1) {
            newMonth = 12
            newYear--
        }

        _uiState.update {
            it.copy(
                currentYear = newYear,
                currentMonth = newMonth
            )
        }
        loadDataForCurrentMonthAndYear()

        viewModelScope.launch {
            _effect.emit(CalendarEffect.ScrollToMonth(newYear, newMonth))
        }
    }

    private fun goToToday() {
        val today = LocalDate.now()
        val todayKhmerDate = repository.getKhmerDate(today)

        _uiState.update {
            it.copy(
                currentYear = today.year,
                currentMonth = today.monthValue,
                selectedDate = todayKhmerDate,
                currentTab = CalendarTab.CALENDAR,
                isDayDetailSheetVisible = false
            )
        }
        loadDataForCurrentMonthAndYear()

        viewModelScope.launch {
            _effect.emit(CalendarEffect.ScrollToMonth(today.year, today.monthValue))
        }
    }

    private fun loadDataForCurrentMonthAndYear() {
        val year = _uiState.value.currentYear
        val month = _uiState.value.currentMonth

        _uiState.update { it.copy(isLoading = true) }

        val monthDays = repository.getMonthDays(year, month)
        val holidays = repository.getHolidays(year)
        val buddhaDays = repository.getBuddhaDays(year)

        _uiState.update {
            it.copy(
                monthDays = monthDays,
                yearHolidays = holidays,
                yearBuddhaDays = buddhaDays,
                isLoading = false
            )
        }
    }
}
