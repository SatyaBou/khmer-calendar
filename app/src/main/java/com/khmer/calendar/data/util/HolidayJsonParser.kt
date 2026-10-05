package com.khmer.calendar.data.util

import android.content.Context
import com.khmer.calendar.data.model.Holiday
import java.io.InputStream
import java.time.LocalDate

data class HolidayRule(
    val id: String,
    val nameEn: String,
    val nameKm: String,
    val type: String, // "fixed" or "lunar"
    val month: Int = 0,
    val day: Int = 0,
    val lunarMonth: String = "",
    val phase: String = "", // "KERT" or "ROCH"
    val lunarDay: Int = 0,
    val days: Int = 1,
    val imageName: String = ""
)

data class HolidayExtra(
    val id: String = "",
    val dateStr: String, // "YYYY-MM-DD"
    val nameEn: String,
    val nameKm: String,
    val imageName: String = ""
)

object HolidayJsonParser {

    private var cachedRules: List<HolidayRule>? = null
    private var cachedExtras: List<HolidayExtra>? = null

    // Default Khmer name mappings for holiday IDs in JSON
    private val KHMER_NAME_MAP = mapOf(
        "intl_new_year" to "ទិវាចូលឆ្នាំសកល",
        "victory_day" to "ទិវាជ័យជម្នះលើរបបប្រល័យពូជសាសន៍",
        "womens_day" to "ទិវានារីអន្តរជាតិ",
        "khmer_new_year" to "ពិធីបុណ្យចូលឆ្នាំថ្មី ប្រពៃណីជាតិ",
        "labour_day" to "ទិវាពលកម្មអន្តរជាតិ",
        "king_birthday" to "ព្រះរាជពិធីបុណ្យចំរើនព្រះជន្ម ព្រះករុណា ព្រះបាទសម្តេចព្រះបរមនាថ នរោត្តម សីហមុនី",
        "queen_mother_birthday" to "ព្រះរាជពិធីបុណ្យចំរើនព្រះជន្ម សម្តេចព្រះមហាក្សត្រី នរោត្តម មុនិនាថ សីហនុ",
        "constitution_day" to "ទិវារដ្ឋធម្មនុញ្ញ",
        "king_father_commemoration" to "ទិវាប្រារព្ធពិធីគោរពព្រះវិញ្ញាណក្ខន្ធ ព្រះករុណា ព្រះបាទសម្តេចព្រះ នរោត្តម សីហនុ",
        "coronation_day" to "ព្រះរាជពិធីគ្រងព្រះបរមរាជសម្បត្តិ ព្រះករុណា ព្រះបាទសម្តេចព្រះបរមនាថ នរោត្តម សីហមុនី",
        "independence_day" to "ពិធីបុណ្យឯករាជ្យជាតិ",
        "meak_bochea" to "ពិធីបុណ្យមាឃបូជា",
        "visak_bochea" to "ពិធីបុណ្យវិសាខបូជា",
        "plowing_ceremony" to "ព្រះរាជពិធីច្រត់ព្រះនង្គ័ល",
        "pchum_ben" to "ពិធីបុណ្យភ្ជុំបិណ្ឌ",
        "bon_om_touk" to "ព្រះរាជពិធីបុណ្យអុំទូក បណ្តែតប្រទីប និងសំពះព្រះខែ អកអំបុក"
    )

    private val LUNAR_MONTH_INDEX_MAP = mapOf(
        "MIKASIR" to 0,
        "PUSS" to 1,
        "MEAK" to 2,
        "PHALKUN" to 3,
        "CHET" to 4,
        "PISAKH" to 5,
        "CHES" to 6,
        "ASATH" to 7,
        "SRAPUN" to 8,
        "BHADRAPADA" to 9,
        "BHAPROBOT" to 9,
        "ASOCH" to 10,
        "KADEUK" to 11
    )

    fun loadHolidaysFromAssets(context: Context) {
        if (cachedRules != null) return
        try {
            val inputStream: InputStream = context.assets.open("holidays.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            parseJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parseJson(jsonString: String) {
        val rulesList = mutableListOf<HolidayRule>()
        val extrasList = mutableListOf<HolidayExtra>()

        try {
            val rootObject = org.json.JSONObject(jsonString)
            val rulesArray = rootObject.optJSONArray("rules")
            if (rulesArray != null) {
                for (i in 0 until rulesArray.length()) {
                    val obj = rulesArray.getJSONObject(i)
                    val id = obj.optString("id", "")
                    val nameEn = obj.optString("nameEn", "")
                    val nameKm = obj.optString("nameKm", KHMER_NAME_MAP[id] ?: nameEn)

                    val type = obj.optString("type", "fixed")
                    val month = obj.optInt("month", 0)
                    val day = obj.optInt("day", 0)
                    val lunarMonth = obj.optString("lunarMonth", "")
                    val phase = obj.optString("phase", "")
                    val lunarDay = obj.optInt("lunarDay", 0)
                    val days = obj.optInt("days", 1)
                    val imageName = obj.optString("imageName", obj.optString("image", ""))

                    rulesList.add(
                        HolidayRule(
                            id = id,
                            nameEn = nameEn,
                            nameKm = nameKm,
                            type = type,
                            month = month,
                            day = day,
                            lunarMonth = lunarMonth,
                            phase = phase,
                            lunarDay = lunarDay,
                            days = days,
                            imageName = imageName
                        )
                    )
                }
            }

            val extrasArray = rootObject.optJSONArray("extras")
            if (extrasArray != null) {
                for (i in 0 until extrasArray.length()) {
                    val obj = extrasArray.getJSONObject(i)
                    val id = obj.optString("id", "")
                    val dateStr = obj.optString("date", "")
                    val nameEn = obj.optString("nameEn", "")
                    val nameKm = obj.optString("nameKm", nameEn)
                    val imageName = obj.optString("imageName", obj.optString("image", ""))

                    extrasList.add(
                        HolidayExtra(
                            id = id,
                            dateStr = dateStr,
                            nameEn = nameEn,
                            nameKm = nameKm,
                            imageName = imageName
                        )
                    )
                }
            }
        } catch (_: Throwable) {
            parseJsonFallback(jsonString, rulesList, extrasList)
        }

        if (rulesList.isEmpty() && extrasList.isEmpty()) {
            parseJsonFallback(jsonString, rulesList, extrasList)
        }

        cachedRules = rulesList
        cachedExtras = extrasList
    }

    private fun parseJsonFallback(
        jsonString: String,
        rulesList: MutableList<HolidayRule>,
        extrasList: MutableList<HolidayExtra>
    ) {
        val rulesSection = extractArrayContent(jsonString, "rules")
        val extrasSection = extractArrayContent(jsonString, "extras")

        val ruleObjects = extractObjects(rulesSection)
        for (text in ruleObjects) {
            val id = extractStringValue(text, "id")
            val nameEn = extractStringValue(text, "nameEn")
            val nameKm = extractStringValue(text, "nameKm").ifBlank { KHMER_NAME_MAP[id] ?: nameEn }
            val type = extractStringValue(text, "type").ifBlank { "fixed" }
            val month = extractIntValue(text, "month")
            val day = extractIntValue(text, "day")
            val lunarMonth = extractStringValue(text, "lunarMonth")
            val phase = extractStringValue(text, "phase")
            val lunarDay = extractIntValue(text, "lunarDay")
            val days = extractIntValue(text, "days").let { if (it == 0) 1 else it }
            val imageName = extractStringValue(text, "imageName").ifBlank { extractStringValue(text, "image") }

            if (id.isNotBlank()) {
                rulesList.add(
                    HolidayRule(
                        id = id,
                        nameEn = nameEn,
                        nameKm = nameKm,
                        type = type,
                        month = month,
                        day = day,
                        lunarMonth = lunarMonth,
                        phase = phase,
                        lunarDay = lunarDay,
                        days = days,
                        imageName = imageName
                    )
                )
            }
        }

        val extraObjects = extractObjects(extrasSection)
        for (text in extraObjects) {
            val id = extractStringValue(text, "id")
            val dateStr = extractStringValue(text, "date")
            val nameEn = extractStringValue(text, "nameEn")
            val nameKm = extractStringValue(text, "nameKm").ifBlank { nameEn }
            val imageName = extractStringValue(text, "imageName").ifBlank { extractStringValue(text, "image") }

            if (dateStr.isNotBlank()) {
                extrasList.add(
                    HolidayExtra(
                        id = id,
                        dateStr = dateStr,
                        nameEn = nameEn,
                        nameKm = nameKm,
                        imageName = imageName
                    )
                )
            }
        }
    }

    private fun extractArrayContent(json: String, key: String): String {
        val keyIndex = json.indexOf("\"$key\"")
        if (keyIndex == -1) return ""
        val bracketStart = json.indexOf('[', keyIndex)
        if (bracketStart == -1) return ""
        var depth = 1
        var idx = bracketStart + 1
        while (idx < json.length && depth > 0) {
            if (json[idx] == '[') depth++
            else if (json[idx] == ']') depth--
            idx++
        }
        return if (depth == 0) json.substring(bracketStart + 1, idx - 1) else ""
    }

    private fun extractObjects(arrayContent: String): List<String> {
        val list = mutableListOf<String>()
        var depth = 0
        var start = -1
        for (i in arrayContent.indices) {
            val ch = arrayContent[i]
            if (ch == '{') {
                if (depth == 0) start = i
                depth++
            } else if (ch == '}') {
                depth--
                if (depth == 0 && start != -1) {
                    list.add(arrayContent.substring(start, i + 1))
                    start = -1
                }
            }
        }
        return list
    }

    private fun extractStringValue(jsonObjText: String, key: String): String {
        val regex = Regex(""""$key"\s*:\s*"([^"]+)"""")
        return regex.find(jsonObjText)?.groupValues?.get(1) ?: ""
    }

    private fun extractIntValue(jsonObjText: String, key: String): Int {
        val regex = Regex(""""$key"\s*:\s*(\d+)""")
        return regex.find(jsonObjText)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    fun hasExtrasForRule(ruleId: String, year: Int): Boolean {
        val extras = cachedExtras ?: return false
        return extras.any { extra ->
            (extra.id == ruleId || (extra.id.isBlank() && extra.nameEn.lowercase().contains(ruleId.lowercase().replace("_", " ")))) &&
                    extra.dateStr.startsWith("$year-")
        }
    }

    fun getHoliday(
        date: LocalDate,
        lunarMonthIndex: Int,
        lunarDay: Int,
        isWaxing: Boolean
    ): Holiday? {
        if (cachedRules == null) {
            try {
                val inputStream: InputStream? = javaClass.classLoader?.getResourceAsStream("assets/holidays.json")
                if (inputStream != null) {
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    parseJson(jsonString)
                }
            } catch (_: Throwable) { }
        }
        val rules = cachedRules ?: return null
        val extras = cachedExtras ?: emptyList()

        // 1. Check Extras (exact date match YYYY-MM-DD)
        val dateIsoString = date.toString() // "YYYY-MM-DD"
        val matchedExtra = extras.find { it.dateStr == dateIsoString }
        if (matchedExtra != null) {
            val ruleImageName = rules.find { it.id == matchedExtra.id }?.imageName ?: ""
            return Holiday(
                id = matchedExtra.id.ifBlank { "extra_$dateIsoString" },
                nameKhmer = matchedExtra.nameKm,
                descriptionKhmer = matchedExtra.nameEn,
                dateFormatted = "${date.dayOfMonth} ${KhmerUtils.getSolarMonthKhmer(date.monthValue)}",
                imageName = matchedExtra.imageName.ifBlank { ruleImageName }
            )
        }

        // 2. Check Rules
        for (rule in rules) {
            // If extras defines exact dates for this rule ID in the current date's year, skip the generic rule for this year
            if (rule.id.isNotBlank()) {
                val hasExtrasForThisRuleAndYear = extras.any { extra ->
                    val matchesId = extra.id == rule.id ||
                            (extra.id.isBlank() && extra.nameEn.lowercase().contains(rule.id.lowercase().replace("_", " ")))
                    matchesId && extra.dateStr.startsWith("${date.year}-")
                }
                if (hasExtrasForThisRuleAndYear) {
                    continue
                }
            }

            if (rule.type == "fixed") {
                if (date.monthValue == rule.month && date.dayOfMonth in rule.day until (rule.day + rule.days)) {
                    val dayStr = KhmerUtils.toKhmerNumeral(rule.day)
                    val monthStr = KhmerUtils.getSolarMonthKhmer(rule.month)
                    return Holiday(
                        id = rule.id,
                        nameKhmer = rule.nameKm,
                        descriptionKhmer = rule.nameEn,
                        dateFormatted = if (rule.days > 1) {
                            val endDayStr = KhmerUtils.toKhmerNumeral(rule.day + rule.days - 1)
                            "$dayStr-$endDayStr $monthStr"
                        } else {
                            "$dayStr $monthStr"
                        },
                        imageName = rule.imageName
                    )
                }
            } else if (rule.type == "lunar") {
                val targetLunarMonthIndex = LUNAR_MONTH_INDEX_MAP[rule.lunarMonth.uppercase()] ?: -1
                val ruleStartOffset = if (rule.phase.uppercase() == "KERT") rule.lunarDay else 15 + rule.lunarDay
                val currentOffset = if (isWaxing) lunarDay else 15 + lunarDay

                val isSameMonthMatch = (lunarMonthIndex == targetLunarMonthIndex) &&
                        (currentOffset in ruleStartOffset until (ruleStartOffset + rule.days))

                val nextMonthIndex = (targetLunarMonthIndex + 1) % 12
                val isNextMonthRolloverMatch = (lunarMonthIndex == nextMonthIndex) &&
                        isWaxing &&
                        ((30 - ruleStartOffset + lunarDay) in 0 until rule.days)

                if (isSameMonthMatch || isNextMonthRolloverMatch) {
                    val phaseStr = if (isWaxing) "កើត" else "រោច"
                    val dayStr = KhmerUtils.toKhmerNumeral(lunarDay)
                    val monthStr = KhmerUtils.KHMER_LUNAR_MONTHS.getOrElse(lunarMonthIndex) { "" }
                    return Holiday(
                        id = rule.id,
                        nameKhmer = rule.nameKm,
                        descriptionKhmer = rule.nameEn,
                        dateFormatted = "$dayStr$phaseStr ខែ$monthStr",
                        imageName = rule.imageName
                    )
                }
            }
        }

        return null
    }
}
