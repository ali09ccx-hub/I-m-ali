package com.virtual.phone.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import com.virtual.phone.launcher.data.VirtualPhoneSettings
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LockScreen(
    settings: VirtualPhoneSettings,
    onUnlock: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Lock Icon & Time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentTime.ifEmpty { "12:00" },
                    color = Color.White,
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = currentDate,
                    color = Color(0xFF94A3B8),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = settings.deviceName,
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // PIN Input or Swipe-to-Unlock
            if (settings.isPinRequired) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = if (pinError) "رمز PIN غير صحيح!" else "أدخل رمز PIN لفتح الهاتف",
                        color = if (pinError) Color(0xFFEF4444) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 6) {
                                pinInput = it
                                pinError = false
                                if (it == settings.pinCode) {
                                    onUnlock()
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .testTag("lock_pin_input")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (pinInput == settings.pinCode) {
                                onUnlock()
                            } else {
                                pinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("unlock_button")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = "Unlock")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("فتح الهاتف")
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 48.dp)
                ) {
                    Button(
                        onClick = onUnlock,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("unlock_slide_button")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = "Unlock", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("انقر لفتح الهاتف الوهمي", color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
