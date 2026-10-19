package com.example.model

import com.example.R

data class Muezzin(
    val id: String,
    val nameArabic: String,
    val titleArabic: String,
    val cityOrMosqueArabic: String,
    val bioArabic: String,
    val baseFrequency: Float = 150f,
    val tempo: Float = 1.0f,
    val vibratoDepth: Float = 3.0f,
    val maqamName: String = "مقام الحجاز",
    val rawResId: Int? = null,
    val audioUrl: String = "",
    val durationText: String = "3:30",
    val isRealHumanVoice: Boolean = true
) {
    companion object {
        val allMuezzins = listOf(
            Muezzin(
                id = "ali_mulla",
                nameArabic = "الشيخ علي أحمد ملا",
                titleArabic = "شيخ مؤذني المسجد الحرام",
                cityOrMosqueArabic = "مكة المكرمة",
                bioArabic = "صوت الحرم المكي الشريف الخالد منذ عام 1975م، نداء مهيب وتكبيرات خاشعة تتردد أصداؤها في جنبات الكعبة المشرفة بصوت ندي مسجل حقيقي.",
                baseFrequency = 145f,
                tempo = 0.9f,
                vibratoDepth = 3.5f,
                maqamName = "مقام الحجاز المكي",
                rawResId = R.raw.adhan_makkah,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Makkah.mp3",
                durationText = "3:24"
            ),
            Muezzin(
                id = "abdulbasit",
                nameArabic = "الشيخ عبد الباسط عبد الصمد",
                titleArabic = "الصوت الذهبي الخالد",
                cityOrMosqueArabic = "مصر ومكة المكرمة",
                bioArabic = "صوت أسطوري في تلاوة القرآن ورفع الأذان، بنبرات روحانية عميقة تأسر القلوب خشوعاً وسكينة بأداء حجازي وبياتي رفيع.",
                baseFrequency = 160f,
                tempo = 0.85f,
                vibratoDepth = 4.2f,
                maqamName = "مقام الصبا والبياتي",
                rawResId = R.raw.adhan_abdulbasit,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Abed%20Albase6.mp3",
                durationText = "3:40"
            ),
            Muezzin(
                id = "alafasy",
                nameArabic = "الشيخ مشاري راشد العفاسي",
                titleArabic = "إمام ومؤذن المسجد الكبير",
                cityOrMosqueArabic = "دولة الكويت",
                bioArabic = "قارئ ومؤذن عذب ورخيم، يتميز أذانه بالنقاء الصوتي الاستثنائي والرنين الشجي الذي يفيض هيبة وسكينة وخشوعاً.",
                baseFrequency = 175f,
                tempo = 1.0f,
                vibratoDepth = 2.8f,
                maqamName = "مقام الرصد العذب",
                rawResId = R.raw.adhan_alafasy,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Mishary%20Alafasi.mp3",
                durationText = "4:00"
            ),
            Muezzin(
                id = "madinah_nabawi",
                nameArabic = "أذان المسجد النبوي الشريف",
                titleArabic = "الشيخ عصام بخاري ومؤذنو الحرم",
                cityOrMosqueArabic = "المدينة المنورة",
                bioArabic = "نداء المدينة المنورة المبارك من رحاب المسجد النبوي والروضة الشريفة، بنغم مدني هادئ يملأ النفس طمأنينة وسلاماً.",
                baseFrequency = 155f,
                tempo = 0.92f,
                vibratoDepth = 3.2f,
                maqamName = "المقام المدني الأصيل",
                rawResId = R.raw.adhan_madinah,
                audioUrl = "https://raw.githubusercontent.com/achaudhry/adhan/master/Adhan-Madinah.mp3",
                durationText = "3:55"
            ),
            Muezzin(
                id = "minshawi",
                nameArabic = "الشيخ محمد صديق المنشاوي",
                titleArabic = "الصوت الباكي الخاشع",
                cityOrMosqueArabic = "مصر",
                bioArabic = "أحد أعظم أصوات التلاوة والأذان، صوت شجي يلامس أوتار القلوب خشوعاً وتضرعاً وإجلالاً لعظمة الخالق جل وعلا.",
                baseFrequency = 158f,
                tempo = 0.88f,
                vibratoDepth = 3.6f,
                maqamName = "مقام النهاوند والبياتي",
                rawResId = R.raw.adhan_minshawi,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Mohammad%20Almenshawy.mp3",
                durationText = "3:35"
            ),
            Muezzin(
                id = "refaat",
                nameArabic = "الشيخ محمد رفعت",
                titleArabic = "قيثارة السماء والصوت الخالد",
                cityOrMosqueArabic = "القاهرة",
                bioArabic = "أذان الإذاعة المصرية التاريخي، صوت أصيل حفر في ذاكرة الأمة الإسلامية منذ عقود بطابعه الروحي الفريد في رمضان والمناسبات.",
                baseFrequency = 152f,
                tempo = 0.85f,
                vibratoDepth = 4.0f,
                maqamName = "مقام السيكاه الأصيل",
                rawResId = R.raw.adhan_refaat,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Mohammad%20Ref3at.mp3",
                durationText = "3:20"
            ),
            Muezzin(
                id = "nasser_alqatami",
                nameArabic = "الشيخ ناصر القطامي",
                titleArabic = "إمام جامع الملك عبد الله",
                cityOrMosqueArabic = "الرياض",
                bioArabic = "صوت ندي رقيق يفيض خشوعاً ومحبة، من أبرز أصوات الأذان المحببة في المملكة والخليج بنبرة عذبة مؤثرة.",
                baseFrequency = 168f,
                tempo = 0.95f,
                vibratoDepth = 3.0f,
                maqamName = "مقام الكرد",
                rawResId = R.raw.adhan_qatami,
                audioUrl = "https://raw.githubusercontent.com/abodehq/Athan-MP3/master/Sounds/Athan%20Nasser%20Alqatami.mp3",
                durationText = "3:15"
            ),
            Muezzin(
                id = "mustafa_ismail",
                nameArabic = "أذان القاهرة وجامع الأزهر الشريف",
                titleArabic = "الأذان المصري الكلاسيكي العريق",
                cityOrMosqueArabic = "القاهرة - مصر",
                bioArabic = "عملاق المقامات القرآنية والأذان المصري الكلاسيكي العريق، بانتقالات نغمية فريدة تعبر عن جلال النداء وعراقة المساجد التاريخية.",
                baseFrequency = 150f,
                tempo = 0.88f,
                vibratoDepth = 3.8f,
                maqamName = "مقام البياتي الأصيل",
                rawResId = null,
                audioUrl = "http://praytimes.org/audio/adhan/Sunni/Adhan%20Egypt.mp3",
                durationText = "3:48"
            )
        )

        val defaultMuezzin = allMuezzins[0]
    }
}
