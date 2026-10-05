package com.khmer.calendar.data.model

import com.khmer.calendar.R

data class Holiday(
    val id: String,
    val nameKhmer: String,
    val descriptionKhmer: String,
    val isPublicHoliday: Boolean = true,
    val iconEmoji: String = "🇰🇭",
    val dateFormatted: String,
    val imageName: String? = null,
    val customImageRes: Int? = null
) {
    val imageRes: Int
        get() {
            if (customImageRes != null) return customImageRes
            if (!imageName.isNullOrBlank()) {
                val res = getDrawableByName(imageName)
                if (res != null) return res
            }
            return getDrawableById(id)
        }

    private fun getDrawableByName(name: String): Int? {
        val cleanName = name.trim().lowercase().removePrefix("ic_")
        return when (cleanName) {
            "pchum_ben", "pchumben" -> R.drawable.ic_pchum_ben
            "flower" -> R.drawable.ic_flower
            "logo" -> R.drawable.ic_logo
            "holiday" -> R.drawable.ic_holiday
            "sil_day", "silday" -> R.drawable.ic_sil_day
            else -> null
        }
    }

    private fun getDrawableById(holidayId: String): Int {
        val cleanId = holidayId.lowercase()
        val name = nameKhmer.lowercase()
        val desc = descriptionKhmer.lowercase()
        return when {
            cleanId.contains("pchum_ben") || name.contains("ភ្ជុំបិណ្ឌ") || desc.contains("pchum ben") -> R.drawable.ic_pchum_ben
            cleanId.contains("khmer_new_year") || name.contains("ចូលឆ្នាំ") || desc.contains("new year") -> R.drawable.ic_pchum_ben
            cleanId.contains("visak_bochea") || cleanId.contains("meak_bochea") || cleanId.contains("plowing") ||
                    name.contains("បូជា") || name.contains("ច្រត់ព្រះនង្គ័ល") || desc.contains("bochea") || desc.contains("plowing") -> R.drawable.ic_flower
            cleanId.contains("bon_om_touk") || cleanId.contains("water_festival") || name.contains("អុំទូក") || desc.contains("water festival") -> R.drawable.ic_flower
            cleanId.contains("king") || cleanId.contains("queen") || cleanId.contains("coronation") ||
                    cleanId.contains("constitution") || cleanId.contains("independence") || cleanId.contains("logo") ||
                    name.contains("ព្រះរាជ") || name.contains("រដ្ឋធម្មនុញ្ញ") || name.contains("ឯករាជ្យ") ||
                    desc.contains("king") || desc.contains("queen") || desc.contains("constitution") || desc.contains("independence") -> R.drawable.ic_logo
            cleanId.contains("sil") || name.contains("សីល") -> R.drawable.ic_sil_day
            else -> R.drawable.ic_holiday
        }
    }
}

