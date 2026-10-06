package com.khmer.calendar.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val langCode = prefs.getString(KEY_LANGUAGE, AppLanguage.KHMER.code) ?: AppLanguage.KHMER.code
        val language = AppLanguage.entries.find { it.code == langCode } ?: AppLanguage.KHMER

        val themeStr = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val themeMode = AppThemeMode.entries.find { it.name == themeStr } ?: AppThemeMode.SYSTEM

        val firstDayStr = prefs.getString(KEY_FIRST_DAY, FirstDayOfWeekPref.SUNDAY.name) ?: FirstDayOfWeekPref.SUNDAY.name
        val firstDayOfWeek = FirstDayOfWeekPref.entries.find { it.name == firstDayStr } ?: FirstDayOfWeekPref.SUNDAY

        val showLunarDate = prefs.getBoolean(KEY_SHOW_LUNAR, true)
        val showBuddhaDays = prefs.getBoolean(KEY_SHOW_BUDDHA_DAYS, true)
        val showHolidays = prefs.getBoolean(KEY_SHOW_HOLIDAYS, true)
        val enableBuddhaDayReminders = prefs.getBoolean(KEY_REMINDER_BUDDHA, true)
        val enableHolidayReminders = prefs.getBoolean(KEY_REMINDER_HOLIDAY, true)

        return AppSettings(
            language = language,
            themeMode = themeMode,
            firstDayOfWeek = firstDayOfWeek,
            showLunarDate = showLunarDate,
            showBuddhaDays = showBuddhaDays,
            showHolidays = showHolidays,
            enableBuddhaDayReminders = enableBuddhaDayReminders,
            enableHolidayReminders = enableHolidayReminders
        )
    }

    fun updateLanguage(language: AppLanguage) {
        prefs.edit { putString(KEY_LANGUAGE, language.code) }
        _settings.value = _settings.value.copy(language = language)
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, themeMode.name).apply()
        _settings.value = _settings.value.copy(themeMode = themeMode)
    }

    fun updateFirstDayOfWeek(firstDayOfWeek: FirstDayOfWeekPref) {
        prefs.edit().putString(KEY_FIRST_DAY, firstDayOfWeek.name).apply()
        _settings.value = _settings.value.copy(firstDayOfWeek = firstDayOfWeek)
    }

    fun updateShowLunarDate(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LUNAR, show).apply()
        _settings.value = _settings.value.copy(showLunarDate = show)
    }

    fun updateShowBuddhaDays(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_BUDDHA_DAYS, show).apply()
        _settings.value = _settings.value.copy(showBuddhaDays = show)
    }

    fun updateShowHolidays(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_HOLIDAYS, show).apply()
        _settings.value = _settings.value.copy(showHolidays = show)
    }

    fun updateBuddhaDayReminders(enable: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_BUDDHA, enable).apply()
        _settings.value = _settings.value.copy(enableBuddhaDayReminders = enable)
    }

    fun updateHolidayReminders(enable: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_HOLIDAY, enable).apply()
        _settings.value = _settings.value.copy(enableHolidayReminders = enable)
    }

    companion object {
        private const val PREFS_NAME = "khmer_calendar_settings"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_THEME = "key_theme"
        private const val KEY_FIRST_DAY = "key_first_day"
        private const val KEY_SHOW_LUNAR = "key_show_lunar"
        private const val KEY_SHOW_BUDDHA_DAYS = "key_show_buddha_days"
        private const val KEY_SHOW_HOLIDAYS = "key_show_holidays"
        private const val KEY_REMINDER_BUDDHA = "key_reminder_buddha"
        private const val KEY_REMINDER_HOLIDAY = "key_reminder_holiday"
    }
}
