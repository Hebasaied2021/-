package com.example.model

enum class ThemeMode(val titleArabic: String, val descriptionArabic: String) {
    SYSTEM("حسب إعدادات النظام", "يتغير المظهر تلقائياً وفق إعدادات هاتفك"),
    LIGHT("الوضع النهاري", "ألوان زاهية مريحة وواضحة للإضاءة النهارية"),
    DARK("الوضع الليلي", "تصميم إسلامي داكن باللون الزمردي والذهبي لحماية العين")
}
