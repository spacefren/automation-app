package com.jxd20072.automationapp.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
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
import com.jxd20072.automationapp.ui.userSettings.UserSettingsScreen
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel

enum class AppScreen { HOME, EDITOR, SETTINGS }

val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
	dampingRatio = Spring.DampingRatioNoBouncy,
	stiffness = Spring.StiffnessLow
)

val animationSpec_f: FiniteAnimationSpec<Float> = spring(
	dampingRatio = Spring.DampingRatioNoBouncy,
	stiffness = Spring.StiffnessLow
)

fun pageEnterTransition(): EnterTransition {
	return slideInHorizontally(
		animationSpec = animationSpec,
		initialOffsetX = { it }
	)
}

fun pageExitTransition(): ExitTransition {
	return slideOutHorizontally(
		animationSpec = animationSpec,
		targetOffsetX = { it }
	)
}

fun homeEnterTransition(): EnterTransition {
	return slideInHorizontally(
		animationSpec = animationSpec,
		initialOffsetX = { -it }
	)
}

fun homeExitTransition(): ExitTransition {
	return slideOutHorizontally(
		animationSpec = animationSpec,
		targetOffsetX = { -it }
	)
}

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
		modifier = Modifier.background(MaterialTheme.colorScheme.background)
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
				openSettings = { navController.navigate(route = AppScreen.SETTINGS.name) },
				runAutomation = {},
				editAutomation = { id -> startEditAutomation(id) },
			)
		}
		composable(
			route = AppScreen.EDITOR.name,
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
		) {
			AutomationEditor(
				editorViewModel = editorViewModel,
				returnHome = { navController.popBackStack() }
			)
		}
		composable(
			route = AppScreen.SETTINGS.name,
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
		) {
			UserSettingsScreen(
				userSettingsViewModel = settingsViewModel,
				returnHome = { navController.popBackStack() }
			)
		}
	}
}