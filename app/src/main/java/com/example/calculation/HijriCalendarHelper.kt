package com.example.calculation

import com.example.model.HijriDate
import com.example.model.IslamicOccasion
import com.example.model.UpcomingOccasionItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.floor

object HijriCalendarHelper {

    val hijriMonthNamesArabic = listOf(
        "مُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الآخِر",
        "جُمَادَى الأُولَى", "جُمَادَى الآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو القِعْدَة", "ذُو الحِجَّة"
    )

    val hijriMonthNamesEnglish = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    val dayNamesArabic = listOf(
        "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
    )

    val islamicOccasions = listOf(
        IslamicOccasion(
            id = "new_hijri_year",
            titleArabic = "رأس السنة الهجرية",
            hijriMonth = 1,
            hijriDay = 1,
            descriptionArabic = "بداية العام الهجري الجديد واستذكار هجرة النبي محمد ﷺ المباركة من مكة إلى المدينة.",
            virtueArabic = "بداية عام جديد مليء بالبركة والتقرب إلى الله وصيام شهر محرم شهر الله الحرام.",
            isHoliday = true
        ),
        IslamicOccasion(
            id = "tashua",
            titleArabic = "يوم تاسوعاء",
            hijriMonth = 1,
            hijriDay = 9,
            descriptionArabic = "اليوم التاسع من شهر محرم ويستحب صيامه مع يوم عاشوراء.",
            virtueArabic = "قال النبي ﷺ: «لئن بقيت إلى قابل لأصومن التاسع» لمخالفة أهل الكتاب.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "ashura",
            titleArabic = "يوم عاشوراء",
            hijriMonth = 1,
            hijriDay = 10,
            descriptionArabic = "اليوم الذي نجّى الله فيه نبيّه موسى عليه السلام وقومه من فرعون وجنوده.",
            virtueArabic = "صيام يوم عاشوراء يكفّر ذنوب السنة الماضية كما ثبت في صحيح مسلم.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "mawlid",
            titleArabic = "المولد النبوي الشريف",
            hijriMonth = 3,
            hijriDay = 12,
            descriptionArabic = "ذكرى مولد خير الأنام وسيد الخلق محمد ﷺ رحمة للعالمين.",
            virtueArabic = "استذكار سيرته العطرة والإكثار من الصلاة والسلام عليه ﷺ.",
            isHoliday = true
        ),
        IslamicOccasion(
            id = "israa_miraj",
            titleArabic = "ليلة الإسراء والمعراج",
            hijriMonth = 7,
            hijriDay = 27,
            descriptionArabic = "معجزة إسراء النبي ﷺ من المسجد الحرام إلى المسجد الأقصى وعروجه إلى السماوات العُلى وفرض الصلوات الخمس.",
            virtueArabic = "استشعار عظمة الصلاة وفضل المسجد الأقصى المبارك والتضرع بالدعاء.",
            isHoliday = false
        ),
        IslamicOccasion(
            id = "nisf_shaban",
            titleArabic = "ليلة النصف من شعبان",
            hijriMonth = 8,
            hijriDay = 15,
            descriptionArabic = "ليلة مباركة تُرفع فيها الأعمال إلى الله وتُغفر فيها الذنوب.",
            virtueArabic = "يطلع الله إلى خلقه في ليلة النصف من شعبان فيغفر لجميع خلقه إلا لمشرك أو مشاحن.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "first_ramadan",
            titleArabic = "غرة شهر رمضان المبارك",
            hijriMonth = 9,
            hijriDay = 1,
            descriptionArabic = "أول أيام شهر الصيام والقرآن والرحمة والمغفرة والعتق من النيران.",
            virtueArabic = "شهر الصيام، تُفتح فيه أبواب الجنة وتُغلق أبواب النار وتُصفّد الشياطين.",
            isFastRecommended = true,
            isHoliday = true
        ),
        IslamicOccasion(
            id = "badr",
            titleArabic = "ذكرى غزوة بدر الكبرى",
            hijriMonth = 9,
            hijriDay = 17,
            descriptionArabic = "يوم الفرقان يوم التقى الجمعان وأعز الله فيه دينه ونصر نبيه والمؤمنين.",
            virtueArabic = "أول معركة فاصلة في تاريخ الإسلام ونصر عزيز من الله لأهل بدر.",
            isHoliday = false
        ),
        IslamicOccasion(
            id = "laylat_al_qadr",
            titleArabic = "ليلة القدر والعشر الأواخر",
            hijriMonth = 9,
            hijriDay = 27,
            descriptionArabic = "ليلة مباركة أُنزل فيها القرآن الكريم في العشر الأواخر من شهر رمضان المبارك.",
            virtueArabic = "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ، والعبادة فيها تعدل عبادة أكثر من 83 عاماً.",
            isHoliday = false
        ),
        IslamicOccasion(
            id = "eid_al_fitr",
            titleArabic = "عيد الفطر المبارك",
            hijriMonth = 10,
            hijriDay = 1,
            descriptionArabic = "يوم الجائزة والفرحة بإتمام صيام وقيام شهر رمضان المبارك.",
            virtueArabic = "فرحة الصائم بفطره بعد تمام عبادته، ويحرم صيام يوم العيد.",
            isHoliday = true
        ),
        IslamicOccasion(
            id = "shawwal_six",
            titleArabic = "صيام الست من شوال",
            hijriMonth = 10,
            hijriDay = 2,
            descriptionArabic = "يستحب صيام ستة أيام من شهر شوال بعد يوم عيد الفطر متتابعة أو متفرقة.",
            virtueArabic = "من صام رمضان ثم أتبعه ستاً من شوال كان كصيام الدهر كله.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "dhul_hijjah_ten",
            titleArabic = "عشر ذي الحجة المباركة",
            hijriMonth = 12,
            hijriDay = 1,
            descriptionArabic = "أفضل أيام الدنيا، يُستحب فيها الإكثار من الصيام والذكر والتكبير والصدقة.",
            virtueArabic = "ما من أيام العمل الصالح فيها أحب إلى الله من هذه الأيام العشر.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "tarwiyah",
            titleArabic = "يوم التروية",
            hijriMonth = 12,
            hijriDay = 8,
            descriptionArabic = "اليوم الثامن من ذي الحجة حيث ينطلق حجاج بيت الله الحرام إلى مشعر منى.",
            virtueArabic = "يوم مبارك يستعد فيه الحجيج للوقوف بعرفة وتكثر فيه التلبية.",
            isHoliday = false
        ),
        IslamicOccasion(
            id = "arafah",
            titleArabic = "يوم عرفة المبارك",
            hijriMonth = 12,
            hijriDay = 9,
            descriptionArabic = "أعظم أركان الحج، يقف فيه الحجيج بجبل عرفات متضرعين إلى الله تعالى.",
            virtueArabic = "صيام يوم عرفة لغير الحاج يكفر السنة الماضية والسنة القابلة.",
            isFastRecommended = true
        ),
        IslamicOccasion(
            id = "eid_al_adha",
            titleArabic = "عيد الأضحى المبارك",
            hijriMonth = 12,
            hijriDay = 10,
            descriptionArabic = "يوم النحر الأعظم وذكرى فداء نبي الله إسماعيل عليه السلام بأضحية عظيمة.",
            virtueArabic = "أعظم الأيام عند الله يوم النحر، ويستحب فيه التقرب بالأضاحي وإطعام الفقراء.",
            isHoliday = true
        ),
        IslamicOccasion(
            id = "tashreeq",
            titleArabic = "أيام التشريق",
            hijriMonth = 12,
            hijriDay = 11,
            descriptionArabic = "الحادي عشر والثاني عشر والثالث عشر من ذي الحجة، أيام أكل وشرب وذكر لله.",
            virtueArabic = "أيام ذكر وتكبير لله تعالى ورمي الجمرات بمشعر منى لحجاج بيت الله.",
            isHoliday = true
        )
    )

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Converts a Gregorian Calendar date to accurate HijriDate
     */
    fun gregorianToHijri(calendar: Calendar): HijriDate {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val jd = julianDay(y, m, d)

        val l = jd.toLong() - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316).toInt()) * ((50 * lPrime / 17719).toInt()) +
                ((lPrime / 5670).toInt()) * ((43 * lPrime / 15238).toInt())
        val lDoublePrime = lPrime - (((30 - j) / 15).toInt()) * ((17719 * j / 50).toInt()) -
                ((j / 16).toInt()) * ((15238 * j / 43).toInt()) + 29
        val hijriMonth = (24 * lDoublePrime / 709).toInt()
        val hijriDay = (lDoublePrime - 709 * hijriMonth / 24).toInt()
        val hijriYear = (30 * n + j - 30).toInt()

        val monthIdx = (hijriMonth - 1).coerceIn(0, 11)
        val monthAr = hijriMonthNamesArabic[monthIdx]
        val monthEn = hijriMonthNamesEnglish[monthIdx]

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayNameAr = dayNamesArabic.getOrElse(dayOfWeek - 1) { "" }

        return HijriDate(
            year = hijriYear,
            month = hijriMonth,
            day = hijriDay,
            monthNameArabic = monthAr,
            monthNameEnglish = monthEn,
            dayNameArabic = dayNameAr
        )
    }

    /**
     * Finds Islamic occasion for a specific Hijri day and month
     */
    fun getOccasion(hijriMonth: Int, hijriDay: Int): IslamicOccasion? {
        return islamicOccasions.firstOrNull { it.hijriMonth == hijriMonth && it.hijriDay == hijriDay }
    }

    /**
     * Calculates upcoming occasions with days remaining relative to today
     */
    fun getUpcomingOccasions(todayCal: Calendar = Calendar.getInstance()): List<UpcomingOccasionItem> {
        val todayHijri = gregorianToHijri(todayCal)
        val currentHijriDayOfYear = (todayHijri.month - 1) * 30 + todayHijri.day

        val items = mutableListOf<UpcomingOccasionItem>()

        for (occ in islamicOccasions) {
            val occDayOfYear = (occ.hijriMonth - 1) * 30 + occ.hijriDay
            var diffDays = occDayOfYear - currentHijriDayOfYear
            if (diffDays < 0) {
                diffDays += 354 // Occurs next Hijri year
            }

            // Estimate Gregorian date for this event
            val eventCal = (todayCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, diffDays)
            }
            val dateFmt = SimpleDateFormat("d MMMM yyyy", Locale("ar"))

            items.add(
                UpcomingOccasionItem(
                    occasion = occ,
                    nextGregorianDateFormatted = dateFmt.format(eventCal.time),
                    daysRemaining = diffDays
                )
            )
        }

        return items.sortedBy { it.daysRemaining }
    }

    data class CalendarGridCell(
        val gregorianCalendar: Calendar,
        val gregorianDay: Int,
        val gregorianMonth: Int, // 0-indexed
        val gregorianYear: Int,
        val hijriDate: HijriDate,
        val isToday: Boolean,
        val isCurrentMonth: Boolean,
        val occasion: IslamicOccasion?,
        val isFriday: Boolean,
        val isAyyamAlBeed: Boolean
    )

    /**
     * Generates calendar days matrix for a given Gregorian year and month
     */
    fun generateMonthMatrix(year: Int, month: Int): List<CalendarGridCell> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayCal = Calendar.getInstance()
        val todayYear = todayCal.get(Calendar.YEAR)
        val todayMonth = todayCal.get(Calendar.MONTH)
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

        // Day of week of the 1st of month: 1=Sunday, 7=Saturday
        // In Arabic/Islamic week, Saturday is day 7 or index 0 depending on start.
        // Let's standardly start week on Saturday (السبت) or Sunday (الأحد).
        // If start is Sunday (1): offset = firstDayOfWeek - 1
        // If start is Saturday (7): offset = (firstDayOfWeek % 7)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon... 7=Sat
        val leadingDays = (firstDayOfWeek % 7) // If Saturday(7)->0 leading days; If Sunday(1)->1 leading day

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val cells = mutableListOf<CalendarGridCell>()

        // 1. Previous month leading days
        if (leadingDays > 0) {
            val prevCal = (cal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, -leadingDays)
            }
            for (i in 0 until leadingDays) {
                val h = gregorianToHijri(prevCal)
                val isToday = (prevCal.get(Calendar.YEAR) == todayYear &&
                        prevCal.get(Calendar.MONTH) == todayMonth &&
                        prevCal.get(Calendar.DAY_OF_MONTH) == todayDay)
                val occ = getOccasion(h.month, h.day)

                cells.add(
                    CalendarGridCell(
                        gregorianCalendar = prevCal.clone() as Calendar,
                        gregorianDay = prevCal.get(Calendar.DAY_OF_MONTH),
                        gregorianMonth = prevCal.get(Calendar.MONTH),
                        gregorianYear = prevCal.get(Calendar.YEAR),
                        hijriDate = h,
                        isToday = isToday,
                        isCurrentMonth = false,
                        occasion = occ,
                        isFriday = prevCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY,
                        isAyyamAlBeed = h.isAyyamAlBeed
                    )
                )
                prevCal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // 2. Current month days
        val currentCal = cal.clone() as Calendar
        for (day in 1..daysInMonth) {
            currentCal.set(Calendar.DAY_OF_MONTH, day)
            val h = gregorianToHijri(currentCal)
            val isToday = (year == todayYear && month == todayMonth && day == todayDay)
            val occ = getOccasion(h.month, h.day)

            cells.add(
                CalendarGridCell(
                    gregorianCalendar = currentCal.clone() as Calendar,
                    gregorianDay = day,
                    gregorianMonth = month,
                    gregorianYear = year,
                    hijriDate = h,
                    isToday = isToday,
                    isCurrentMonth = true,
                    occasion = occ,
                    isFriday = currentCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY,
                    isAyyamAlBeed = h.isAyyamAlBeed
                )
            )
        }

        // 3. Next month trailing days to complete full weeks (up to multiple of 7)
        val remainder = cells.size % 7
        if (remainder != 0) {
            val trailingDays = 7 - remainder
            val nextCal = (cal.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, daysInMonth)
                add(Calendar.DAY_OF_MONTH, 1)
            }
            for (i in 0 until trailingDays) {
                val h = gregorianToHijri(nextCal)
                val isToday = (nextCal.get(Calendar.YEAR) == todayYear &&
                        nextCal.get(Calendar.MONTH) == todayMonth &&
                        nextCal.get(Calendar.DAY_OF_MONTH) == todayDay)
                val occ = getOccasion(h.month, h.day)

                cells.add(
                    CalendarGridCell(
                        gregorianCalendar = nextCal.clone() as Calendar,
                        gregorianDay = nextCal.get(Calendar.DAY_OF_MONTH),
                        gregorianMonth = nextCal.get(Calendar.MONTH),
                        gregorianYear = nextCal.get(Calendar.YEAR),
                        hijriDate = h,
                        isToday = isToday,
                        isCurrentMonth = false,
                        occasion = occ,
                        isFriday = nextCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY,
                        isAyyamAlBeed = h.isAyyamAlBeed
                    )
                )
                nextCal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return cells
    }
}
