package com.jxd20072.automationapp.ui.userSettings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.categories.GeneralSettingsCategory
import kotlinx.coroutines.launch

@Composable
fun UserSettingsScreen(
	userSettingsViewModel: UserSettingsViewModel = hiltViewModel(),
	returnHome: () -> Unit
) {
	val coroutineScope = rememberCoroutineScope()

	val userSettingsUiState by
	userSettingsViewModel.userSettingsUiState.collectAsStateWithLifecycle()

	val resetIconRotation = remember { Animatable(0f) }

	Layer(
		modifier = Modifier
			.fillMaxSize()
			.background(LocalTheme.colorScheme.background)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
			modifier = Modifier
				.windowInsetsPadding(WindowInsets.statusBars)
				.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1)
				.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2)
				) {
					Icon(
						painter = painterResource(R.drawable.round_arrow_back_24),
						contentDescription = "back",
						tint = LocalTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(28.dp)
							.clickableWithIndication { returnHome() }
					)
					Text(
						text = "Settings",
						color = LocalTheme.colorScheme.onBackground,
						style = LocalTheme.typography.title
					)
				}
				Icon(
					painter = painterResource(R.drawable.round_loop_24),
					contentDescription = "settings",
					tint = LocalTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(28.dp)
						.rotate(resetIconRotation.value)
						.clickableWithIndication {
							userSettingsViewModel.resetSettingsToDefault()
							coroutineScope.launch {
								resetIconRotation.animateTo(
									-360f,
									animationSpec = spring(
										dampingRatio = 0.6f,
										stiffness = 80f
									)
								)
								resetIconRotation.snapTo(0f)
							}
						}
				)
			}
			if (userSettingsUiState is UserSettingsUiState.Success) {
				val settings = userSettingsUiState as UserSettingsUiState.Success

				Column(
					verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
					modifier = Modifier
						.verticalScroll(rememberScrollState(0))
				) {
					GeneralSettingsCategory(
						userSettingsUiState = settings,
						onAutoSaveChanged = userSettingsViewModel::setAutoSaveEnabled,
						onAutoOpenKeyboardChanged = userSettingsViewModel::setAutoOpenKeyboard,
						openAutomationsFolder = userSettingsViewModel::openAutomationsFolder
					)
				}
			}
		}
	}
}