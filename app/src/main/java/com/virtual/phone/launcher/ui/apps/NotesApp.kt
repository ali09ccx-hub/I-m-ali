package com.virtual.phone.launcher.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtual.phone.launcher.data.VirtualNote
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(
    notes: List<VirtualNote>,
    onSaveNote: (VirtualNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCloseApp: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<VirtualNote?>(null) }
    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#6366F1") }

    val colors = listOf("#6366F1", "#10B981", "#F59E0B", "#EC4899", "#3B82F6")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ملاحظات الهاتف الوهمي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("حفظ دائم لا يضيع أبداً", color = Color(0xFF10B981), fontSize = 12.sp)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    titleInput = ""
                    contentInput = ""
                    selectedColor = "#6366F1"
                    showDialog = true
                },
                containerColor = Color(0xFF6366F1),
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
            }
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد ملاحظات محفوظة.\nاضغط + لإضافة أول ملاحظة!",
                    color = Color(0xFF94A3B8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes) { note ->
                    val dateFormatted = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(note.timestamp))
                    val cardColor = try {
                        Color(android.graphics.Color.parseColor(note.colorTag))
                    } catch (e: Exception) {
                        Color(0xFF6366F1)
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                editingNote = note
                                titleInput = note.title
                                contentInput = note.content
                                selectedColor = note.colorTag
                                showDialog = true
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(cardColor, shape = RoundedCornerShape(5.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = note.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editingNote = note
                                            titleInput = note.title
                                            contentInput = note.content
                                            selectedColor = note.colorTag
                                            showDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteNote(note.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.content,
                                color = Color(0xFFCBD5E1),
                                fontSize = 14.sp,
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = dateFormatted,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = if (editingNote == null) "ملاحظة جديدة" else "تعديل الملاحظة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("عنوان الملاحظة") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_title_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            label = { Text("نص الملاحظة") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("note_content_input"),
                            maxLines = 6
                        )
                        Text("اختر لون الملاحظة:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            colors.forEach { hex ->
                                val parsed = Color(android.graphics.Color.parseColor(hex))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            parsed,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedColor = hex }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                val saved = VirtualNote(
                                    id = editingNote?.id ?: System.currentTimeMillis().toString(),
                                    title = titleInput.trim(),
                                    content = contentInput.trim(),
                                    timestamp = System.currentTimeMillis(),
                                    colorTag = selectedColor
                                )
                                onSaveNote(saved)
                                showDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.testTag("save_note_confirm_btn")
                    ) {
                        Text("حفظ دائم")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("إلغاء", color = Color(0xFF94A3B8))
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}
