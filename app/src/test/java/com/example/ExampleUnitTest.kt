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
  fun chained_calculations_and_formatting() {
    // Evaluation with commas from previous result (e.g., user result was 21,672,730 and multiplies by 5)
    assertEquals("21,672,730", CalculatorEngine.evaluate("4334546 × 5", AngleMode.DEG))
    assertEquals("108,363,650", CalculatorEngine.evaluate("21,672,730 × 5", AngleMode.DEG))
    assertEquals("108,363,650", CalculatorEngine.evaluate("2,16,72,730 × 5", AngleMode.DEG))
    assertEquals("108,363,650", CalculatorEngine.evaluatePreview("21,672,730 × 5", AngleMode.DEG))
    assertEquals("108,363,650", CalculatorEngine.evaluatePreview("2,16,72,730 × 5", AngleMode.DEG))

    // Display Formatter Indian, Space, None, Comma formatting
    val indianFormatted = com.example.model.DisplayFormatter.formatNumber("21672730", com.example.model.DisplaySeparatorStyle.INDIAN)
    assertEquals("2,16,72,730", indianFormatted)

    // Formatter handles numbers that already had commas
    val indianFromComma = com.example.model.DisplayFormatter.formatNumber("21,672,730", com.example.model.DisplaySeparatorStyle.INDIAN)
    assertEquals("2,16,72,730", indianFromComma)

    val spaceFormatted = com.example.model.DisplayFormatter.formatNumber("21,672,730", com.example.model.DisplaySeparatorStyle.SPACE)
    assertEquals("21 672 730", spaceFormatted)

    val noneFormatted = com.example.model.DisplayFormatter.formatNumber("21,672,730", com.example.model.DisplaySeparatorStyle.NONE)
    assertEquals("21672730", noneFormatted)

    // Expression formatting with Indian separator
    val exprIndian = com.example.model.DisplayFormatter.formatExpression("4334546×5", com.example.model.DisplaySeparatorStyle.INDIAN)
    assertEquals("43,34,546×5", exprIndian)

    // Implicit multiplication & functions
    assertEquals("14", CalculatorEngine.evaluate("2(3+4)", AngleMode.DEG))
    assertEquals("35", CalculatorEngine.evaluate("(3+4)5", AngleMode.DEG))
    assertEquals("3", CalculatorEngine.evaluate("√9", AngleMode.DEG))
    assertEquals("6", CalculatorEngine.evaluate("2√9", AngleMode.DEG))
    assertEquals("8", CalculatorEngine.evaluate("+5 + +3", AngleMode.DEG))
    assertEquals("-8", CalculatorEngine.evaluate("-(5+3)", AngleMode.DEG))
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

