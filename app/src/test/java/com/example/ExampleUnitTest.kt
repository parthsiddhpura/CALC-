package com.example

import com.example.domain.CalculatorEngine
import com.example.model.AngleMode
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun percentage_calculations() {
    // 7x10% as requested by user
    assertEquals("0.7", CalculatorEngine.evaluate("7*10%", AngleMode.DEG))
    assertEquals("0.7", CalculatorEngine.evaluate("7×10%", AngleMode.DEG))
    assertEquals("0.7", CalculatorEngine.evaluate("7x10%", AngleMode.DEG))

    // Previews
    assertEquals("0.7", CalculatorEngine.evaluatePreview("7×10%", AngleMode.DEG))
    assertEquals("0.7", CalculatorEngine.evaluatePreview("7*10%", AngleMode.DEG))

    // Standalone percent
    assertEquals("0.1", CalculatorEngine.evaluate("10%", AngleMode.DEG))
    assertEquals("0.5", CalculatorEngine.evaluate("50%", AngleMode.DEG))

    // Markup / discount
    assertEquals("110", CalculatorEngine.evaluate("100+10%", AngleMode.DEG))
    assertEquals("90", CalculatorEngine.evaluate("100-10%", AngleMode.DEG))
    assertEquals("80", CalculatorEngine.evaluate("100-20%", AngleMode.DEG))

    // Multiplications & divisions
    assertEquals("10", CalculatorEngine.evaluate("100*10%", AngleMode.DEG))
    assertEquals("1,000", CalculatorEngine.evaluate("100/10%", AngleMode.DEG))
    assertEquals("100", CalculatorEngine.evaluate("50/50%", AngleMode.DEG))
    assertEquals("0.2", CalculatorEngine.evaluate("(10+10)%", AngleMode.DEG))
  }

  @Test
  fun themes_allRegisteredAndValid() {
    val themes = com.example.ui.theme.CalculatorThemes.allThemes
    assertTrue("Themes count should be 49", themes.size >= 49)

    // Ensure all theme IDs are unique
    val ids = themes.map { it.id }
    assertEquals(ids.size, ids.toSet().size)

    // Ensure new image themes are present
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.NEUMORPHIC_ICE_LIGHT))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.NEUMORPHIC_MIDNIGHT_AZURE))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.OBSIDIAN_TANGERINE))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.KAWAII_CLAY_PINK))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.RETRO_MACARON_TYPEWRITER))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.INDUSTRIAL_AMBER_BEZEL))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.MIDNIGHT_OCEAN_RADIAL))
    assertNotNull(com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.MINIMAL_POWDER_BLUE))
    
    val batmanTheme = com.example.ui.theme.CalculatorThemes.getThemeById(com.example.model.ThemeId.BATMAN_DARK_KNIGHT)
    assertNotNull(batmanTheme)
    assertTrue("Batman theme should have hasBatSignal=true", batmanTheme.hasBatSignal)
    assertTrue("Batman theme should be dark", batmanTheme.isDark)
  }
}

