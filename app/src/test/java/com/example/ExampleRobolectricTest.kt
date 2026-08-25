package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ThemeId
import com.example.ui.theme.CalculatorThemes
import com.example.ui.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        assertEquals("CALC +", appName)
    }

    @Test
    fun `all themes in palette are valid and accessible`() {
        assertTrue(CalculatorThemes.allThemes.size >= 24)
        ThemeId.entries.forEach { themeId ->
            val theme = CalculatorThemes.getThemeById(themeId)
            assertNotNull(theme)
            assertEquals(themeId, theme.id)
            assertTrue(theme.name.isNotBlank())
        }
    }

    @Test
    fun `viewModel can switch themes and cycle them`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CalculatorViewModel(app)

        vm.setTheme(ThemeId.CYBERPUNK)
        assertEquals(ThemeId.CYBERPUNK, vm.uiState.value.currentThemeId)

        vm.cycleNextTheme()
        val nextTheme = vm.uiState.value.currentThemeId
        assertTrue(nextTheme != ThemeId.CYBERPUNK)

        vm.cyclePrevTheme()
        assertEquals(ThemeId.CYBERPUNK, vm.uiState.value.currentThemeId)

        vm.setTheme(ThemeId.GAMEBOY_8BIT)
        assertEquals(ThemeId.GAMEBOY_8BIT, vm.uiState.value.currentThemeId)
        assertEquals("Game Boy 1989", vm.currentTheme.name)
    }

    @Test
    fun `theme persists across app close and reopen`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        
        // First app session: user selects Retro Synthwave
        val firstSessionVm = CalculatorViewModel(app)
        firstSessionVm.setTheme(ThemeId.SYNTHWAVE)
        assertEquals(ThemeId.SYNTHWAVE, firstSessionVm.uiState.value.currentThemeId)

        // Simulate app closing and reopening (new ViewModel instance in new session)
        val secondSessionVm = CalculatorViewModel(app)
        assertEquals(ThemeId.SYNTHWAVE, secondSessionVm.uiState.value.currentThemeId)
        assertEquals("Synthwave 80s", secondSessionVm.currentTheme.name)

        // Switch to GameBoy in second session
        secondSessionVm.setTheme(ThemeId.GAMEBOY_8BIT)
        assertEquals(ThemeId.GAMEBOY_8BIT, secondSessionVm.uiState.value.currentThemeId)

        // Third session verify
        val thirdSessionVm = CalculatorViewModel(app)
        assertEquals(ThemeId.GAMEBOY_8BIT, thirdSessionVm.uiState.value.currentThemeId)
    }
}
