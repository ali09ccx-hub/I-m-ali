package com.virtual.phone.launcher.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtual.phone.launcher.data.VirtualPhoneSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsApp(
    settings: VirtualPhoneSettings,
    onUpdateSettings: (VirtualPhoneSettings) -> Unit,
    onResetAllData: () -> Unit,
    onCloseApp: () -> Unit
) {
    var showDeviceNameDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("إعدادات الهاتف الوهمي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("نظام تشغيل ذكي متكامل", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseApp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Architecture Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = "Chip", tint = Color(0xFF6366F1), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("معمارية النظام (Architecture)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("32-bit & 64-bit Compatible (Full Support)", color = Color(0xFF10B981), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "تم تضمين فلاتر المعمارية armeabi-v7a (32-bit) و arm64-v8a (64-bit) في إعدادات البناء Gradle لضمان التصدير والعمل على كافة أنواع الأجهزة الذكية.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Connectivity Section
            item {
                SettingsSectionHeader("الاتصال والشبكات")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column {
                        SettingsToggleItem(
                            icon = Icons.Default.Wifi,
                            title = "الشبكة اللاسلكية (Wi-Fi)",
                            subtitle = if (settings.isWifiEnabled) "متصل بشبكة Virtual-5G" else "غير مفعّل",
                            isChecked = settings.isWifiEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isWifiEnabled = it)) }
                        )
                        HorizontalDivider(color = Color(0xFF334155))
                        SettingsToggleItem(
                            icon = Icons.Default.Bluetooth,
                            title = "البلوتوث (Bluetooth)",
                            subtitle = if (settings.isBluetoothEnabled) "مفعّل وجاهز للاقتران" else "معطل",
                            isChecked = settings.isBluetoothEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isBluetoothEnabled = it)) }
                        )
                        HorizontalDivider(color = Color(0xFF334155))
                        SettingsToggleItem(
                            icon = Icons.Default.AirplanemodeActive,
                            title = "وضع الطيران (Airplane Mode)",
                            subtitle = if (settings.isAirplaneMode) "جميع الاتصالات معطلة" else "متصل بالشبكة",
                            isChecked = settings.isAirplaneMode,
                            onCheckedChange = { onUpdateSettings(settings.copy(isAirplaneMode = it)) }
                        )
                    }
                }
            }

            // Display & Theme Section
            item {
                SettingsSectionHeader("الشاشة والمظهر")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Brightness6, contentDescription = "Brightness", tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("سطوع الشاشة: ${settings.brightnessPercent}%", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                        Slider(
                            value = settings.brightnessPercent.toFloat(),
                            onValueChange = { onUpdateSettings(settings.copy(brightnessPercent = it.toInt())) },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFF59E0B),
                                activeTrackColor = Color(0xFFF59E0B)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsToggleItem(
                            icon = Icons.Default.DarkMode,
                            title = "الوضع الداكن (Dark Mode)",
                            subtitle = if (settings.isDarkMode) "توفير الطاقة ومريح للعين" else "الوضع الفاتح",
                            isChecked = settings.isDarkMode,
                            onCheckedChange = { onUpdateSettings(settings.copy(isDarkMode = it)) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("خلفية شاشة الهاتف الوهمي:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val wallpapers = listOf("النيون الحديث", "الفضاء الأزرق", "السيبير الأسود")
                            wallpapers.forEachIndexed { idx, name ->
                                Button(
                                    onClick = { onUpdateSettings(settings.copy(selectedWallpaper = idx)) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (settings.selectedWallpaper == idx) Color(0xFF6366F1) else Color(0xFF334155)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(name, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Device Info & Security
            item {
                SettingsSectionHeader("الأمان ومعلومات الهاتف")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeviceNameDialog = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = "Device", tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("اسم الهاتف الوهمي", color = Color.White, fontWeight = FontWeight.Medium)
                                Text(settings.deviceName, color = Color(0xFF94A3B8), fontSize = 13.sp)
                            }
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF64748B))
                        }
                        HorizontalDivider(color = Color(0xFF334155))
                        SettingsToggleItem(
                            icon = Icons.Default.Lock,
                            title = "رمز قفل الشاشة (PIN)",
                            subtitle = if (settings.isPinRequired) "الرمز الحالي: ${settings.pinCode}" else "القفل غير مفعّل",
                            isChecked = settings.isPinRequired,
                            onCheckedChange = {
                                if (it) {
                                    showPinDialog = true
                                } else {
                                    onUpdateSettings(settings.copy(isPinRequired = false))
                                }
                            }
                        )
                    }
                }
            }

            // Storage & Reset
            item {
                SettingsSectionHeader("إدارة التخزين الدائم")
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = "Storage", tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("المساحة المستخدمة في الهاتف الوهمي", color = Color.White, fontWeight = FontWeight.Medium)
                                Text("ذاكرة دائمة: SharedPreferences (JSON)", color = Color(0xFF10B981), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.22f },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("28.4 MB من إجمالي 128 GB مستخدمة", color = Color(0xFF94A3B8), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showResetConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("factory_reset_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = "Reset", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إعادة ضبط المصنع ومسح البيانات")
                        }
                    }
                }
            }
        }

        // Device Name Dialog
        if (showDeviceNameDialog) {
            var tempName by remember { mutableStateOf(settings.deviceName) }
            AlertDialog(
                onDismissRequest = { showDeviceNameDialog = false },
                title = { Text("تغيير اسم الهاتف الوهمي", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("اسم الجهاز") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                onUpdateSettings(settings.copy(deviceName = tempName.trim()))
                                showDeviceNameDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeviceNameDialog = false }) { Text("إلغاء", color = Color(0xFF94A3B8)) }
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        // PIN Dialog
        if (showPinDialog) {
            var tempPin by remember { mutableStateOf(settings.pinCode) }
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("تعيين رمز PIN لقفل الهاتف", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = tempPin,
                        onValueChange = { if (it.length <= 6) tempPin = it },
                        label = { Text("رمز القفل (4-6 أرقام)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempPin.length >= 4) {
                                onUpdateSettings(settings.copy(isPinRequired = true, pinCode = tempPin))
                                showPinDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("تفعيل القفل")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) { Text("إلغاء", color = Color(0xFF94A3B8)) }
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        // Reset Dialog
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = { Text("تأكيد إعادة الضبط", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
                text = { Text("هل أنت متأكد من مسح جميع الملاحظات والرسائل والأسماء والإعدادات المحفوظة؟", color = Color.White) },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetAllData()
                            showResetConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("نعم، مسح الكل")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) { Text("تراجع", color = Color(0xFF94A3B8)) }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF94A3B8),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1)
            )
        )
    }
}
