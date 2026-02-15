package com.jxd20072.automationapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jxd20072.automationapp.ui.editor.AutomationEditor
import com.jxd20072.automationapp.ui.home.HomeScreen
import com.jxd20072.automationapp.ui.home.HomeViewModel
import com.jxd20072.automationapp.ui.settings.SettingsScreen
import com.jxd20072.automationapp.ui.settings.SettingsViewModel
import com.jxd20072.automationapp.ui.shared.specs.navPopPredictiveTransitionSpec
import com.jxd20072.automationapp.ui.shared.specs.navPopTransitionSpec
import com.jxd20072.automationapp.ui.shared.specs.navTransitionSpec
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data class Editor(
    val automationId: String,
) : NavKey

@Composable
fun AutomationApp() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = navTransitionSpec,
        popTransitionSpec = navPopTransitionSpec,
        predictivePopTransitionSpec = navPopPredictiveTransitionSpec,
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    openSettings = { backStack.add(Settings) },
                    runAutomation = {},
                    editAutomation = { id ->
                        backStack.add(Editor(id))
                    },
                )
            }
            entry<Settings> {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    returnHome = { if (backStack.size > 1) backStack.removeLastOrNull() }
                )
            }
            entry<Editor> {
                AutomationEditor(
                    settingsViewModel = settingsViewModel,
                    automationId = it.automationId,
                    returnHome = { if (backStack.size > 1) backStack.removeLastOrNull() }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(LocalTheme.colorScheme.background),
    )
}