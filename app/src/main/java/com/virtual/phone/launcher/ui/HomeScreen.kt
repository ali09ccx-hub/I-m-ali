package com.virtual.phone.launcher.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.virtual.phone.launcher.data.VirtualApp
import com.virtual.phone.launcher.data.VirtualAppId
import com.virtual.phone.launcher.data.VirtualPhoneSettings
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    settings: VirtualPhoneSettings,
    unreadMessagesCount: Int,
    notesCount: Int,
    onLaunchApp: (VirtualAppId) -> Unit,
    onLockPhone: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFmt = SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ar"))
            val now = Date()
            currentTime = timeFmt.format(now)
            currentDate = dateFmt.format(now)
            kotlinx.coroutines.delay(1000)
        }
    }

    val appsList = remember(unreadMessagesCount, notesCount) {
        listOf(
            VirtualApp(VirtualAppId.PHONE, "الهاتف", "اتصالات", Color(0xFF10B981)),
            VirtualApp(VirtualAppId.MESSAGES, "الرسائل", "مراسلة", Color(0xFF38BDF8), badgeCount = unreadMessagesCount),
            VirtualApp(VirtualAppId.NOTES, "الملاحظات", "إنتاجية", Color(0xFFF59E0B), badgeCount = notesCount),
            VirtualApp(VirtualAppId.CAMERA, "الكاميرا", "وسائط", Color(0xFFEC4899)),
            VirtualApp(VirtualAppId.CALCULATOR, "الحاسبة", "أدوات", Color(0xFF8B5CF6)),
            VirtualApp(VirtualAppId.BROWSER, "المتصفح", "إنترنت", Color(0xFF3B82F6)),
            VirtualApp(VirtualAppId.TERMINAL, "الترمينال", "نظام", Color(0xFF06B6D4)),
            VirtualApp(VirtualAppId.SETTINGS, "الإعدادات", "نظام", Color(0xFF64748B))
        )
    }

    val filteredApps = if (searchQuery.isBlank()) {
        appsList
    } else {
        appsList.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
    }

    // Wallpaper Background
    val wallpaperBrush = when (settings.selectedWallpaper) {
        1 -> Brush.verticalGradient(listOf(Color(0xFF030712), Color(0xFF1E1B4B), Color(0xFF0F172A)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF0A0A0A), Color(0xFF171717), Color(0xFF262626)))
        else -> Brush.verticalGradient(listOf(Color(0xFF090D16), Color(0xFF1E1B4B), Color(0xFF0F172A)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(wallpaperBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))

                // Clock & Weather Widget
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.65f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentTime.ifEmpty { "12:00" },
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDate,
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WbSunny, contentDescription = "Weather", tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("28°C", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("الرياض • صافي", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Widget
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث في تطبيقات الهاتف...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF6366F1))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("launcher_search_bar"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Main App Grid (4 Columns)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredApps) { app ->
                        VirtualAppIconItem(
                            app = app,
                            onClick = { onLaunchApp(app.id) }
                        )
                    }
                }
            }

            // Bottom Section: Persistent Dock & Android Nav Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Dock Bar (Surface pill containing the 4 primary quick apps)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DockIcon(icon = Icons.Default.Call, bg = Color(0xFF10B981), tag = "dock_phone") {
                            onLaunchApp(VirtualAppId.PHONE)
                        }
                        DockIcon(icon = Icons.Default.Sms, bg = Color(0xFF38BDF8), tag = "dock_msg") {
                            onLaunchApp(VirtualAppId.MESSAGES)
                        }
                        DockIcon(icon = Icons.Default.Language, bg = Color(0xFF6366F1), tag = "dock_browser") {
                            onLaunchApp(VirtualAppId.BROWSER)
                        }
                        DockIcon(icon = Icons.Default.CameraAlt, bg = Color(0xFFEC4899), tag = "dock_camera") {
                            onLaunchApp(VirtualAppId.CAMERA)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Virtual Android Navigation Bar (Back, Home, Recents, Screen Lock)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Simulated Back */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { /* Simulated Home */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    IconButton(
                        onClick = { onLaunchApp(VirtualAppId.SETTINGS) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF94A3B8))
                        )
                    }

                    IconButton(
                        onClick = onLockPhone,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualAppIconItem(
    app: VirtualApp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("app_${app.id.name}")
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(app.iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (app.id) {
                        VirtualAppId.PHONE -> Icons.Default.Call
                        VirtualAppId.MESSAGES -> Icons.Default.Sms
                        VirtualAppId.NOTES -> Icons.Default.NoteAlt
                        VirtualAppId.CAMERA -> Icons.Default.CameraAlt
                        VirtualAppId.GALLERY -> Icons.Default.PhotoLibrary
                        VirtualAppId.CALCULATOR -> Icons.Default.Calculate
                        VirtualAppId.SETTINGS -> Icons.Default.Settings
                        VirtualAppId.BROWSER -> Icons.Default.Public
                        VirtualAppId.TERMINAL -> Icons.Default.Terminal
                        VirtualAppId.CLOCK -> Icons.Default.Schedule
                    },
                    contentDescription = app.title,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            if (app.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (app.badgeCount > 9) "9+" else "${app.badgeCount}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun DockIcon(
    icon: ImageVector,
    bg: Color,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
    }
}
