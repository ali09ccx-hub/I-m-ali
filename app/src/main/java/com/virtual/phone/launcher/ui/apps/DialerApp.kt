package com.virtual.phone.launcher.ui.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtual.phone.launcher.data.CallLogEntry
import com.virtual.phone.launcher.data.VirtualContact
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerApp(
    contacts: List<VirtualContact>,
    callLogs: List<CallLogEntry>,
    onAddContact: (VirtualContact) -> Unit,
    onDeleteContact: (String) -> Unit,
    onMakeCall: (String, String) -> Unit,
    onClearCallLogs: () -> Unit,
    onCloseApp: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Keypad, 1: Contacts, 2: Recents
    var dialNumber by remember { mutableStateOf("") }
    var isCallActive by remember { mutableStateOf(false) }
    var activeCallName by remember { mutableStateOf("") }
    var activeCallNumber by remember { mutableStateOf("") }
    var callSeconds by remember { mutableIntStateOf(0) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            callSeconds = 0
            while (isCallActive) {
                kotlinx.coroutines.delay(1000)
                callSeconds++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        if (isCallActive) {
            // Calling Screen Simulator
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (activeCallName.isNotBlank()) activeCallName else activeCallNumber,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "جاري الاتصال... ${String.format("%02d:%02d", callSeconds / 60, callSeconds % 60)}",
                        color = Color(0xFF10B981),
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            isCallActive = false
                            onMakeCall(activeCallNumber, activeCallName)
                        },
                        containerColor = Color(0xFFEF4444),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                0 -> "الهاتف (Dialer)"
                                1 -> "جهات الاتصال (${contacts.size})"
                                else -> "سجل المكالمات (${callLogs.size})"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onCloseApp) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        if (selectedTab == 1) {
                            IconButton(onClick = { showAddContactDialog = true }) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact", tint = Color(0xFF38BDF8))
                            }
                        } else if (selectedTab == 2 && callLogs.isNotEmpty()) {
                            IconButton(onClick = onClearCallLogs) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = Color(0xFFEF4444))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E293B)
                    )
                )

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF6366F1)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("لوحة المفاتيح", color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8)) },
                        icon = { Icon(Icons.Default.Dialpad, contentDescription = "Dialpad") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("الأسماء", color = if (selectedTab == 1) Color.White else Color(0xFF94A3B8)) },
                        icon = { Icon(Icons.Default.Contacts, contentDescription = "Contacts") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("السجل", color = if (selectedTab == 2) Color.White else Color(0xFF94A3B8)) },
                        icon = { Icon(Icons.Default.History, contentDescription = "Recents") }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Keypad Screen
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Number Display
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dialNumber.ifEmpty { "أدخل الرقم" },
                                    color = if (dialNumber.isEmpty()) Color(0xFF64748B) else Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            // Dialpad Grid
                            val keys = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("*", "0", "#")
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                for (row in keys) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        for (key in row) {
                                            KeypadButton(
                                                text = key,
                                                onClick = { dialNumber += key }
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Action Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.size(56.dp))

                                FloatingActionButton(
                                    onClick = {
                                        if (dialNumber.isNotBlank()) {
                                            val matchedContact = contacts.find { it.phoneNumber == dialNumber }
                                            activeCallName = matchedContact?.name ?: ""
                                            activeCallNumber = dialNumber
                                            isCallActive = true
                                        }
                                    },
                                    containerColor = Color(0xFF10B981),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .testTag("dial_call_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (dialNumber.isNotEmpty()) {
                                            dialNumber = dialNumber.dropLast(1)
                                        }
                                    },
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Contacts List
                        if (contacts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("لا توجد جهات اتصال محفوظة", color = Color(0xFF94A3B8))
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(contacts) { contact ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF1E293B)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF6366F1)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = contact.name.take(1).uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = contact.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = contact.phoneNumber,
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 14.sp
                                                )
                                                if (contact.note.isNotBlank()) {
                                                    Text(
                                                        text = contact.note,
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    activeCallName = contact.name
                                                    activeCallNumber = contact.phoneNumber
                                                    isCallActive = true
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Call,
                                                    contentDescription = "Call Contact",
                                                    tint = Color(0xFF10B981)
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteContact(contact.id) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Contact",
                                                    tint = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Call Logs List
                        if (callLogs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("سجل المكالمات فارغ", color = Color(0xFF94A3B8))
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(callLogs) { log ->
                                    val dateStr = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(log.timestamp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (log.type == "INCOMING") Icons.Default.CallReceived else Icons.Default.CallMade,
                                                contentDescription = "Call Type",
                                                tint = if (log.type == "INCOMING") Color(0xFF10B981) else Color(0xFF38BDF8),
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = log.name.ifEmpty { log.number },
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Text(
                                                    text = "${log.number} • $dateStr",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    activeCallName = log.name
                                                    activeCallNumber = log.number
                                                    isCallActive = true
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Call,
                                                    contentDescription = "Call Again",
                                                    tint = Color(0xFF10B981)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Contact Dialog
        if (showAddContactDialog) {
            var nameInput by remember { mutableStateOf("") }
            var phoneInput by remember { mutableStateOf("") }
            var noteInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                title = { Text("إضافة جهة اتصال جديدة", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("الاسم") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_name_input")
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("رقم الهاتف") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_phone_input")
                        )
                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = { noteInput = it },
                            label = { Text("ملاحظة (اختياري)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                                onAddContact(
                                    VirtualContact(
                                        name = nameInput.trim(),
                                        phoneNumber = phoneInput.trim(),
                                        note = noteInput.trim()
                                    )
                                )
                                showAddContactDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.testTag("save_contact_btn")
                    ) {
                        Text("حفظ دائم")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
                        Text("إلغاء", color = Color(0xFF94A3B8))
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
