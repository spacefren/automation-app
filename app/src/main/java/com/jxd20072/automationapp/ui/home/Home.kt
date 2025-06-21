package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.home.addAutomationSheet.AddAutomationDrawer
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.shared.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.Instant

@Composable
fun TopBar(
	modifier: Modifier = Modifier,
	openSettings: () -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = modifier.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1 * 2)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = "AutomationApp",
				color = LocalTheme.colorScheme.onBackground,
				style = LocalTheme.typography.title
			)

			Icon(
				painter = painterResource(R.drawable.round_settings_24),
				contentDescription = "settings",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier
					.size(28.dp)
					.clickableWithIndication { openSettings() }
			)
		}
	}
}

@Composable
private fun AutomationsList(
	automations: List<AutomationCardState>,
	currentTime: Instant,
	modifier: Modifier = Modifier,
	runAutomation: (id: String) -> Unit,
	editAutomation: (id: String) -> Unit
) {
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
		contentPadding = PaddingValues(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = modifier
			.fillMaxHeight()
	) {
		items(automations, { it.id }) { automation ->
			AutomationCard(
				name = automation.name,
				icon = automation.icon,
				runAutomation = { runAutomation(automation.id) },
				editAutomation = { editAutomation(automation.id) },
				lastUsed = if (automation.lastUsed != null) {
					getRelativeTime(automation.lastUsed, currentTime)
				} else "never",
				modifier = Modifier.animateItem()
			)
		}
	}
}

@Composable
fun HomeScreen(
	homeViewModel: HomeViewModel = hiltViewModel(),
	settingsViewModel: UserSettingsViewModel = hiltViewModel(),
	openSettings: () -> Unit,
	modifier: Modifier = Modifier,
	runAutomation: (id: String) -> Unit,
	editAutomation: (id: String) -> Unit,
) {
	val homeUiState by homeViewModel.homeUiState.collectAsState()
	val settingsUiState by settingsViewModel.userSettingsUiState.collectAsState()
	val currentTime by homeViewModel.currentTime.collectAsState()

	Layer(
		modifier = modifier
			.fillMaxSize()
			.background(LocalTheme.colorScheme.background)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
			modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
		) {
			TopBar(
				openSettings = openSettings,
				modifier = Modifier.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			)


			SearchBar(
				searchText = "Search automations",
				queryText = homeUiState.searchQuery,
				onSearch = { query -> homeViewModel.changeSearchQuery(query) },
				autoFocus = false,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			)

			FilterChips(
				filters = homeUiState.filters,
				sidePadding = PaddingValues(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1),
				onFilterChange = { filter, enabled ->
					homeViewModel.changeFilter(filter, enabled)
				},
			)

			AutomationsList(
				automations = homeUiState.filteredAutomations,
				currentTime = currentTime,
				runAutomation = runAutomation,
				editAutomation = editAutomation,
				modifier = Modifier.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			)
		}

		AddAutomationDrawer(
			drawerUiState = homeUiState.addAutomationDrawerState,
			autoOpenKeyboard = settingsUiState is UserSettingsUiState.Success
				&& (settingsUiState as UserSettingsUiState.Success).settings.autoOpenKeyboard.value,
			onDismissRequest = {
				homeViewModel.changeAddAutomationDrawerVisibility(false)
				homeViewModel.changeAddAutomationDrawerScreen(AddAutomationDrawerScreen.BLANK)
			},
			validateAutomationName = homeViewModel::validateAddAutomationDrawerName,
			onScreenChanged = homeViewModel::changeAddAutomationDrawerScreen,
			onIconChanged = homeViewModel::changeAddAutomationDrawerIcon,
			onNameChanged = homeViewModel::changeAddAutomationDrawerName,
			onIconSearch = homeViewModel::changeIconSelectorSearchQuery,
		) {
			homeViewModel.addAutomation(
				name = homeUiState.addAutomationDrawerState.selectedName,
				icon = homeUiState.addAutomationDrawerState.selectedIcon
			) { id ->
				homeViewModel.changeAddAutomationDrawerVisibility(false)
				homeViewModel.changeAddAutomationDrawerScreen(AddAutomationDrawerScreen.BLANK)
				editAutomation(id)
			}
		}

		Box(modifier = Modifier.fillMaxSize()) {
			FloatingNewButton(modifier = Modifier.align(Alignment.BottomEnd)) {
				homeViewModel.changeAddAutomationDrawerVisibility(true)
				homeViewModel.changeAddAutomationDrawerScreen(AddAutomationDrawerScreen.AUTOMATION_ADD)
			}
		}
	}
}