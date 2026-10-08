package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.virtual.phone.launcher.data.VirtualNote
import com.virtual.phone.launcher.data.VirtualPhoneSettings
import com.virtual.phone.launcher.data.VirtualPhoneStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Virtual Phone", appName)
  }

  @Test
  fun `test virtual phone storage persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = VirtualPhoneStorage(context)

    // Save custom settings
    val customSettings = VirtualPhoneSettings(
      deviceName = "My Tested Virtual Phone",
      isDarkMode = false,
      brightnessPercent = 50
    )
    storage.saveSettings(customSettings)

    // Load back and verify
    val loadedSettings = storage.loadSettings()
    assertEquals("My Tested Virtual Phone", loadedSettings.deviceName)
    assertEquals(false, loadedSettings.isDarkMode)
    assertEquals(50, loadedSettings.brightnessPercent)

    // Test saving notes
    val testNotes = listOf(
      VirtualNote(id = "test_1", title = "Saved Note", content = "Test Content")
    )
    storage.saveNotes(testNotes)
    val loadedNotes = storage.loadNotes()
    assertTrue(loadedNotes.any { it.title == "Saved Note" })
  }
}
