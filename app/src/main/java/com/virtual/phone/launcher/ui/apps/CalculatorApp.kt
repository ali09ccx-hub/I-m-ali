package com.virtual.phone.launcher.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp(onCloseApp: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) }
    var showHistory by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || display == "Error") {
            display = d
        } else {
            display += d
        }
    }

    fun onOperator(op: String) {
        expression = "$display $op "
        display = "0"
    }

    fun onEquals() {
        if (expression.isNotBlank()) {
            val parts = expression.trim().split(" ")
            if (parts.size >= 2) {
                val num1 = parts[0].toDoubleOrNull() ?: 0.0
                val op = parts[1]
                val num2 = display.toDoubleOrNull() ?: 0.0

                val result = when (op) {
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    "×" -> num1 * num2
                    "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
                    else -> num2
                }

                val resultText = if (result.isNaN()) {
                    "Error"
                } else if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    String.format("%.4f", result).trimEnd('0').trimEnd('.')
                }

                val historyEntry = "$expression$display = $resultText"
                history = listOf(historyEntry) + history
                display = resultText
                expression = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الحاسبة الذكية", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCloseApp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = if (showHistory) Color(0xFF6366F1) else Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showHistory) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("سجل العمليات الحسابية:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (history.isEmpty()) {
                            Text("لا توجد عمليات سابقة", color = Color(0xFF64748B), fontSize = 12.sp)
                        } else {
                            LazyColumn {
                                items(history) { item ->
                                    Text(item, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    color = Color(0xFF94A3B8),
                    fontSize = 20.sp,
                    maxLines = 1
                )
                Text(
                    text = display,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            // Keypad
            val buttons = listOf(
                listOf("AC" to Color(0xFFEF4444), "+/-" to Color(0xFF475569), "%" to Color(0xFF475569), "÷" to Color(0xFF6366F1)),
                listOf("7" to Color(0xFF1E293B), "8" to Color(0xFF1E293B), "9" to Color(0xFF1E293B), "×" to Color(0xFF6366F1)),
                listOf("4" to Color(0xFF1E293B), "5" to Color(0xFF1E293B), "6" to Color(0xFF1E293B), "-" to Color(0xFF6366F1)),
                listOf("1" to Color(0xFF1E293B), "2" to Color(0xFF1E293B), "3" to Color(0xFF1E293B), "+" to Color(0xFF6366F1)),
                listOf("0" to Color(0xFF1E293B), "." to Color(0xFF1E293B), "⌫" to Color(0xFF475569), "=" to Color(0xFF10B981))
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                for (row in buttons) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for ((text, color) in row) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        when (text) {
                                            "AC" -> {
                                                display = "0"
                                                expression = ""
                                            }
                                            "+/-" -> {
                                                if (display != "0" && display != "Error") {
                                                    display = if (display.startsWith("-")) display.drop(1) else "-$display"
                                                }
                                            }
                                            "%" -> {
                                                val n = display.toDoubleOrNull() ?: 0.0
                                                display = (n / 100.0).toString()
                                            }
                                            "⌫" -> {
                                                display = if (display.length > 1) display.dropLast(1) else "0"
                                            }
                                            "=" -> onEquals()
                                            "+", "-", "×", "÷" -> onOperator(text)
                                            else -> onDigit(text)
                                        }
                                    }
                                    .testTag("calc_btn_$text"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
