package com.virtual.phone.launcher.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class VirtualPhoneStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("virtual_phone_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SETTINGS = "vp_settings"
        private const val KEY_CONTACTS = "vp_contacts"
        private const val KEY_MESSAGES = "vp_messages"
        private const val KEY_NOTES = "vp_notes"
        private const val KEY_CALL_LOGS = "vp_call_logs"
        private const val KEY_GALLERY_ITEMS = "vp_gallery_items"
    }

    // --- Settings Persistence ---
    fun saveSettings(settings: VirtualPhoneSettings) {
        val json = JSONObject().apply {
            put("deviceName", settings.deviceName)
            put("ownerName", settings.ownerName)
            put("isWifiEnabled", settings.isWifiEnabled)
            put("isBluetoothEnabled", settings.isBluetoothEnabled)
            put("isAirplaneMode", settings.isAirplaneMode)
            put("isDarkMode", settings.isDarkMode)
            put("isSoundEnabled", settings.isSoundEnabled)
            put("brightnessPercent", settings.brightnessPercent)
            put("batteryPercent", settings.batteryPercent)
            put("isScreenLocked", settings.isScreenLocked)
            put("pinCode", settings.pinCode)
            put("isPinRequired", settings.isPinRequired)
            put("selectedWallpaper", settings.selectedWallpaper)
        }
        prefs.edit().putString(KEY_SETTINGS, json.toString()).apply()
    }

    fun loadSettings(): VirtualPhoneSettings {
        val raw = prefs.getString(KEY_SETTINGS, null) ?: return VirtualPhoneSettings()
        return try {
            val json = JSONObject(raw)
            VirtualPhoneSettings(
                deviceName = json.optString("deviceName", "Virtual Phone Pro"),
                ownerName = json.optString("ownerName", "User"),
                isWifiEnabled = json.optBoolean("isWifiEnabled", true),
                isBluetoothEnabled = json.optBoolean("isBluetoothEnabled", true),
                isAirplaneMode = json.optBoolean("isAirplaneMode", false),
                isDarkMode = json.optBoolean("isDarkMode", true),
                isSoundEnabled = json.optBoolean("isSoundEnabled", true),
                brightnessPercent = json.optInt("brightnessPercent", 85),
                batteryPercent = json.optInt("batteryPercent", 98),
                isScreenLocked = json.optBoolean("isScreenLocked", false),
                pinCode = json.optString("pinCode", "1234"),
                isPinRequired = json.optBoolean("isPinRequired", false),
                selectedWallpaper = json.optInt("selectedWallpaper", 0)
            )
        } catch (e: Exception) {
            VirtualPhoneSettings()
        }
    }

    // --- Contacts Persistence ---
    fun saveContacts(contacts: List<VirtualContact>) {
        val array = JSONArray()
        contacts.forEach { c ->
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("phoneNumber", c.phoneNumber)
                put("note", c.note)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CONTACTS, array.toString()).apply()
    }

    fun loadContacts(): List<VirtualContact> {
        val raw = prefs.getString(KEY_CONTACTS, null)
        if (raw.isNullOrEmpty()) {
            return getDefaultContacts()
        }
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<VirtualContact>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VirtualContact(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        name = obj.optString("name", "Contact"),
                        phoneNumber = obj.optString("phoneNumber", ""),
                        note = obj.optString("note", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            getDefaultContacts()
        }
    }

    private fun getDefaultContacts(): List<VirtualContact> = listOf(
        VirtualContact(id = "1", name = "Support / الدعم الفني", phoneNumber = "+1 800 555 0199", note = "خدمة العملاء"),
        VirtualContact(id = "2", name = "Emergency / الطوارئ", phoneNumber = "911", note = "رقم الطوارئ"),
        VirtualContact(id = "3", name = "Friend / صديق", phoneNumber = "+966 50 123 4567", note = "العمل")
    )

    // --- Messages Persistence ---
    fun saveMessages(messages: List<VirtualMessage>) {
        val array = JSONArray()
        messages.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("sender", m.sender)
                put("text", m.text)
                put("timestamp", m.timestamp)
                put("isIncoming", m.isIncoming)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_MESSAGES, array.toString()).apply()
    }

    fun loadMessages(): List<VirtualMessage> {
        val raw = prefs.getString(KEY_MESSAGES, null)
        if (raw.isNullOrEmpty()) {
            return getDefaultMessages()
        }
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<VirtualMessage>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VirtualMessage(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        sender = obj.optString("sender", "System"),
                        text = obj.optString("text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isIncoming = obj.optBoolean("isIncoming", true)
                    )
                )
            }
            list
        } catch (e: Exception) {
            getDefaultMessages()
        }
    }

    private fun getDefaultMessages(): List<VirtualMessage> = listOf(
        VirtualMessage(
            id = "m1",
            sender = "Virtual OS System",
            text = "مرحباً بك في نظام الهاتف الوهمي الذكي! جميع بياناتك ورسائلك وملاحظاتك تحفظ تلقائياً وبشكل دائم.",
            isIncoming = true
        ),
        VirtualMessage(
            id = "m2",
            sender = "Support / الدعم",
            text = "النظام جاهز ويدعم التوافقية لجميع المعماريات 32 بت و 64 بت.",
            isIncoming = true
        )
    )

    // --- Notes Persistence ---
    fun saveNotes(notes: List<VirtualNote>) {
        val array = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("timestamp", n.timestamp)
                put("colorTag", n.colorTag)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_NOTES, array.toString()).apply()
    }

    fun loadNotes(): List<VirtualNote> {
        val raw = prefs.getString(KEY_NOTES, null)
        if (raw.isNullOrEmpty()) {
            return getDefaultNotes()
        }
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<VirtualNote>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VirtualNote(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        title = obj.optString("title", ""),
                        content = obj.optString("content", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        colorTag = obj.optString("colorTag", "#6366F1")
                    )
                )
            }
            list
        } catch (e: Exception) {
            getDefaultNotes()
        }
    }

    private fun getDefaultNotes(): List<VirtualNote> = listOf(
        VirtualNote(
            id = "n1",
            title = "ملاحظة أولى حول الهاتف الوهمي",
            content = "هذا التطبيق يحاكي نظام هاتف ذكي متكامل مع حفظ فوري لأي تغييرات أو بيانات داخل الذاكرة الدائمة (SharedPreferences).",
            colorTag = "#6366F1"
        ),
        VirtualNote(
            id = "n2",
            title = "قائمة المهام اليومية",
            content = "- تجربة إرسال رسائل وهمية\n- تجربة إضافة جهة اتصال جديدة\n- تغيير إعدادات الهاتف والمظهر\n- استخدام الآلة الحاسبة",
            colorTag = "#10B981"
        )
    )

    // --- Call Log Persistence ---
    fun saveCallLogs(logs: List<CallLogEntry>) {
        val array = JSONArray()
        logs.forEach { log ->
            val obj = JSONObject().apply {
                put("id", log.id)
                put("number", log.number)
                put("name", log.name)
                put("timestamp", log.timestamp)
                put("durationSeconds", log.durationSeconds)
                put("type", log.type)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CALL_LOGS, array.toString()).apply()
    }

    fun loadCallLogs(): List<CallLogEntry> {
        val raw = prefs.getString(KEY_CALL_LOGS, null) ?: return listOf(
            CallLogEntry(id = "c1", number = "+1 800 555 0199", name = "Support / الدعم", type = "INCOMING", durationSeconds = 45),
            CallLogEntry(id = "c2", number = "+966 50 123 4567", name = "Friend", type = "OUTGOING", durationSeconds = 120)
        )
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<CallLogEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CallLogEntry(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        number = obj.optString("number", ""),
                        name = obj.optString("name", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        durationSeconds = obj.optInt("durationSeconds", 0),
                        type = obj.optString("type", "OUTGOING")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Gallery Items Persistence ---
    fun saveGalleryCaptures(captures: List<String>) {
        val array = JSONArray()
        captures.forEach { array.put(it) }
        prefs.edit().putString(KEY_GALLERY_ITEMS, array.toString()).apply()
    }

    fun loadGalleryCaptures(): List<String> {
        val raw = prefs.getString(KEY_GALLERY_ITEMS, null) ?: return listOf(
            "IMG_20261008_VIRTUAL_01.jpg",
            "IMG_20261008_VIRTUAL_02.jpg",
            "SCREENSHOT_HOMESCREEN.png"
        )
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            listOf("IMG_DEFAULT.jpg")
        }
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
