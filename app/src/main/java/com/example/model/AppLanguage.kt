package com.example.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val regionName: String
) {
    ENGLISH("en", "English", "English", "🇬🇧", "Global / USA / UK"),
    SPANISH("es", "Spanish", "Español", "🇪🇸", "España / América Latina"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳", "भारत (India)"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "🇮🇳", "ગુજરાત (Gujarat, India)"),
    FRENCH("fr", "French", "Français", "🇫🇷", "France / Canada"),
    GERMAN("de", "German", "Deutsch", "🇩🇪", "Deutschland / Österreich"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵", "日本 (Japan)"),
    CHINESE("zh", "Chinese (Simplified)", "简体中文", "🇨🇳", "中国 (China)"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦", "الشرق الأوسط (Middle East)"),
    PORTUGUESE("pt", "Portuguese", "Português", "🇧🇷", "Brasil / Portugal"),
    RUSSIAN("ru", "Russian", "Русский", "🇷🇺", "Россия / СНГ"),
    ITALIAN("it", "Italian", "Italiano", "🇮🇹", "Italia"),
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", "Indonesia"),
    KOREAN("ko", "Korean", "한국어", "🇰🇷", "대한민국 (Korea)"),
    TURKISH("tr", "Turkish", "Türkçe", "🇹🇷", "Türkiye");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
