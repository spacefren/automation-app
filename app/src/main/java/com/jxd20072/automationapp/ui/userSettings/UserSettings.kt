package com.jxd20072.automationapp.ui.userSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.userSettings.categories.GeneralSettingsCategory

@Composable
fun UserSettingsScreen(
	userSettingsViewModel: UserSettingsViewModel = hiltViewModel(),
	returnHome: () -> Unit
) {
	val userSettingsUiState by
	userSettingsViewModel.userSettingsUiState.collectAsStateWithLifecycle()

	Layer(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		Column(
			modifier = Modifier
				.windowInsetsPadding(WindowInsets.statusBars)
				.padding(top = 30.dp, start = 20.dp, end = 20.dp)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painter = painterResource(R.drawable.round_arrow_back_24),
						contentDescription = "back",
						tint = MaterialTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(28.dp)
							.clickable { returnHome() }
					)
					Spacer(modifier = Modifier.width(10.dp))
					Text(
						text = "Settings",
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 32.sp
					)
				}
				Icon(
					painter = painterResource(R.drawable.round_loop_24),
					contentDescription = "settings",
					tint = MaterialTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(28.dp)
						.clickable { userSettingsViewModel.resetSettingsToDefault() }
				)
			}
			Spacer(modifier = Modifier.height(20.dp))
			if (userSettingsUiState is UserSettingsUiState.Success) {
				val settings = userSettingsUiState as UserSettingsUiState.Success

				Column(
					verticalArrangement = Arrangement.spacedBy(30.dp),
					modifier = Modifier
						.verticalScroll(rememberScrollState(0))
						.padding(top = 10.dp)
				) {
					GeneralSettingsCategory(
						userSettingsUiState = settings,
						onAutoSaveChanged = userSettingsViewModel::setAutoSaveEnabled,
						openAutomationsFolder = userSettingsViewModel::openAutomationsFolder
					)
				}
			}
		}
	}
}