package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.local.ThikrEntity
import com.example.model.ThikrCategory
import com.example.notification.PrayerNotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import java.io.File
import java.util.Locale

@Composable
fun AthkarScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showRecordDialog by remember { mutableStateOf(false) }

    // Room Database StateFlows
    val morningEntities by viewModel.morningAthkarEntities.collectAsState()
    val eveningEntities by viewModel.eveningAthkarEntities.collectAsState()
    val afterPrayerEntities by viewModel.afterPrayerAthkarEntities.collectAsState()
    val favoriteEntities by viewModel.favoriteAthkarEntities.collectAsState()
    val favoriteCount by viewModel.favoriteCount.collectAsState()
    val customVoiceAthkar by viewModel.customVoiceAthkar.collectAsState()

    // Voice Playback state
    val playingVoiceId by viewModel.playingVoiceThikrId.collectAsState()
    val isVoicePlaying by viewModel.isVoiceThikrPlaying.collectAsState()
    val voiceProgress by viewModel.voicePlaybackProgress.collectAsState()

    // Scheduled Audible Athkar state
    val isAudibleAthkarEnabled by viewModel.isAudibleAthkarEnabled.collectAsState()
    val isMorningAthkarScheduled by viewModel.isMorningAthkarScheduled.collectAsState()
    val morningAthkarTime by viewModel.morningAthkarTime.collectAsState()
    val isEveningAthkarScheduled by viewModel.isEveningAthkarScheduled.collectAsState()
    val eveningAthkarTime by viewModel.eveningAthkarTime.collectAsState()

    val randomThikr by viewModel.randomThikr.collectAsState()

    // Tasbih state
    val tasbihCurrentCount by viewModel.tasbihCurrentCount.collectAsState()
    val tasbihTargetCount by viewModel.tasbihTargetCount.collectAsState()
    val tasbihTotalCount by viewModel.tasbihTotalCount.collectAsState()
    val tasbihPhraseIndex by viewModel.currentTasbihPhraseIndex.collectAsState()
    val tasbihPhrases = viewModel.tasbihPhrases

    // Random Athkar Settings state
    val randomAthkarEnabled by viewModel.randomAthkarEnabled.collectAsState()
    val randomAthkarIntervalMin by viewModel.randomAthkarIntervalMin.collectAsState()

    val tabs = listOf(
        "أذكار الصباح 🌅",
        "أذكار المساء 🌇",
        "بعد الصلاة 🕌",
        "المفضلة ⭐ ($favoriteCount)",
        "تسجيلاتي الصوتية 🎙️ (${customVoiceAthkar.size})",
        "المواقيت المسموعة ⏰",
        "المسبحة 📿",
        "تذكير عشوائي 🎲"
    )

    // Voice Recording Dialog
    if (showRecordDialog) {
        VoiceRecordingDialog(
            viewModel = viewModel,
            onDismiss = { showRecordDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("athkar_screen")
    ) {
        // Top Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == index) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("athkar_tab_$index")
                )
            }
        }

        when (selectedTabIndex) {
            0 -> RoomAthkarListTab(
                items = morningEntities,
                title = "أذكار الصباح المباركة",
                category = ThikrCategory.MORNING,
                playingVoiceId = playingVoiceId,
                isVoicePlaying = isVoicePlaying,
                voiceProgress = voiceProgress,
                onIncrement = { viewModel.incrementThikrEntity(it.id) },
                onToggleFavorite = { viewModel.toggleThikrFavorite(it.id, it.isFavorite) },
                onResetCategory = { viewModel.resetThikrCategoryEntity(ThikrCategory.MORNING) },
                onPlayVoice = { viewModel.playVoiceThikr(it) },
                onStopVoice = { viewModel.stopVoiceThikrPlayback() },
                onOpenRecordDialog = { showRecordDialog = true }
            )
            1 -> RoomAthkarListTab(
                items = eveningEntities,
                title = "أذكار المساء وحفظ الليل",
                category = ThikrCategory.EVENING,
                playingVoiceId = playingVoiceId,
                isVoicePlaying = isVoicePlaying,
                voiceProgress = voiceProgress,
                onIncrement = { viewModel.incrementThikrEntity(it.id) },
                onToggleFavorite = { viewModel.toggleThikrFavorite(it.id, it.isFavorite) },
                onResetCategory = { viewModel.resetThikrCategoryEntity(ThikrCategory.EVENING) },
                onPlayVoice = { viewModel.playVoiceThikr(it) },
                onStopVoice = { viewModel.stopVoiceThikrPlayback() },
                onOpenRecordDialog = { showRecordDialog = true }
            )
            2 -> RoomAthkarListTab(
                items = afterPrayerEntities,
                title = "أذكار ما بعد الصلوات المكتوبة",
                category = ThikrCategory.AFTER_PRAYER,
                playingVoiceId = playingVoiceId,
                isVoicePlaying = isVoicePlaying,
                voiceProgress = voiceProgress,
                onIncrement = { viewModel.incrementThikrEntity(it.id) },
                onToggleFavorite = { viewModel.toggleThikrFavorite(it.id, it.isFavorite) },
                onResetCategory = { viewModel.resetThikrCategoryEntity(ThikrCategory.AFTER_PRAYER) },
                onPlayVoice = { viewModel.playVoiceThikr(it) },
                onStopVoice = { viewModel.stopVoiceThikrPlayback() },
                onOpenRecordDialog = { showRecordDialog = true }
            )
            3 -> FavoritesTab(
                items = favoriteEntities,
                playingVoiceId = playingVoiceId,
                isVoicePlaying = isVoicePlaying,
                voiceProgress = voiceProgress,
                onIncrement = { viewModel.incrementThikrEntity(it.id) },
                onToggleFavorite = { viewModel.toggleThikrFavorite(it.id, it.isFavorite) },
                onPlayVoice = { viewModel.playVoiceThikr(it) },
                onStopVoice = { viewModel.stopVoiceThikrPlayback() },
                onNavigateToMorning = { selectedTabIndex = 0 }
            )
            4 -> VoiceAthkarTab(
                items = customVoiceAthkar,
                playingVoiceId = playingVoiceId,
                isVoicePlaying = isVoicePlaying,
                voiceProgress = voiceProgress,
                onIncrement = { viewModel.incrementThikrEntity(it.id) },
                onToggleFavorite = { viewModel.toggleThikrFavorite(it.id, it.isFavorite) },
                onPlayVoice = { viewModel.playVoiceThikr(it) },
                onStopVoice = { viewModel.stopVoiceThikrPlayback() },
                onDeleteVoice = { viewModel.deleteVoiceThikr(it) },
                onOpenRecordDialog = { showRecordDialog = true }
            )
            5 -> ScheduledAudibleAthkarTab(
                isAudibleEnabled = isAudibleAthkarEnabled,
                isMorningScheduled = isMorningAthkarScheduled,
                morningTime = morningAthkarTime,
                isEveningScheduled = isEveningAthkarScheduled,
                eveningTime = eveningAthkarTime,
                onToggleAudible = { viewModel.setAudibleAthkarEnabled(it) },
                onUpdateMorningSchedule = { enabled, h, m -> viewModel.setMorningAthkarSchedule(enabled, h, m) },
                onUpdateEveningSchedule = { enabled, h, m -> viewModel.setEveningAthkarSchedule(enabled, h, m) },
                onTestMorning = { viewModel.testAudibleAthkarWork(ThikrCategory.MORNING, 2) },
                onTestEvening = { viewModel.testAudibleAthkarWork(ThikrCategory.EVENING, 2) }
            )
            6 -> TasbihTab(
                currentCount = tasbihCurrentCount,
                targetCount = tasbihTargetCount,
                totalCount = tasbihTotalCount,
                phrases = tasbihPhrases,
                selectedPhraseIndex = tasbihPhraseIndex,
                onIncrement = { viewModel.incrementTasbih() },
                onReset = { viewModel.resetTasbihCurrent() },
                onSelectPhrase = { viewModel.selectTasbihPhrase(it) },
                onSetTarget = { viewModel.setTasbihTarget(it) }
            )
            7 -> RandomAthkarTab(
                currentRandomThikr = randomThikr,
                randomAthkarEnabled = randomAthkarEnabled,
                intervalMinutes = randomAthkarIntervalMin,
                onShuffle = { viewModel.shuffleRandomThikr() },
                onToggleEnabled = { viewModel.toggleRandomAthkar(it) },
                onSelectInterval = { viewModel.setRandomAthkarInterval(it) },
                onSendTestNotification = {
                    PrayerNotificationHelper.showRandomAthkarNotification(
                        context,
                        viewModel.randomThikr.value
                    )
                }
            )
        }
    }
}

@Composable
private fun RoomAthkarListTab(
    items: List<ThikrEntity>,
    title: String,
    category: ThikrCategory,
    playingVoiceId: Int?,
    isVoicePlaying: Boolean,
    voiceProgress: Float,
    onIncrement: (ThikrEntity) -> Unit,
    onToggleFavorite: (ThikrEntity) -> Unit,
    onResetCategory: () -> Unit,
    onPlayVoice: (ThikrEntity) -> Unit,
    onStopVoice: () -> Unit,
    onOpenRecordDialog: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            items
        } else {
            items.filter {
                it.textArabic.contains(searchQuery, ignoreCase = true) ||
                        it.virtueArabic.contains(searchQuery, ignoreCase = true) ||
                        it.referenceArabic.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val completedCount = items.count { it.isCompleted }
    val totalCount = items.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("athkar_list_scroll_${category.name}")
    ) {
        // Progress & Category Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "أتممت $completedCount من $totalCount أذكار مباركة",
                            fontSize = 12.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenRecordDialog,
                            modifier = Modifier.testTag("record_quick_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "تسجيل ذكر بصوتك",
                                tint = GoldDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = onResetCategory,
                            modifier = Modifier.testTag("reset_athkar_category_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "إعادة ضبط العدادات",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث في نصوص الأذكار أو الفضل...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = EmeraldPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .testTag("athkar_search_input")
            )
        }

        // Athkar Items
        items(filteredItems, key = { it.id }) { item ->
            ThikrEntityCard(
                item = item,
                isPlaying = isVoicePlaying && playingVoiceId == item.id,
                playbackProgress = if (playingVoiceId == item.id) voiceProgress else 0f,
                onIncrement = { onIncrement(item) },
                onToggleFavorite = { onToggleFavorite(item) },
                onPlayVoice = { onPlayVoice(item) },
                onStopVoice = onStopVoice
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FavoritesTab(
    items: List<ThikrEntity>,
    playingVoiceId: Int?,
    isVoicePlaying: Boolean,
    voiceProgress: Float,
    onIncrement: (ThikrEntity) -> Unit,
    onToggleFavorite: (ThikrEntity) -> Unit,
    onPlayVoice: (ThikrEntity) -> Unit,
    onStopVoice: () -> Unit,
    onNavigateToMorning: () -> Unit
) {
    if (items.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("empty_favorites_view")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "قائمتك المفضلة فارغة حالياً",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "اضغط على أيقونة النجمة ⭐ بجانب أي ذكر في أذكار الصباح أو المساء لحفظه هنا وتسهيل قراءته في أي وقت.",
                fontSize = 13.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigateToMorning,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تصفح أذكار الصباح والمساء")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("favorites_list_scroll")
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "أذكارك المفضلة المختارة (${items.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "محفوظة محلياً في قاعدة البيانات وسهلة الوصول دائماً",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(items, key = { it.id }) { item ->
                ThikrEntityCard(
                    item = item,
                    isPlaying = isVoicePlaying && playingVoiceId == item.id,
                    playbackProgress = if (playingVoiceId == item.id) voiceProgress else 0f,
                    onIncrement = { onIncrement(item) },
                    onToggleFavorite = { onToggleFavorite(item) },
                    onPlayVoice = { onPlayVoice(item) },
                    onStopVoice = onStopVoice
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun VoiceAthkarTab(
    items: List<ThikrEntity>,
    playingVoiceId: Int?,
    isVoicePlaying: Boolean,
    voiceProgress: Float,
    onIncrement: (ThikrEntity) -> Unit,
    onToggleFavorite: (ThikrEntity) -> Unit,
    onPlayVoice: (ThikrEntity) -> Unit,
    onStopVoice: () -> Unit,
    onDeleteVoice: (ThikrEntity) -> Unit,
    onOpenRecordDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("voice_athkar_tab")
    ) {
        // Banner with Record Action Button
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(EmeraldPrimary.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تسجيلات أذكارك بصوتك المبارك",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "محفوظة في Room لتشغيلها كأذكار صباح ومساء",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenRecordDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("record_new_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تسجيل ذكر جديد بصوتك الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (items.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "لا توجد تسجيلات صوتية حتى الآن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "اضغط على الزر أعلاه لتسجيل صوتك بتلاوة أذكار الصباح أو المساء وحفظها في قاعدة البيانات للاستماع إليها يومياً.",
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(items, key = { it.id }) { item ->
                VoiceThikrCard(
                    item = item,
                    isPlaying = isVoicePlaying && playingVoiceId == item.id,
                    playbackProgress = if (playingVoiceId == item.id) voiceProgress else 0f,
                    onPlay = { onPlayVoice(item) },
                    onStop = onStopVoice,
                    onIncrement = { onIncrement(item) },
                    onToggleFavorite = { onToggleFavorite(item) },
                    onDelete = { onDeleteVoice(item) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun VoiceThikrCard(
    item: ThikrEntity,
    isPlaying: Boolean,
    playbackProgress: Float,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onIncrement: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (isPlaying) 2.dp else 1.dp,
            color = if (isPlaying) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_thikr_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = when (item.category) {
                        "MORNING" -> EmeraldPrimary.copy(alpha = 0.15f)
                        "EVENING" -> GoldAccent.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (item.category) {
                            "MORNING" -> "🌅 أذكار الصباح بصوتك"
                            "EVENING" -> "🌇 أذكار المساء بصوتك"
                            else -> "🎙️ ذكر بصوتك"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.category) {
                            "MORNING" -> EmeraldPrimary
                            "EVENING" -> GoldDark
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "مفضل",
                            tint = if (item.isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف التسجيل",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.textArabic,
                fontSize = 17.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Audio Player Control Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = if (isPlaying) onStop else onPlay,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isPlaying) "جارٍ الاستماع لتسجيلك..." else "تسجيل بصوتك المبارك",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (item.audioDurationSeconds > 0) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d", item.audioDurationSeconds / 60, item.audioDurationSeconds % 60),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { if (isPlaying) playbackProgress else 0f },
                            color = GoldAccent,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Counter Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${item.currentCount} / ${item.targetCount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onIncrement,
                    enabled = !item.isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCompleted) EmeraldPrimary else GoldDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (item.isCompleted) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تم بحمد الله")
                    } else {
                        Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسبيح (${item.targetCount - item.currentCount} متبقي)")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduledAudibleAthkarTab(
    isAudibleEnabled: Boolean,
    isMorningScheduled: Boolean,
    morningTime: Pair<Int, Int>,
    isEveningScheduled: Boolean,
    eveningTime: Pair<Int, Int>,
    onToggleAudible: (Boolean) -> Unit,
    onUpdateMorningSchedule: (Boolean, Int, Int) -> Unit,
    onUpdateEveningSchedule: (Boolean, Int, Int) -> Unit,
    onTestMorning: () -> Unit,
    onTestEvening: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("scheduled_audible_athkar_tab")
    ) {
        // Master Audible Switch Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الصوت التلقائي المسموع للأذكار",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تشغيل صوت الأذكار تلقائياً عند حلول موعد أذكار الصباح والمساء المحدد",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isAudibleEnabled,
                        onCheckedChange = onToggleAudible,
                        modifier = Modifier.testTag("toggle_audible_athkar_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Morning Athkar Schedule Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "🌅 موعد أذكار الصباح المسموعة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "التوقيت الحالي: ${String.format(Locale.getDefault(), "%02d:%02d", morningTime.first, morningTime.second)} ص",
                                fontSize = 13.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = isMorningScheduled,
                            onCheckedChange = { onUpdateMorningSchedule(it, morningTime.first, morningTime.second) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "اختر التوقيت المفضل:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    val morningPresets = listOf(
                        Pair(5, 30),
                        Pair(6, 0),
                        Pair(6, 30),
                        Pair(7, 0),
                        Pair(8, 0)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        morningPresets.forEach { (h, m) ->
                            val isSelected = morningTime.first == h && morningTime.second == m
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateMorningSchedule(true, h, m) },
                                label = { Text(String.format(Locale.getDefault(), "%02d:%02d", h, m), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTestMorning,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تجربة تنبيه أذكار الصباح المسموعة الآن (خلال ثانيتين)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Evening Athkar Schedule Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "🌇 موعد أذكار المساء المسموعة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "التوقيت الحالي: ${String.format(Locale.getDefault(), "%02d:%02d", eveningTime.first, eveningTime.second)} م",
                                fontSize = 13.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = isEveningScheduled,
                            onCheckedChange = { onUpdateEveningSchedule(it, eveningTime.first, eveningTime.second) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "اختر التوقيت المفضل:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    val eveningPresets = listOf(
                        Pair(16, 30),
                        Pair(17, 0),
                        Pair(17, 30),
                        Pair(18, 0),
                        Pair(19, 0)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        eveningPresets.forEach { (h, m) ->
                            val isSelected = eveningTime.first == h && eveningTime.second == m
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateEveningSchedule(true, h, m) },
                                label = { Text(String.format(Locale.getDefault(), "%02d:%02d", h, m), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTestEvening,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تجربة تنبيه أذكار المساء المسموعة الآن (خلال ثانيتين)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun VoiceRecordingDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startVoiceRecording()
        } else {
            Toast.makeText(context, "يلزم إذن الميكروفون لتسجيل الأذكار بصوتك", Toast.LENGTH_LONG).show()
        }
    }

    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDurationSeconds.collectAsState()
    val amplitude by viewModel.recordingAmplitude.collectAsState()

    var thikrTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ThikrCategory.MORNING) }
    var targetCount by remember { mutableIntStateOf(1) }
    var recordedFileReady by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {
        if (isRecording) viewModel.cancelVoiceRecording()
        onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("voice_recording_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "تسجيل ذكر بصوتك المبارك",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = {
                        if (isRecording) viewModel.cancelVoiceRecording()
                        onDismiss()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Mic Button
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isRecording) 1.18f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "micPulse"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) Color.Red.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.15f)
                        )
                        .border(
                            width = if (isRecording) 3.dp else 2.dp,
                            color = if (isRecording) Color.Red else EmeraldPrimary,
                            shape = CircleShape
                        )
                        .clickable {
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                if (isRecording) {
                                    recordedFileReady = true
                                    viewModel.stopAndSaveVoiceRecording(
                                        title = if (thikrTitle.isBlank()) "ذكر صوتي (${selectedCategory.titleArabic})" else thikrTitle,
                                        category = selectedCategory,
                                        targetCount = targetCount
                                    )
                                    Toast.makeText(context, "تم حفظ الذكر الصوتي في قاعدة البيانات بنجاح!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    val file = viewModel.startVoiceRecording()
                                    if (file != null) {
                                        recordedFileReady = false
                                    }
                                }
                            }
                        }
                        .testTag("record_mic_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "إيقاف وحفظ" else "بدء التسجيل",
                        tint = if (isRecording) Color.Red else EmeraldPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer / Status Text
                Text(
                    text = if (isRecording) {
                        String.format(Locale.getDefault(), "جارٍ التسجيل: %02d:%02d 🔴", recordingDuration / 60, recordingDuration % 60)
                    } else {
                        "اضغط على الميكروفون لبدء التسجيل"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isRecording) Color.Red else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = thikrTitle,
                    onValueChange = { thikrTitle = it },
                    label = { Text("اسم أو نص الذكر (اختياري)") },
                    placeholder = { Text("مثلاً: سبحان الله وبحمده بصوتي") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category selector: Morning / Evening
                Text(text = "تصنيف الذكر:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedCategory == ThikrCategory.MORNING,
                        onClick = { selectedCategory = ThikrCategory.MORNING },
                        label = { Text("🌅 أذكار الصباح") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedCategory == ThikrCategory.EVENING,
                        onClick = { selectedCategory = ThikrCategory.EVENING },
                        label = { Text("🌇 أذكار المساء") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Target Count selector
                Text(text = "عدد التكرار المستهدف:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(1, 3, 7, 33, 100).forEach { cnt ->
                        val isSelected = targetCount == cnt
                        FilterChip(
                            selected = isSelected,
                            onClick = { targetCount = cnt },
                            label = { Text("$cnt") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = EmeraldDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isRecording) {
                    Button(
                        onClick = {
                            viewModel.stopAndSaveVoiceRecording(
                                title = if (thikrTitle.isBlank()) "ذكر صوتي (${selectedCategory.titleArabic})" else thikrTitle,
                                category = selectedCategory,
                                targetCount = targetCount
                            )
                            Toast.makeText(context, "تم حفظ الذكر الصوتي في قاعدة البيانات بنجاح!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إيقاف التسجيل وحفظ الذكر في Room")
                    }
                }
            }
        }
    }
}

@Composable
private fun ThikrEntityCard(
    item: ThikrEntity,
    isPlaying: Boolean,
    playbackProgress: Float,
    onIncrement: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayVoice: () -> Unit,
    onStopVoice: () -> Unit
) {
    val context = LocalContext.current

    val favoriteIconColor by animateColorAsState(
        targetValue = if (item.isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 300),
        label = "favoriteColor"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) {
                EmeraldPrimary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (item.isCompleted) 1.5.dp else if (item.isFavorite) 1.2.dp else 1.dp,
            color = when {
                item.isCompleted -> EmeraldPrimary
                item.isFavorite -> GoldAccent.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            }
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isFavorite) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("thikr_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Action Bar: Category Tag, Copy Button, and Favorite Star
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Chip / Badge
                Surface(
                    color = when (item.category) {
                        "MORNING" -> EmeraldPrimary.copy(alpha = 0.15f)
                        "EVENING" -> GoldAccent.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (item.category) {
                            "MORNING" -> "🌅 أذكار الصباح"
                            "EVENING" -> "🌇 أذكار المساء"
                            "AFTER_PRAYER" -> "🕌 بعد الصلاة"
                            else -> "ذكر"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.category) {
                            "MORNING" -> EmeraldPrimary
                            "EVENING" -> GoldDark
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Copy button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("ذكر", item.textArabic)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ الذكر المبارك", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("copy_thikr_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ الذكر",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Favorite Toggle Star Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("favorite_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (item.isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
                            tint = favoriteIconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thikr Arabic text (large, clear, vocalized)
            Text(
                text = item.textArabic,
                fontSize = 18.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            // Voice audio playback bar if recorded by user
            if (!item.audioFilePath.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        IconButton(
                            onClick = if (isPlaying) onStopVoice else onPlayVoice,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تسجيل بصوتك المبارك 🎙️",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            LinearProgressIndicator(
                                progress = { if (isPlaying) playbackProgress else 0f },
                                color = GoldAccent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            // Virtue Box
            if (item.virtueArabic.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = GoldAccent.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "⭐ فضل الذكر: ${item.virtueArabic}",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Reference Source
            if (item.referenceArabic.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "المصدر: ${item.referenceArabic}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            val progress = (item.currentCount.toFloat() / item.targetCount.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                color = if (item.isCompleted) EmeraldPrimary else GoldAccent,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Count Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${item.currentCount} / ${item.targetCount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onIncrement,
                    enabled = !item.isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCompleted) EmeraldPrimary else GoldDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("increment_thikr_button_${item.id}")
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "تم",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تم بحمد الله")
                    } else {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "تسبيح",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسبيح (${item.targetCount - item.currentCount} متبقي)")
                    }
                }
            }
        }
    }
}

@Composable
private fun TasbihTab(
    currentCount: Int,
    targetCount: Int,
    totalCount: Int,
    phrases: List<String>,
    selectedPhraseIndex: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSelectPhrase: (Int) -> Unit,
    onSetTarget: (Int) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tasbih_tab")
    ) {
        // Phrase selector chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(phrases.size) { index ->
                    val isSelected = index == selectedPhraseIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPhrase(index) },
                        label = { Text(phrases[index], fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Active Phrase Display
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = phrases.getOrElse(selectedPhraseIndex) { "سُبْحَانَ اللَّهِ" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Big Digital Bead Button
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                EmeraldPrimary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.surfaceVariant,
                                EmeraldDark.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(4.dp, GoldAccent, CircleShape)
                    .clickable { onIncrement() }
                    .testTag("tasbih_circle_button")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentCount",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (targetCount > 0) "الهدف: $targetCount" else "عداد مفتوح",
                        fontSize = 14.sp,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط للتسبيح",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Target Selector & Total Counts
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(33, 100, 0).forEach { target ->
                    val isSelected = targetCount == target
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetTarget(target) },
                        label = {
                            Text(if (target == 0) "مفتوح" else "$target مرة")
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = EmeraldDark
                        )
                    )
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("reset_tasbih_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تصفير الجولة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مجموع التسبيحات الإجمالي: ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalCount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun RandomAthkarTab(
    currentRandomThikr: String,
    randomAthkarEnabled: Boolean,
    intervalMinutes: Int,
    onShuffle: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectInterval: (Int) -> Unit,
    onSendTestNotification: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("random_athkar_tab")
    ) {
        // Spotlight random thikr card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✨ ذكر عشوائي للتأمل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = EmeraldPrimary
                        )
                        IconButton(
                            onClick = onShuffle,
                            modifier = Modifier.testTag("shuffle_athkar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "تغيير الذكر",
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentRandomThikr,
                        fontSize = 17.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onShuffle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إظهار ذكر آخر")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "إعدادات التذكير العشوائي التلقائي",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Toggle Switch Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تفعيل إشعارات الأذكار العشوائية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "يصلك إشعار لطيف بذكر عشوائي في أوقات متفرقة خلال اليوم",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = randomAthkarEnabled,
                        onCheckedChange = onToggleEnabled,
                        modifier = Modifier.testTag("toggle_random_athkar_switch")
                    )
                }
            }
        }

        // Interval Selection
        if (randomAthkarEnabled) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "معدل تكرار التذكير:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val intervals = listOf(
                    Pair(30, "كل 30 دقيقة"),
                    Pair(60, "كل ساعة"),
                    Pair(120, "كل ساعتين"),
                    Pair(180, "كل 3 ساعات")
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    intervals.forEach { (mins, label) ->
                        val isSelected = intervalMinutes == mins
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectInterval(mins) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Test Notification Button
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSendTestNotification,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_athkar_notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إرسال إشعار تجريبي الآن",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
