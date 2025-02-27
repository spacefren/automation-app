package com.jxd20072.automationapp.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.home.addAutomationSheet.AddAutomationDrawer
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.Instant

@Composable
fun TopBar(
	modifier: Modifier = Modifier,
	openSettings: () -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp),
		modifier = modifier
			.padding(top = 30.dp)
	) {

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Home",
				color = LocalTheme.colorScheme.onBackground,
				fontSize = 32.sp
			)

			Icon(
				painter = painterResource(R.drawable.round_settings_24),
				contentDescription = "settings",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier
					.size(28.dp)
					.clickable { openSettings() }
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
		modifier = modifier
			.fillMaxHeight(),
	) {
		item { Spacer(modifier = Modifier.height(2.dp)) }
		items(automations, { it.id }) { automation ->
			AutomationCard(
				name = automation.name,
				icon = automation.icon,
				lastUsed = if (automation.lastUsed != null) {
					getRelativeTime(automation.lastUsed, currentTime)
				} else "never",
				modifier = Modifier.animateItem(
					fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
					fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
				),
				runAutomation = { runAutomation(automation.id) },
				editAutomation = { editAutomation(automation.id) }
			)
			Spacer(modifier = Modifier.height(10.dp))
		}

		item { Spacer(modifier = Modifier.height(300.dp)) }
	}
}

@Composable
fun HomeScreen(
	homeViewModel: HomeViewModel = hiltViewModel(),
	openSettings: () -> Unit,
	runAutomation: (id: String) -> Unit,
	editAutomation: (id: String) -> Unit,
) {
	val sidePadding = PaddingValues(horizontal = 20.dp)
	val homeUiState by homeViewModel.homeUiState.collectAsState()
	val currentTime by homeViewModel.currentTime.collectAsState()

	Layer(
		modifier = Modifier
			.fillMaxSize()
			.background(LocalTheme.colorScheme.background)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(15.dp),
			modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
		) {
			TopBar(
				modifier = Modifier.padding(sidePadding),
				openSettings = openSettings
			)

			SearchBar(
				searchText = "Search for automations",
				queryText = homeUiState.searchQuery,
				onSearch = { query -> homeViewModel.changeSearchQuery(query) },
				modifier = Modifier
					.fillMaxWidth()
					.height(45.dp)
					.padding(sidePadding),
			)

			FilterChips(
				filters = homeUiState.filters,
				sidePadding = sidePadding,
				onFilterChange = { filter, enabled ->
					homeViewModel.changeFilter(filter, enabled)
				}
			)

			AutomationsList(
				automations = homeUiState.filteredAutomations,
				currentTime = currentTime,
				modifier = Modifier.padding(sidePadding),
				runAutomation = runAutomation,
				editAutomation = editAutomation
			)
		}

		AddAutomationDrawer(
			drawerUiState = homeUiState.addAutomationDrawerState,
			onDismissRequest = {
				homeViewModel.changeAddAutomationDrawerVisibility(false)
			},
			modifier = Modifier.padding(sidePadding),
			validateAutomationName = homeViewModel::validateAddAutomationDrawerName,
			onScreenChanged = homeViewModel::changeAddAutomationDrawerScreen,
			onIconChanged = homeViewModel::changeAddAutomationDrawerIcon,
			onNameChanged = homeViewModel::changeAddAutomationDrawerName,
			onIconSearch = homeViewModel::changeIconSelectorSearchQuery
		) {
			homeViewModel.addAutomation(
				name = homeUiState.addAutomationDrawerState.selectedName,
				icon = homeUiState.addAutomationDrawerState.selectedIcon
			) { id ->
				homeViewModel.changeAddAutomationDrawerVisibility(false)
				editAutomation(id)
			}
		}

		Box(modifier = Modifier.fillMaxSize()) {
			FloatingNewButton(modifier = Modifier.align(Alignment.BottomEnd)) {
				homeViewModel.changeAddAutomationDrawerVisibility(true)
			}
		}
	}
}