package com.virtual.phone.launcher.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WebBookmark(val title: String, val url: String, val category: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserApp(onCloseApp: () -> Unit) {
    var urlInput by remember { mutableStateOf("https://google.com") }
    var currentUrl by remember { mutableStateOf("https://google.com") }
    var pageTitle by remember { mutableStateOf("Google Search Engine") }
    var isLoading by remember { mutableStateOf(false) }

    val bookmarks = listOf(
        WebBookmark("Google", "https://google.com", "محرك بحث"),
        WebBookmark("GitHub", "https://github.com", "مستودعات وبرمجة"),
        WebBookmark("Wikipedia", "https://wikipedia.org", "موسوعة المعرفة"),
        WebBookmark("Android Developers", "https://developer.android.com", "تطوير التطبيقات"),
        WebBookmark("Tech News / أخبار التقنية", "https://news.ycombinator.com", "أخبار تقنية")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("browser_url_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "SSL", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                currentUrl = if (urlInput.startsWith("http")) urlInput else "https://$urlInput"
                                pageTitle = "نتيجة البحث: $urlInput"
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Go", tint = Color(0xFF6366F1))
                            }
                        }
                    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Simulated Web Page Content
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = "Web", tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pageTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currentUrl, color = Color(0xFF94A3B8), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "محاكي متصفح الويب الآمن داخل الهاتف الوهمي. يدعم تصفح المواقع الذكية والبحث السريع مع نظام تشغيل افتراضي خفيف وسريع الاستجابة.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Bookmarks / Fast Links
            Text(
                text = "المواقع الأكثر زيارة والمفضلة:",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                urlInput = item.url
                                currentUrl = item.url
                                pageTitle = item.title
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Bookmark", tint = Color(0xFF6366F1))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(item.url, color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
