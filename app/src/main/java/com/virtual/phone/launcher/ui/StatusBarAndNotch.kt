package com.virtual.phone.launcher.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtual.phone.launcher.data.VirtualPhoneSettings
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VirtualStatusBar(
    settings: VirtualPhoneSettings,
    onOpenControlCenter: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            currentTime = format.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onOpenControlCenter() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left: Time & Notifications dot
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentTime.ifEmpty { "12:00" },
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
        }

        // Center: Dynamic Island / Camera Punch Hole
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
                .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                )
                Text(
                    text = "V-PHONE",
                    color = Color(0xFF64748B),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Right: Telemetry (Network, Battery, Sound)
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (settings.isAirplaneMode) {
                Icon(Icons.Default.AirplanemodeActive, contentDescription = "Airplane", tint = Color.White, modifier = Modifier.size(13.dp))
            } else {
                Text("5G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                if (settings.isWifiEnabled) {
                    Icon(Icons.Default.Wifi, contentDescription = "Wi-Fi", tint = Color.White, modifier = Modifier.size(13.dp))
                }
            }
            if (settings.isBluetoothEnabled) {
                Icon(Icons.Default.Bluetooth, contentDescription = "BT", tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${settings.batteryPercent}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Battery",
                    tint = if (settings.batteryPercent > 20) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun QuickSettingsControlCenter(
    settings: VirtualPhoneSettings,
    onUpdateSettings: (VirtualPhoneSettings) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() }
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clickable(enabled = false) {}
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("مركز التحكم السريع", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(settings.deviceName, color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Toggles Grid (Wi-Fi, Bluetooth, Airplane, Sound)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ControlCenterTile(
                        icon = Icons.Default.Wifi,
                        label = "Wi-Fi",
                        isActive = settings.isWifiEnabled,
                        onClick = { onUpdateSettings(settings.copy(isWifiEnabled = !settings.isWifiEnabled)) }
                    )
                    ControlCenterTile(
                        icon = Icons.Default.Bluetooth,
                        label = "Bluetooth",
                        isActive = settings.isBluetoothEnabled,
                        onClick = { onUpdateSettings(settings.copy(isBluetoothEnabled = !settings.isBluetoothEnabled)) }
                    )
                    ControlCenterTile(
                        icon = Icons.Default.AirplanemodeActive,
                        label = "Airplane",
                        isActive = settings.isAirplaneMode,
                        onClick = { onUpdateSettings(settings.copy(isAirplaneMode = !settings.isAirplaneMode)) }
                    )
                    ControlCenterTile(
                        icon = if (settings.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        label = if (settings.isSoundEnabled) "صوت" else "صامت",
                        isActive = settings.isSoundEnabled,
                        onClick = { onUpdateSettings(settings.copy(isSoundEnabled = !settings.isSoundEnabled)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brightness Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Brightness6, contentDescription = "Brightness", tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = settings.brightnessPercent.toFloat(),
                        onValueChange = { onUpdateSettings(settings.copy(brightnessPercent = it.toInt())) },
                        valueRange = 10f..100f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFF59E0B),
                            activeTrackColor = Color(0xFFF59E0B)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Architecture Notification Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Shield", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("تأمين المعمارية: 32 بت و 64 بت جاهز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("armeabi-v7a & arm64-v8a • الحفظ الدائم مفعل", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ControlCenterTile(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isActive) Color(0xFF6366F1) else Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) Color.White else Color(0xFF94A3B8),
            fontSize = 11.sp
        )
    }
}
