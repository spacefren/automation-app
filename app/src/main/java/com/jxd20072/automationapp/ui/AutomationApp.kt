package com.jxd20072.automationapp.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jxd20072.automationapp.ui.home.HomeScreen
import com.jxd20072.automationapp.ui.home.HomeViewModel
import com.jxd20072.automationapp.ui.userSettings.UserSettingsScreen
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel

enum class AppScreen { HOME, EDITOR, SETTINGS }

val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
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
	NavHost(
		navController = navController,
		startDestination = AppScreen.HOME.name,
	) {
		composable(
			route = AppScreen.HOME.name,
			enterTransition = { homeEnterTransition() },
			exitTransition = { homeExitTransition() },
			popEnterTransition = { homeEnterTransition() },
			popExitTransition = { homeExitTransition() },
		) {
			HomeScreen(
				homeViewModel = homeViewModel,
				openSettings = { navController.navigate(route = AppScreen.SETTINGS.name) },
				openAutomation = { }
			)
		}
		composable(route = AppScreen.EDITOR.name) {

		}
		composable(
			route = AppScreen.SETTINGS.name,
			enterTransition = { pageEnterTransition() },
			exitTransition = { pageExitTransition() },
			popEnterTransition = { pageEnterTransition() },
			popExitTransition = { pageExitTransition() },
		) {
			UserSettingsScreen(
				userSettingsViewModel = settingsViewModel,
				returnHome = { navController.navigate(route = AppScreen.HOME.name) }
			)
		}
	}
}