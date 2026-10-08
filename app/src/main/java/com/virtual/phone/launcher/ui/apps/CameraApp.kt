package com.virtual.phone.launcher.ui.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraApp(
    galleryItems: List<String>,
    onCapturePhoto: (String) -> Unit,
    onCloseApp: () -> Unit
) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableStateOf(0) } // 0: Auto, 1: On, 2: Off
    var showFlashEffect by remember { mutableStateOf(false) }
    var showGalleryView by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("عادي") }

    LaunchedEffect(showFlashEffect) {
        if (showFlashEffect) {
            delay(150)
            showFlashEffect = false
        }
    }

    if (showGalleryView) {
        // Gallery Screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("معرض صور الهاتف الوهمي (${galleryItems.size})", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { showGalleryView = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
                )
            },
            containerColor = Color(0xFF0F172A)
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(galleryItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .aspectRatio(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, contentDescription = "Photo", tint = Color(0xFF38BDF8), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.take(14), color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Camera Viewfinder Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Viewfinder Simulated Preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isFrontCamera) Color(0xFF1E293B) else Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isFrontCamera) Icons.Default.Face else Icons.Default.CameraAlt,
                        contentDescription = "Lens",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isFrontCamera) "كاميرا أمامية بدقة 32MP" else "عدسة رئيسية بدقة 108MP 4K",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "فلتر: $selectedFilter • التركيز التلقائي AI مفعل",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                }

                // Grid Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                )
            }

            // Flash Screen Effect
            if (showFlashEffect) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            // Top Camera Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCloseApp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconButton(
                        onClick = { flashMode = (flashMode + 1) % 3 }
                    ) {
                        Icon(
                            imageVector = when (flashMode) {
                                1 -> Icons.Default.FlashOn
                                2 -> Icons.Default.FlashOff
                                else -> Icons.Default.FlashAuto
                            },
                            contentDescription = "Flash",
                            tint = if (flashMode == 1) Color(0xFFF59E0B) else Color.White
                        )
                    }

                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera }
                    ) {
                        Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip", tint = Color.White)
                    }
                }
            }

            // Bottom Shutter & Gallery Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail Preview
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { showGalleryView = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color(0xFF38BDF8))
                }

                // Main Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            showFlashEffect = true
                            val newPhotoName = "IMG_${System.currentTimeMillis()}.jpg"
                            onCapturePhoto(newPhotoName)
                        }
                        .testTag("camera_shutter_btn")
                )

                // Filter Switcher
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val filters = listOf("عادي", "سينمائي", "طبيعي", "أبيض وأسود")
                            val nextIdx = (filters.indexOf(selectedFilter) + 1) % filters.size
                            selectedFilter = filters[nextIdx]
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FilterVintage, contentDescription = "Filter", tint = Color(0xFFEC4899))
                }
            }
        }
    }
}
