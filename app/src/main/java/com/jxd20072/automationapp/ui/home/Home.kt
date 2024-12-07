package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.LocalDateTime

@Composable
fun TopBar(
	modifier: Modifier = Modifier
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
				color = MaterialTheme.colorScheme.onBackground,
				fontSize = 32.sp
			)

			Box {
				Icon(
					painter = painterResource(R.drawable.round_settings_24),
					contentDescription = "settings",
					tint = MaterialTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(28.dp)
				)
			}
		}
	}
}

@Composable
private fun AutomationsList(
	automations: List<Automation>,
	currentTime: LocalDateTime,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		modifier = modifier
			.fillMaxHeight(),

		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		items(automations) { automation ->
			AutomationCard(
				name = automation.name,
				icon = automation.icon,
				lastUsed = getRelativeTime(automation.lastUsed, currentTime)
			)
		}

		item { Spacer(modifier = Modifier.height(300.dp)) }
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel = viewModel(),
) {
	val sidePadding = PaddingValues(horizontal = 20.dp)
	val homeUiState by viewModel.state.collectAsState()
	var showBottomSheet by remember { mutableStateOf(false) }

	Layer(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.background)
			.fillMaxSize()
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(15.dp),
			modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
		) {
			TopBar(modifier = Modifier.padding(sidePadding))

			SearchBar(
				searchText = "Search for automations",
				modifier = Modifier
					.fillMaxWidth()
					.height(45.dp)
					.padding(sidePadding)
			)

			FilterChips(
				filters = homeUiState.filters,
				sidePadding = sidePadding,
				onFilterChange = { filter, enabled ->
					viewModel.onFilterChanged(filter, enabled)
				}
			)
			AutomationsList(
				automations = homeUiState.automations,
				currentTime = homeUiState.currentTime,
				modifier = Modifier.padding(sidePadding)
			)
		}

		AddAutomationSheet(
			show = showBottomSheet,
			onDismissRequest = {
				showBottomSheet = false
			},
			modifier = Modifier.padding(sidePadding),
		) { name, icon ->
			viewModel.addAutomation(
				name = name,
				icon = icon
			)
			showBottomSheet = false
		}

		FloatingNewButton(modifier = Modifier.align(Alignment.BottomEnd)) {
			showBottomSheet = true
		}
	}
}