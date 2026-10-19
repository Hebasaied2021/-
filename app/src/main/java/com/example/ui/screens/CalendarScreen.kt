package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculation.HijriCalendarHelper
import com.example.calculation.PrayerTimeCalculator
import com.example.model.IslamicOccasion
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val location by viewModel.currentLocation.collectAsState()
    val calculationMethod by viewModel.calculationMethod.collectAsState()
    val juristicMethod by viewModel.juristicMethod.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: التقويم المتزامن, 1: المناسبات الإسلامية
    val currentCalendar = remember { Calendar.getInstance() }

    var displayYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    var displayMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH)) } // 0-indexed

    val monthMatrix = remember(displayYear, displayMonth) {
        HijriCalendarHelper.generateMonthMatrix(displayYear, displayMonth)
    }

    var selectedCell by remember {
        mutableStateOf(monthMatrix.firstOrNull { it.isToday } ?: monthMatrix.firstOrNull())
    }

    val upcomingOccasions = remember {
        HijriCalendarHelper.getUpcomingOccasions(currentCalendar)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calendar_screen")
    ) {
        // Tab Row: التقويم المتزامن vs المناسبات الإسلامية
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("التقويم المتزامن", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                    }
                },
                modifier = Modifier.testTag("calendar_tab_matrix")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("المناسبات الإسلامية", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                    }
                },
                modifier = Modifier.testTag("calendar_tab_occasions")
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Month Navigation Header Card
                item {
                    val firstDayHijri = monthMatrix.firstOrNull { it.isCurrentMonth }?.hijriDate
                    val lastDayHijri = monthMatrix.lastOrNull { it.isCurrentMonth }?.hijriDate

                    val hijriMonthTitle = if (firstDayHijri != null && lastDayHijri != null && firstDayHijri.month != lastDayHijri.month) {
                        "${firstDayHijri.monthNameArabic} / ${lastDayHijri.monthNameArabic} ${firstDayHijri.year} هـ"
                    } else {
                        "${firstDayHijri?.monthNameArabic ?: ""} ${firstDayHijri?.year ?: ""} هـ"
                    }

                    val gregorianMonthFmt = SimpleDateFormat("MMMM yyyy", Locale("ar"))
                    val displayCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, displayYear)
                        set(Calendar.MONTH, displayMonth)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val gregorianMonthTitle = gregorianMonthFmt.format(displayCal.time)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Previous Month
                                IconButton(
                                    onClick = {
                                        if (displayMonth == 0) {
                                            displayMonth = 11
                                            displayYear -= 1
                                        } else {
                                            displayMonth -= 1
                                        }
                                    },
                                    modifier = Modifier.testTag("calendar_prev_month_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward, // RTL Forward is logically previous
                                        contentDescription = "الشهر السابق",
                                        tint = EmeraldPrimary
                                    )
                                }

                                // Centered Titles
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = hijriMonthTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = gregorianMonthTitle,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Next Month
                                IconButton(
                                    onClick = {
                                        if (displayMonth == 11) {
                                            displayMonth = 0
                                            displayYear += 1
                                        } else {
                                            displayMonth += 1
                                        }
                                    },
                                    modifier = Modifier.testTag("calendar_next_month_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // RTL Back is logically next
                                        contentDescription = "الشهر القادم",
                                        tint = EmeraldPrimary
                                    )
                                }
                            }

                            // Jump to Today button
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .clickable {
                                            val now = Calendar.getInstance()
                                            displayYear = now.get(Calendar.YEAR)
                                            displayMonth = now.get(Calendar.MONTH)
                                            selectedCell = monthMatrix.firstOrNull { it.isToday }
                                        }
                                        .testTag("calendar_jump_today_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Today,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "العودة إلى اليوم",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Days of week header row (السبت، الأحد، الإثنين، الثلاثاء، الأربعاء، الخميس، الجمعة)
                item {
                    val weekDays = listOf("سبت", "أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEach { name ->
                            val isFriday = name == "جمعة"
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFriday) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Calendar Grid Days
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val rows = monthMatrix.chunked(7)
                        rows.forEach { rowCells ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                rowCells.forEach { cell ->
                                    val isSelected = selectedCell?.gregorianCalendar?.timeInMillis == cell.gregorianCalendar.timeInMillis
                                    CalendarCellItem(
                                        cell = cell,
                                        isSelected = isSelected,
                                        onSelect = { selectedCell = cell },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Day Details Card
                item {
                    selectedCell?.let { cell ->
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectedDayDetailsCard(
                            cell = cell,
                            location = location,
                            calculationMethod = calculationMethod,
                            juristicMethod = juristicMethod
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // Important Islamic Occasions List Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .testTag("upcoming_occasions_list")
            ) {
                item {
                    Text(
                        text = "المناسبات والشهور الإسلامية المهمة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(upcomingOccasions) { item ->
                    OccasionCard(item = item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarCellItem(
    cell: HijriCalendarHelper.CalendarGridCell,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = cell.isToday
    val isCurrentMonth = cell.isCurrentMonth
    val hasOccasion = cell.occasion != null
    val isAyyamAlBeed = cell.isAyyamAlBeed && isCurrentMonth

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> EmeraldPrimary
                    isToday -> EmeraldPrimary.copy(alpha = 0.2f)
                    isAyyamAlBeed -> GoldAccent.copy(alpha = 0.15f)
                    else -> if (isCurrentMonth) MaterialTheme.colorScheme.surface else Color.Transparent
                }
            )
            .border(
                width = when {
                    isSelected -> 2.dp
                    isToday -> 1.5.dp
                    hasOccasion && isCurrentMonth -> 1.dp
                    else -> 0.5.dp
                },
                color = when {
                    isSelected -> GoldAccent
                    isToday -> EmeraldPrimary
                    hasOccasion && isCurrentMonth -> GoldDark
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .testTag("calendar_cell_${cell.gregorianDay}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            // Gregorian Day
            Text(
                text = "${cell.gregorianDay}",
                fontSize = 13.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.SemiBold,
                color = when {
                    isSelected -> Color.White
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    cell.isFriday -> EmeraldPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Hijri Day (Golden)
            Text(
                text = "${cell.hijriDate.day}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSelected -> GoldLight
                    !isCurrentMonth -> GoldDark.copy(alpha = 0.3f)
                    else -> GoldDark
                }
            )

            // Dot / Star indicator for Occasions
            if (hasOccasion && isCurrentMonth) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoldLight else GoldDark)
                )
            }
        }
    }
}

@Composable
private fun SelectedDayDetailsCard(
    cell: HijriCalendarHelper.CalendarGridCell,
    location: com.example.model.LocationData,
    calculationMethod: com.example.model.CalculationMethod,
    juristicMethod: com.example.model.JuristicMethod
) {
    val gregorianFmt = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
    val gregorianDateStr = gregorianFmt.format(cell.gregorianCalendar.time)

    // Calculate prayer times for this specific day
    val prayerSchedule = remember(cell.gregorianCalendar.timeInMillis, location) {
        PrayerTimeCalculator.calculate(
            location = location,
            calendar = cell.gregorianCalendar,
            method = calculationMethod,
            juristic = juristicMethod
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Date Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${cell.hijriDate.dayNameArabic} ${cell.hijriDate.formatted}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "الموافق: $gregorianDateStr م",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (cell.isToday) {
                    Surface(
                        color = EmeraldPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "اليوم",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Special Occasion Banner
            if (cell.occasion != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = GoldAccent.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cell.occasion.titleArabic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cell.occasion.descriptionArabic,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (cell.occasion.virtueArabic.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✨ الفضل: ${cell.occasion.virtueArabic}",
                                fontSize = 11.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (cell.isAyyamAlBeed) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🌕 من الأيام البيض (13، 14، 15) المستحب صيامها من كل شهر هجري.",
                        fontSize = 12.sp,
                        color = EmeraldDark,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prayer times miniature summary for this day
            Text(
                text = "مواقيت الصلاة لهذا اليوم (${location.cityNameArabic}):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                prayerSchedule.times.forEach { p ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = p.type.arabicName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = p.formattedTime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OccasionCard(item: com.example.model.UpcomingOccasionItem) {
    val occ = item.occasion

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("occasion_card_${occ.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (occ.isHoliday) Icons.Default.Celebration else Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = occ.titleArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = occ.dateFormatted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    }
                }

                // Days Remaining Pill
                Surface(
                    color = if (item.daysRemaining == 0) EmeraldPrimary else GoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (item.daysRemaining == 0) "اليوم!" else "متبقي ${item.daysRemaining} يوماً",
                        color = if (item.daysRemaining == 0) Color.White else GoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = occ.descriptionArabic,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (occ.virtueArabic.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⭐ فضلها: ${occ.virtueArabic}",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = EmeraldDark,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الموعد الميلادي المتوقع: ${item.nextGregorianDateFormatted} م",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
