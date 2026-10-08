package com.virtual.phone.launcher.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class VirtualAppId {
    PHONE,
    MESSAGES,
    CAMERA,
    GALLERY,
    NOTES,
    CALCULATOR,
    SETTINGS,
    BROWSER,
    TERMINAL,
    CLOCK
}

data class VirtualApp(
    val id: VirtualAppId,
    val title: String,
    val category: String,
    val iconColor: Color,
    val isDock: Boolean = false,
    val badgeCount: Int = 0
)

data class VirtualContact(
    val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val phoneNumber: String,
    val note: String = ""
)

data class VirtualMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isIncoming: Boolean = false
)

data class VirtualNote(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorTag: String = "#6366F1"
)

data class CallLogEntry(
    val id: String = System.currentTimeMillis().toString(),
    val number: String,
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val type: String = "OUTGOING" // OUTGOING, MISSED, INCOMING
)

data class VirtualPhoneSettings(
    val deviceName: String = "Virtual Phone Pro",
    val ownerName: String = "User",
    val isWifiEnabled: Boolean = true,
    val isBluetoothEnabled: Boolean = true,
    val isAirplaneMode: Boolean = false,
    val isDarkMode: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val brightnessPercent: Int = 85,
    val batteryPercent: Int = 98,
    val isScreenLocked: Boolean = false,
    val pinCode: String = "1234",
    val isPinRequired: Boolean = false,
    val selectedWallpaper: Int = 0 // 0 = Abstract Neon, 1 = Deep Space, 2 = Cyber Minimal
)
