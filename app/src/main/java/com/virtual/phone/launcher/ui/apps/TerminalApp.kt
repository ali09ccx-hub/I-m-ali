package com.virtual.phone.launcher.ui.apps

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalApp(onCloseApp: () -> Unit) {
    var commandInput by remember { mutableStateOf("") }
    val logs = remember {
        mutableStateListOf(
            "Virtual Phone Shell v1.0.0 (Android-Linux)",
            "Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString(", ")}",
            "Type 'help' to view available system commands.",
            "------------------------------------------------"
        )
    }
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    fun executeCommand(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isEmpty()) return

        logs.add("$ virtual-os# $trimmed")
        when (trimmed.lowercase(Locale.ROOT)) {
            "help" -> {
                logs.add("Available commands:")
                logs.add("  arch       - Display CPU architecture (armeabi-v7a / arm64-v8a)")
                logs.add("  sysinfo    - Phone OS specs, hardware, and kernel info")
                logs.add("  storage    - Check persistent storage status")
                logs.add("  date       - Print current date and time")
                logs.add("  ping       - Ping virtual gateway")
                logs.add("  whoami     - Current user session")
                logs.add("  clear      - Clear terminal screen")
            }
            "arch" -> {
                logs.add("Primary Architecture: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"}")
                logs.add("All Configured ABIs: [armeabi-v7a, arm64-v8a, x86, x86_64]")
                logs.add("Binary status: 32-bit & 64-bit ABI filters active")
            }
            "sysinfo" -> {
                logs.add("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                logs.add("Android Release: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                logs.add("Board / Hardware: ${Build.BOARD} / ${Build.HARDWARE}")
                logs.add("Virtual Phone Launcher: Production Ready")
            }
            "storage" -> {
                logs.add("Database Engine: Android SharedPreferences (Persistent)")
                logs.add("Location: /data/data/com.aistudio.vphone.../shared_prefs")
                logs.add("Status: HEALTHY (No data loss upon app exit)")
            }
            "date" -> {
                logs.add("Current Time: ${Date()}")
            }
            "ping" -> {
                logs.add("PING 127.0.0.1: 64 bytes, icmp_seq=1 ttl=64 time=0.04 ms")
                logs.add("PING 127.0.0.1: 64 bytes, icmp_seq=2 ttl=64 time=0.03 ms")
            }
            "whoami" -> {
                logs.add("root@virtual-phone-os")
            }
            "clear" -> {
                logs.clear()
            }
            else -> {
                logs.add("sh: command not found: $trimmed. Type 'help' for commands.")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terminal / موجه الأوامر", color = Color(0xFF10B981), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCloseApp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { logs.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090D16))
            )
        },
        containerColor = Color(0xFF05070C)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                items(logs) { line ->
                    Text(
                        text = line,
                        color = if (line.startsWith("$")) Color(0xFF38BDF8) else Color(0xFF10B981),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Surface(
                color = Color(0xFF090D16),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$ ", color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("terminal_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF10B981),
                            unfocusedTextColor = Color(0xFF10B981),
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF334155)
                        )
                    )
                    IconButton(
                        onClick = {
                            executeCommand(commandInput)
                            commandInput = ""
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Run", tint = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}
