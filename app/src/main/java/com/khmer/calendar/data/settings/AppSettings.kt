package com.khmer.calendar.data.settings

enum class AppLanguage(
    val code: String,
    val displayNameKm: String,
    val displayNameEn: String,
    val flagEmoji: String
) {
    KHMER("km", "ខ្មែរ", "Khmer", "🇰🇭"),
    ENGLISH("en", "អង់គ្លេស", "English", "🇬🇧")
}

enum class AppThemeMode(
    val displayNameKm: String,
    val displayNameEn: String
) {
    SYSTEM("តាមប្រព័ន្ធ", "System Default"),
    LIGHT("ពន្លឺ", "Light Mode"),
    DARK("ងងឹត", "Dark Mode")
}

enum class FirstDayOfWeekPref(
    val displayNameKm: String,
    val displayNameEn: String
) {
    SUNDAY("ថ្ងៃអាទិត្យ", "Sunday"),
    MONDAY("ថ្ងៃច័ន្ទ", "Monday")
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.KHMER,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val firstDayOfWeek: FirstDayOfWeekPref = FirstDayOfWeekPref.SUNDAY,
    val showLunarDate: Boolean = true,
    val showBuddhaDays: Boolean = true,
    val showHolidays: Boolean = true,
    val enableBuddhaDayReminders: Boolean = true,
    val enableHolidayReminders: Boolean = true
)
