package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.NavTab
import com.example.ui.screens.about.AboutScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.scan.ScanScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.ScanCodeBarreTheme
import com.example.viewmodel.ScanViewModel

@Composable
fun AppNavigation(
    viewModel: ScanViewModel = viewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    ScanCodeBarreTheme(themeMode = themeMode) {
        var showSplash by remember { mutableStateOf(true) }
        var currentTab by remember { mutableStateOf<NavTab>(NavTab.Scan) }

        if (showSplash) {
            SplashScreen(
                onSplashFinished = { showSplash = false }
            )
        } else {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentTab.route,
                        onTabSelected = { tab -> currentTab = tab }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = currentTab,
                        label = "MainTabTransition"
                    ) { tab ->
                        when (tab) {
                            NavTab.Scan -> ScanScreen(viewModel = viewModel)
                            NavTab.History -> HistoryScreen(viewModel = viewModel)
                            NavTab.Settings -> SettingsScreen(viewModel = viewModel)
                            NavTab.About -> AboutScreen()
                        }
                    }
                }
            }
        }
    }
}
