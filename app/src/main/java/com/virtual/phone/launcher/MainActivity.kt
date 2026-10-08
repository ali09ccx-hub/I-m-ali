package com.virtual.phone.launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.example.R
import com.virtual.phone.launcher.data.*
import com.virtual.phone.launcher.ui.*
import com.virtual.phone.launcher.ui.apps.*

open class MainActivity : ComponentActivity() {

    private lateinit var storage: VirtualPhoneStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Persistent Storage instance
        storage = VirtualPhoneStorage(this)

        // Inflate the XML layout requested in project requirements
        setContentView(R.layout.activity_main)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            VirtualPhoneRoot(storage)
        }
    }
}

@Composable
fun VirtualPhoneRoot(storage: VirtualPhoneStorage) {
    // Persistent reactive states loaded from SharedPreferences
    var settings by remember { mutableStateOf(storage.loadSettings()) }
    val contacts = remember { mutableStateListOf<VirtualContact>().apply { addAll(storage.loadContacts()) } }
    val messages = remember { mutableStateListOf<VirtualMessage>().apply { addAll(storage.loadMessages()) } }
    val notes = remember { mutableStateListOf<VirtualNote>().apply { addAll(storage.loadNotes()) } }
    val callLogs = remember { mutableStateListOf<CallLogEntry>().apply { addAll(storage.loadCallLogs()) } }
    val galleryItems = remember { mutableStateListOf<String>().apply { addAll(storage.loadGalleryCaptures()) } }

    var currentApp by remember { mutableStateOf<VirtualAppId?>(null) }
    var isControlCenterOpen by remember { mutableStateOf(false) }

    // Intercept hardware Android back button to return to Home Launcher
    BackHandler(enabled = currentApp != null || isControlCenterOpen) {
        if (isControlCenterOpen) {
            isControlCenterOpen = false
        } else {
            currentApp = null
        }
    }

    VirtualPhoneTheme(darkTheme = settings.isDarkMode) {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            containerColor = Color(0xFF090D16)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF090D16))
            ) {
                if (settings.isScreenLocked) {
                    // Lock Screen
                    LockScreen(
                        settings = settings,
                        onUnlock = {
                            settings = settings.copy(isScreenLocked = false)
                            storage.saveSettings(settings)
                        }
                    )
                } else {
                    // Active Phone OS
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Status Bar
                        VirtualStatusBar(
                            settings = settings,
                            onOpenControlCenter = { isControlCenterOpen = true }
                        )

                        // Main Content: Active App or Home Launcher
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentApp) {
                                null -> {
                                    HomeScreen(
                                        settings = settings,
                                        unreadMessagesCount = messages.count { it.isIncoming },
                                        notesCount = notes.size,
                                        onLaunchApp = { appId -> currentApp = appId },
                                        onLockPhone = {
                                            settings = settings.copy(isScreenLocked = true)
                                            storage.saveSettings(settings)
                                        }
                                    )
                                }

                                VirtualAppId.PHONE -> {
                                    DialerApp(
                                        contacts = contacts,
                                        callLogs = callLogs,
                                        onAddContact = { newContact ->
                                            contacts.add(newContact)
                                            storage.saveContacts(contacts)
                                        },
                                        onDeleteContact = { contactId ->
                                            contacts.removeAll { it.id == contactId }
                                            storage.saveContacts(contacts)
                                        },
                                        onMakeCall = { number, name ->
                                            val log = CallLogEntry(
                                                number = number,
                                                name = name,
                                                type = "OUTGOING",
                                                durationSeconds = (10..180).random()
                                            )
                                            callLogs.add(0, log)
                                            storage.saveCallLogs(callLogs)
                                        },
                                        onClearCallLogs = {
                                            callLogs.clear()
                                            storage.saveCallLogs(callLogs)
                                        },
                                        onCloseApp = { currentApp = null }
                                    )
                                }

                                VirtualAppId.MESSAGES -> {
                                    MessagesApp(
                                        messages = messages,
                                        onSendMessage = { newMsg ->
                                            messages.add(newMsg)
                                            storage.saveMessages(messages)
                                        },
                                        onClearMessages = {
                                            messages.clear()
                                            storage.saveMessages(messages)
                                        },
                                        onCloseApp = { currentApp = null }
                                    )
                                }

                                VirtualAppId.NOTES -> {
                                    NotesApp(
                                        notes = notes,
                                        onSaveNote = { note ->
                                            val index = notes.indexOfFirst { it.id == note.id }
                                            if (index >= 0) {
                                                notes[index] = note
                                            } else {
                                                notes.add(0, note)
                                            }
                                            storage.saveNotes(notes)
                                        },
                                        onDeleteNote = { noteId ->
                                            notes.removeAll { it.id == noteId }
                                            storage.saveNotes(notes)
                                        },
                                        onCloseApp = { currentApp = null }
                                    )
                                }

                                VirtualAppId.SETTINGS -> {
                                    SettingsApp(
                                        settings = settings,
                                        onUpdateSettings = { updated ->
                                            settings = updated
                                            storage.saveSettings(updated)
                                        },
                                        onResetAllData = {
                                            storage.clearAllData()
                                            settings = storage.loadSettings()
                                            contacts.clear()
                                            contacts.addAll(storage.loadContacts())
                                            messages.clear()
                                            messages.addAll(storage.loadMessages())
                                            notes.clear()
                                            notes.addAll(storage.loadNotes())
                                            callLogs.clear()
                                            callLogs.addAll(storage.loadCallLogs())
                                            galleryItems.clear()
                                            galleryItems.addAll(storage.loadGalleryCaptures())
                                        },
                                        onCloseApp = { currentApp = null }
                                    )
                                }

                                VirtualAppId.CAMERA, VirtualAppId.GALLERY -> {
                                    CameraApp(
                                        galleryItems = galleryItems,
                                        onCapturePhoto = { photoName ->
                                            galleryItems.add(0, photoName)
                                            storage.saveGalleryCaptures(galleryItems)
                                        },
                                        onCloseApp = { currentApp = null }
                                    )
                                }

                                VirtualAppId.CALCULATOR -> {
                                    CalculatorApp(onCloseApp = { currentApp = null })
                                }

                                VirtualAppId.BROWSER -> {
                                    BrowserApp(onCloseApp = { currentApp = null })
                                }

                                VirtualAppId.TERMINAL -> {
                                    TerminalApp(onCloseApp = { currentApp = null })
                                }

                                VirtualAppId.CLOCK -> {
                                    HomeScreen(
                                        settings = settings,
                                        unreadMessagesCount = messages.count { it.isIncoming },
                                        notesCount = notes.size,
                                        onLaunchApp = { appId -> currentApp = appId },
                                        onLockPhone = {
                                            settings = settings.copy(isScreenLocked = true)
                                            storage.saveSettings(settings)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pull-down Control Center Overlay
                    if (isControlCenterOpen) {
                        QuickSettingsControlCenter(
                            settings = settings,
                            onUpdateSettings = { updated ->
                                settings = updated
                                storage.saveSettings(updated)
                            },
                            onDismiss = { isControlCenterOpen = false }
                        )
                    }
                }
            }
        }
    }
}
