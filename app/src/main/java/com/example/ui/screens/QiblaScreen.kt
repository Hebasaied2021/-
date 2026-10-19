package com.example.ui.screens

import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaScreen(viewModel: MainViewModel) {
    val location by viewModel.currentLocation.collectAsState()
    val qiblaBearing by viewModel.qiblaBearing.collectAsState()
    val distanceKm by viewModel.distanceToKaabaKm.collectAsState()
    val rawSensorHeading by viewModel.compassHeading.collectAsState()
    val isAligned by viewModel.isQiblaAligned.collectAsState()

    // Interactive slider for testing / manual adjustment on emulator
    var manualHeading by remember { mutableFloatStateOf(0f) }
    var useManualSlider by remember { mutableStateOf(false) }

    val effectiveHeading = if (useManualSlider) manualHeading else rawSensorHeading

    // Angle of Kaaba relative to phone's current direction
    val relativeKaabaAngle = (qiblaBearing.toFloat() - effectiveHeading + 360f) % 360f

    val animatedRelativeAngle by animateFloatAsState(
        targetValue = relativeKaabaAngle,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "qiblaAngle"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("qibla_screen")
    ) {
        // Location & Distance Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${location.cityNameArabic}، ${location.countryNameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = GoldAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${distanceKm.roundToInt()} كم",
                                color = GoldDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        val themeMode by viewModel.themeMode.collectAsState()
                        IconButton(
                            onClick = { viewModel.toggleDarkMode() },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("qibla_theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (themeMode == com.example.model.ThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تبديل الوضع الليلي",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "زاوية القبلة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${qiblaBearing.roundToInt()}°",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = EmeraldPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "اتجاه الهاتف",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${effectiveHeading.roundToInt()}°",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المسافة للكعبة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${distanceKm.roundToInt()} km",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GoldDark,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Alignment Notification Banner
        AnimatedVisibility(visible = isAligned || (relativeKaabaAngle <= 4f || relativeKaabaAngle >= 356f)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("qibla_aligned_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "🕋",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "أنت الآن باتجاه الكعبة المشرفة بدقة!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Compass Canvas Dial
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(310.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface,
                            if (isAligned) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .border(
                    width = if (isAligned) 4.dp else 2.dp,
                    color = if (isAligned) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            val majorTickColor = MaterialTheme.colorScheme.onSurface
            val minorTickColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)

            // Rotating Compass Canvas
            Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Draw Compass ticks
                for (deg in 0 until 360 step 15) {
                    val isMajor = deg % 90 == 0
                    val isSemi = deg % 45 == 0
                    val tickLen = if (isMajor) 18f else if (isSemi) 12f else 7f
                    val strokeW = if (isMajor) 3f else 1.5f
                    val tickColor = if (isMajor) majorTickColor else minorTickColor

                    val angleRad = (deg - 90) * (PI / 180.0)
                    val startX = center.x + (radius - tickLen) * cos(angleRad).toFloat()
                    val startY = center.y + (radius - tickLen) * sin(angleRad).toFloat()
                    val endX = center.x + radius * cos(angleRad).toFloat()
                    val endY = center.y + radius * sin(angleRad).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeW
                    )
                }

                // Rotating Needle towards Qibla
                rotate(degrees = animatedRelativeAngle, pivot = center) {
                    // Needle Pointer (Green / Gold to Kaaba)
                    val needlePath = Path().apply {
                        moveTo(center.x, center.y - radius * 0.78f) // Tip pointing up
                        lineTo(center.x - 16f, center.y)
                        lineTo(center.x, center.y + radius * 0.35f) // Tail
                        lineTo(center.x + 16f, center.y)
                        close()
                    }

                    drawPath(
                        path = needlePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(GoldAccent, EmeraldPrimary)
                        )
                    )

                    // Kaaba silhouette icon at top of needle
                    val kaabaBoxSize = 34f
                    val kaabaCenterY = center.y - radius * 0.82f
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(center.x - kaabaBoxSize / 2, kaabaCenterY - kaabaBoxSize / 2),
                        size = androidx.compose.ui.geometry.Size(kaabaBoxSize, kaabaBoxSize)
                    )
                    // Kaaba golden door / band
                    drawLine(
                        color = GoldAccent,
                        start = Offset(center.x - kaabaBoxSize / 2, kaabaCenterY - kaabaBoxSize / 6),
                        end = Offset(center.x + kaabaBoxSize / 2, kaabaCenterY - kaabaBoxSize / 6),
                        strokeWidth = 4f
                    )
                }

                // Center Pin Circle
                drawCircle(
                    color = EmeraldDark,
                    radius = 16f,
                    center = center
                )
                drawCircle(
                    color = GoldAccent,
                    radius = 8f,
                    center = center
                )
            }

            // Top alignment indicator arrow
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = null,
                tint = if (isAligned) GoldAccent else EmeraldPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Calibration Tip Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RotateRight,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "نصيحة لدقة البوصلة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ضع الهاتف في وضع أفقي مستوٍ، وحركه في الهواء على شكل رقم 8 لمعايرة حساس الاتجاه.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Emulator/Desktop testing slider toggle
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "محاكاة تدوير الهاتف (للمعاينة)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    androidx.compose.material3.Switch(
                        checked = useManualSlider,
                        onCheckedChange = { useManualSlider = it }
                    )
                }

                if (useManualSlider) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تدوير الهاتف: ${manualHeading.roundToInt()}° (وجّه حتى تصل لـ ${qiblaBearing.roundToInt()}°)",
                        fontSize = 12.sp,
                        color = EmeraldPrimary
                    )
                    Slider(
                        value = manualHeading,
                        onValueChange = { manualHeading = it },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
