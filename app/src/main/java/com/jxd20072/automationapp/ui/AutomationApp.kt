package com.jxd20072.automationapp.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jxd20072.automationapp.ui.editor.AutomationEditor
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorViewModel
import com.jxd20072.automationapp.ui.home.HomeScreen
import com.jxd20072.automationapp.ui.home.HomeViewModel
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsScreen
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel
import kotlinx.serialization.Serializable

val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
	dampingRatio = Spring.DampingRatioNoBouncy,
	stiffness = Spring.StiffnessMediumLow
)

@Serializable
data object Home : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data object Editor : NavKey

@Composable
fun AutomationApp() {
	val settingsViewModel: UserSettingsViewModel = hiltViewModel()
	val homeViewModel: HomeViewModel = hiltViewModel()
	val editorViewModel: EditorViewModel = hiltViewModel()

	val backStack = rememberNavBackStack(Home)

	NavDisplay(
		backStack = backStack,
		modifier = Modifier.background(LocalTheme.colorScheme.background),
		transitionSpec = {
			ContentTransform(
				targetContentEnter = slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					initialOffset = { it / 3 },
					animationSpec = animationSpec
				) + fadeIn(),
				initialContentExit = slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					targetOffset = { it / 5 },
					animationSpec = animationSpec
				)
			)
		},
		popTransitionSpec = {
			ContentTransform(
				targetContentEnter = slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					initialOffset = { it / 5 },
					animationSpec = animationSpec
				),
				initialContentExit = slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					targetOffset = { it / 3 },
					animationSpec = animationSpec
				) + fadeOut()
			)
		},
		predictivePopTransitionSpec = {
			ContentTransform(
				targetContentEnter = slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					initialOffset = { it / 5 },
					animationSpec = animationSpec
				),
				initialContentExit = slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					targetOffset = { it / 3 },
					animationSpec = animationSpec
				) + fadeOut()
			)
		},
		entryProvider = entryProvider {
			entry<Home> {
				HomeScreen(
					homeViewModel = homeViewModel,
					settingsViewModel = settingsViewModel,
					openSettings = { backStack.add(Settings) },
					runAutomation = {},
					editAutomation = { id ->
						editorViewModel.automationManager.loadAutomation(id)
						backStack.add(Editor)
					},
				)
			}
			entry<Settings> {
				UserSettingsScreen(
					userSettingsViewModel = settingsViewModel,
					returnHome = { backStack.removeLastOrNull() }
				)
			}
			entry<Editor> {
				AutomationEditor(
					editorViewModel = editorViewModel,
					settingsViewModel = settingsViewModel,
					returnHome = { backStack.removeLastOrNull() }
				)
			}
		}
	)
}