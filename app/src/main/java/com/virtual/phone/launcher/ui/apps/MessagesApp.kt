package com.virtual.phone.launcher.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtual.phone.launcher.data.VirtualMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesApp(
    messages: List<VirtualMessage>,
    onSendMessage: (VirtualMessage) -> Unit,
    onClearMessages: () -> Unit,
    onCloseApp: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("الرسائل النصية (SMS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("محفوظة في الذاكرة الدائمة", color = Color(0xFF10B981), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseApp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (messages.isNotEmpty()) {
                        IconButton(onClick = onClearMessages) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All Messages", tint = Color(0xFFEF4444))
                        }
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
        ) {
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isUser = !msg.isIncoming
                    val dateFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            color = if (isUser) Color(0xFF6366F1) else Color(0xFF1E293B),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (msg.isIncoming) {
                                    Text(
                                        text = msg.sender,
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormatted,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            // Message Input Field
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("اكتب رسالة...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sms_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val textToSend = inputText.trim()
                            if (textToSend.isNotBlank()) {
                                val outgoingMsg = VirtualMessage(
                                    sender = "You / أنا",
                                    text = textToSend,
                                    isIncoming = false
                                )
                                onSendMessage(outgoingMsg)
                                inputText = ""

                                // Auto-reply simulation for interactive realism!
                                scope.launch {
                                    delay(1200)
                                    val replies = listOf(
                                        "تم استلام رسالتك وحفظها بنجاح في نظام الهاتف الوهمي!",
                                        "مرحباً! الهاتف الوهمي يعمل بكفاءة مع التخزين الدائم SharedPreferences.",
                                        "رسالتك '$textToSend' محفوظة في قاعدة بيانات الهاتف.",
                                        "أهلاً بك! المعمارية الحالية arm64-v8a / armeabi-v7a مهيأة بنجاح."
                                    )
                                    val replyMsg = VirtualMessage(
                                        sender = "Virtual Phone Bot",
                                        text = replies.random(),
                                        isIncoming = true
                                    )
                                    onSendMessage(replyMsg)
                                }
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF6366F1), shape = RoundedCornerShape(12.dp))
                            .size(50.dp)
                            .testTag("sms_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
