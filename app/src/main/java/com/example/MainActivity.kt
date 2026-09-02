package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CalculatorMode
import com.example.ui.CalculatorScreen
import com.example.ui.theme.CalculatorThemes
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.widget.CalculatorAppWidgetProvider

class MainActivity : ComponentActivity() {
    private var pendingMode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingMode = intent?.getStringExtra(CalculatorAppWidgetProvider.EXTRA_MODE)

        setContent {
            val calculatorViewModel: CalculatorViewModel = viewModel()
            val uiState by calculatorViewModel.uiState.collectAsStateWithLifecycle()
            val theme = remember(uiState) {
                calculatorViewModel.getEffectiveTheme(uiState)
            }

            LaunchedEffect(pendingMode) {
                pendingMode?.let { modeStr ->
                    when (modeStr) {
                        "GST_CALCULATOR" -> calculatorViewModel.setMode(CalculatorMode.GST_CALCULATOR)
                        "CALCULATION_CHAINS" -> calculatorViewModel.setMode(CalculatorMode.CALCULATION_CHAINS)
                        "AI_COPILOT" -> calculatorViewModel.setMode(CalculatorMode.AI_COPILOT)
                        "CUSTOM_BUILDER" -> calculatorViewModel.setMode(CalculatorMode.CUSTOM_BUILDER)
                        "STANDARD" -> calculatorViewModel.setMode(CalculatorMode.STANDARD)
                    }
                    pendingMode = null
                }
            }

            MyApplicationTheme(
                darkTheme = theme.isDark,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = theme.backgroundColor
                ) {
                    CalculatorScreen(viewModel = calculatorViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val modeStr = intent.getStringExtra(CalculatorAppWidgetProvider.EXTRA_MODE)
        if (modeStr != null) {
            pendingMode = modeStr
        }
    }
}

