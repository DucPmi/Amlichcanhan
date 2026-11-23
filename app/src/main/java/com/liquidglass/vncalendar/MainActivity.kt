package com.liquidglass.vncalendar

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liquidglass.vncalendar.ui.DashboardViewModel
import com.liquidglass.vncalendar.ui.LiquidGlassDashboardScreen
import com.liquidglass.vncalendar.ui.theme.LiquidGlassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            val vm: DashboardViewModel = viewModel()
            val state by vm.uiState.collectAsState()

            LaunchedEffect(state.isDarkMode) {
                val style = if (state.isDarkMode) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }

            LiquidGlassTheme(darkTheme = state.isDarkMode) {
                LiquidGlassDashboardScreen(viewModel = vm)
            }
        }
    }
}
