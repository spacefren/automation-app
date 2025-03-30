package com.jxd20072.automationapp.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jxd20072.automationapp.ui.editor.AutomationEditor
import com.jxd20072.automationapp.ui.editor.EditorViewModel
import com.jxd20072.automationapp.ui.home.HomeScreen
import com.jxd20072.automationapp.ui.home.HomeViewModel
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsScreen
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel

enum class AppScreen { HOME, EDITOR, SETTINGS }

val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
	dampingRatio = Spring.DampingRatioNoBouncy,
	stiffness = Spring.StiffnessLow
)

@Composable
fun AutomationApp(
	navController: NavHostController = rememberNavController()
) {
	val settingsViewModel: UserSettingsViewModel = hiltViewModel()
	val homeViewModel: HomeViewModel = hiltViewModel()
	val editorViewModel: EditorViewModel = hiltViewModel()

	fun startEditAutomation(id: String) {
		navController.navigate(route = AppScreen.EDITOR.name)
		editorViewModel.loadAutomation(id)
	}

	NavHost(
		navController = navController,
		startDestination = AppScreen.HOME.name,
		enterTransition = {
			slideIntoContainer(
				AnimatedContentTransitionScope.SlideDirection.Left,
				animationSpec = animationSpec
			)
		},
		exitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = animationSpec
			)
		},
		modifier = Modifier
			.background(LocalTheme.colorScheme.background)
	) {
		composable(
			route = AppScreen.HOME.name,
			enterTransition = {
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					initialOffset = { it / 5 },
					animationSpec = animationSpec
				)
			},
			exitTransition = {
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					targetOffset = { it / 5 },
					animationSpec = animationSpec
				)
			},
		) {
			HomeScreen(
				homeViewModel = homeViewModel,
				settingsViewModel = settingsViewModel,
				openSettings = { navController.navigate(route = AppScreen.SETTINGS.name) },
				runAutomation = {},
				editAutomation = { id -> startEditAutomation(id) },
			)
		}
		composable(
			route = AppScreen.EDITOR.name
		) {
			AutomationEditor(
				editorViewModel = editorViewModel,
				settingsViewModel = settingsViewModel,
				returnHome = { navController.popBackStack() }
			)
		}
		composable(
			route = AppScreen.SETTINGS.name
		) {
			UserSettingsScreen(
				userSettingsViewModel = settingsViewModel,
				returnHome = { navController.popBackStack() }
			)
		}
	}
}