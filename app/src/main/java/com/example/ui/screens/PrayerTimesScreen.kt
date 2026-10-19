package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PrayerType
import com.example.model.SinglePrayerTime
import com.example.calculation.HijriCalendarHelper
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.model.ThemeMode
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PrayerTimesScreen(
    viewModel: MainViewModel,
    onNavigateToCalendar: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToMuezzins: () -> Unit
) {
    val location by viewModel.currentLocation.collectAsState()
    val schedule by viewModel.prayerSchedule.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()
    val randomThikr by viewModel.randomThikr.collectAsState()
    val selectedMuezzin by viewModel.selectedMuezzin.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val playingMuezzinId by viewModel.playingMuezzinId.collectAsState()
    val currentPhrase by viewModel.currentAudioPhrase.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val qiblaBearing by viewModel.qiblaBearing.collectAsState()
    val distanceKm by viewModel.distanceToKaabaKm.collectAsState()
    val compassHeading by viewModel.compassHeading.collectAsState()
    val isQiblaAligned by viewModel.isQiblaAligned.collectAsState()
    val randomAthkarEnabled by viewModel.randomAthkarEnabled.collectAsState()

    var showCityDialog by remember { mutableStateOf(false) }

    if (showCityDialog) {
        CitySelectionDialog(
            currentLocation = location,
            isLocating = isLocating,
            onDismiss = { showCityDialog = false },
            onSelectCity = { city ->
                viewModel.setLocation(city)
            },
            onRequestGps = {
                viewModel.requestGpsLocation()
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("prayer_times_screen")
    ) {
        // Hero Header Image & Mosque Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.prayer_header_art),
                    contentDescription = "زخرفة إسلامية",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    EmeraldDark.copy(alpha = 0.85f),
                                    EmeraldDark
                                )
                            )
                        )
                )

                // Top Location Bar Overlay
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter)
                ) {
                    // Location Pill
                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showCityDialog = true }
                            .testTag("location_picker_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${location.cityNameArabic}، ${location.countryNameArabic}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ChangeCircle,
                                contentDescription = "تغيير",
                                tint = GoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Hijri Date Button / Pill (Opens Calendar)
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onNavigateToCalendar() }
                                .testTag("hijri_date_pill_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "التقويم الهجري",
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = schedule.hijriDate,
                                    color = GoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Theme Mode / Dark Mode Toggle Button
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.toggleDarkMode() }
                                .testTag("toggle_dark_mode_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "تبديل الوضع الليلي",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Header Bottom Title
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "نور الصلاة",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = schedule.dateFormatted,
                        fontSize = 13.sp,
                        color = GoldLight
                    )
                }
            }
        }

        // Next Prayer Hero Countdown Card
        item {
            val next = schedule.nextPrayer
            val remainingSec = (schedule.remainingMillis / 1000).coerceAtLeast(0)
            val hours = remainingSec / 3600
            val minutes = (remainingSec % 3600) / 60
            val seconds = remainingSec % 60
            val countdownStr = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("next_prayer_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    EmeraldPrimary.copy(alpha = 0.12f),
                                    GoldAccent.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = GoldAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "الصلاة القادمة",
                                    color = GoldDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "صلاة ${next?.type?.arabicName ?: "الفجر"}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "الأذان في تمام: ${next?.formattedTime ?: "--:--"}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Countdown Timer Display
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = countdownStr,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المتبقي",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Circular Progress Dial
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            val animatedProgress by animateFloatAsState(
                                targetValue = schedule.intervalProgress,
                                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                                label = "prayerProgress"
                            )

                            CircularProgressIndicator(
                                progress = { 1f },
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                strokeWidth = 8.dp,
                                modifier = Modifier.fillMaxSize()
                            )

                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                color = GoldAccent,
                                strokeWidth = 8.dp,
                                modifier = Modifier.fillMaxSize()
                            )

                            Icon(
                                imageVector = getPrayerIcon(next?.type ?: PrayerType.FAJR),
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Muezzin Quick Banner
        item {
            val isPlayingThis = isAudioPlaying && playingMuezzinId == selectedMuezzin.id

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToMuezzins() }
                    .testTag("active_muezzin_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "صوت الأذان المعتمد: ${selectedMuezzin.nameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isPlayingThis && currentPhrase.isNotEmpty()) "يصدح الآن: $currentPhrase" else "${selectedMuezzin.cityOrMosqueArabic} • ${selectedMuezzin.maqamName}",
                            fontSize = 12.sp,
                            color = if (isPlayingThis) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleAudioPreview(selectedMuezzin) },
                        modifier = Modifier.testTag("preview_adhan_button")
                    ) {
                        Icon(
                            imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingThis) "إيقاف" else "استماع",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Upcoming Islamic Occasion Card
        item {
            val upcoming = remember {
                HijriCalendarHelper.getUpcomingOccasions(Calendar.getInstance()).firstOrNull()
            }

            upcoming?.let { nextOcc ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = GoldAccent.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigateToCalendar() }
                        .testTag("upcoming_occasion_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "أقرب مناسبة إسلامية: ${nextOcc.occasion.titleArabic}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${nextOcc.occasion.dateFormatted} • متبقي ${nextOcc.daysRemaining} يوماً",
                                fontSize = 12.sp,
                                color = GoldDark
                            )
                        }

                        Text(
                            text = "عرض التقويم",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Daily Prayers List Section
        item {
            Text(
                text = "مواقيت صلوات اليوم",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp)
            )
        }

        items(schedule.times.size) { index ->
            val prayer = schedule.times[index]
            val isEnabled = viewModel.prefs.isPrayerNotificationEnabled(prayer.type.name)

            PrayerRowItem(
                prayer = prayer,
                isNotificationEnabled = isEnabled,
                onToggleNotification = {
                    viewModel.togglePrayerNotification(prayer.type.name, !isEnabled)
                }
            )
        }

        // Interactive Qibla Compass Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isQiblaAligned) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isQiblaAligned) 2.dp else 1.dp,
                    color = if (isQiblaAligned) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToQibla() }
                    .testTag("qibla_compass_quick_card")
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.18f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = "بوصلة القبلة",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "بوصلة اتجاه القبلة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isQiblaAligned) "أنت باتجاه القبلة الشريفة الآن 🕋" else "زاوية القبلة: ${qiblaBearing.roundToInt()}° • بعد ${distanceKm.roundToInt()} كم",
                                    fontSize = 12.sp,
                                    fontWeight = if (isQiblaAligned) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isQiblaAligned) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToQibla,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("فتح البوصلة", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Random Athkar Card (تذكير عشوائي بذكر الله)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("random_athkar_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨ تذكير عشوائي بذكر الله",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EmeraldPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.shuffleRandomThikr() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("shuffle_random_thikr_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "تغيير الذكر",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Switch(
                                checked = randomAthkarEnabled,
                                onCheckedChange = { viewModel.toggleRandomAthkar(it) },
                                modifier = Modifier.testTag("home_toggle_random_athkar_switch")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = randomThikr,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.testRandomAthkarNotification(2) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_random_athkar_quick_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إرسال إشعار تذكير الآن", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Bottom Spacer for navigation bar padding
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PrayerRowItem(
    prayer: SinglePrayerTime,
    isNotificationEnabled: Boolean,
    onToggleNotification: () -> Unit
) {
    val isNext = prayer.isNext
    val isPast = prayer.isPast

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                isNext -> EmeraldPrimary.copy(alpha = 0.15f)
                isPast -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isNext) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("prayer_row_${prayer.type.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isNext) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = getPrayerIcon(prayer.type),
                    contentDescription = prayer.type.arabicName,
                    tint = if (isNext) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Prayer Name
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = prayer.type.arabicName,
                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = if (isNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isNext) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "القادمة",
                                color = EmeraldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = prayer.type.englishName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Time
            Text(
                text = prayer.formattedTime,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )

            // Alert Toggle
            if (prayer.type.isSalat) {
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = onToggleNotification,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("toggle_notification_${prayer.type.name}")
                ) {
                    Icon(
                        imageVector = if (isNotificationEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = "تنبيه الأذان",
                        tint = if (isNotificationEnabled) GoldAccent else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

private fun getPrayerIcon(type: PrayerType): ImageVector {
    return when (type) {
        PrayerType.FAJR -> Icons.Default.Brightness4
        PrayerType.SUNRISE -> Icons.Default.WbTwilight
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.Brightness5
        PrayerType.MAGHRIB -> Icons.Default.Brightness7
        PrayerType.ISHA -> Icons.Default.Bedtime
        PrayerType.QIYAM -> Icons.Default.Bedtime
    }
}
